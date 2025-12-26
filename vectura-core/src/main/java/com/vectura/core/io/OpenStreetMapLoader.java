package com.vectura.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectura.core.domain.GeoCoordinate;
import com.vectura.core.domain.SpatialNode;
import com.vectura.core.topology.NetworkGraph;

/**
 * A concrete implementation of {@link GraphSource} for the OpenStreetMap (OSM)
 * JSON format.
 * <p>
 * Design Rationale:
 * Handles the ETL (Extract, Transform, Load) logic specific to the OSM
 * Overpass API schema, mapping raw JSON elements to the internal
 * {@link com.vectura.core.domain}
 * models.
 * </p>
 */
public class OpenStreetMapLoader implements GraphSource {

    private long nextId = 0;
    private final Map<GeoCoordinate, Long> coordinateIndex = new HashMap<>();

    @Override
    public NetworkGraph load(InputStream stream) {
        NetworkGraph graph = new NetworkGraph();
        ObjectMapper mapper = new ObjectMapper();

        // Reset index for each load to keep IDs small/dense for a single graph
        nextId = 0;
        coordinateIndex.clear();

        try {
            JsonNode root = mapper.readTree(stream);
            JsonNode features = root.get("features");

            if (features == null || !features.isArray()) {
                throw new IllegalArgumentException("Invalid GeoJSON: 'features' array missing");
            }

            for (JsonNode feature : features)
                processFeature(feature, graph);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse GeoJSON stream", e);
        }

        return graph;
    }

    private void processFeature(JsonNode feature, NetworkGraph graph) {
        JsonNode geometry = feature.get("geometry");
        JsonNode oneway = feature.get("properties").get("oneway");
        String surface = feature.get("properties").get("surface").asText();
        boolean isOneWay = false;

        if (geometry == null)
            return;

        if (oneway != null){
            isOneWay = oneway.asText().equals("yes");
        }

        String type = geometry.get("type").asText();

        // filter LineString to keep the data clean
        if ("LineString".equals(type)) {
            processPath(geometry.get("coordinates"), graph, isOneWay, surface);
        }
    }

    private void processPath(JsonNode coord, NetworkGraph graph, boolean isOneWay, String surface) {
        if (coord.size() < 2)
            return;

        List<SpatialNode> pathNodes = new ArrayList<>();

        for (JsonNode point : coord) {
            // GeoJSON format is [Longitude, Latitude]
            double lon = point.get(0).asDouble();
            double lat = point.get(1).asDouble();

            GeoCoordinate geo = new GeoCoordinate(lat, lon);

            // Deduplicate/Intern nodes based on coordinate
            long nodeId;
            if (coordinateIndex.containsKey(geo)) {
                nodeId = coordinateIndex.get(geo);
            } else {
                nodeId = nextId++;
                coordinateIndex.put(geo, nodeId);
            }

            SpatialNode node = new SpatialNode(nodeId, geo);
            pathNodes.add(node);
            graph.addNode(node);
        }

        for (int i = 0; i < pathNodes.size() - 1; i++) {
            SpatialNode from = pathNodes.get(i);
            SpatialNode to = pathNodes.get(i + 1);
            if(!isOneWay)
                graph.addEdge(to, from, surface);
            graph.addEdge(from, to, surface);
        }
    }
}