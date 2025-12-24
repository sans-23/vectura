package com.vectura.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectura.core.domain.GeoCoordinate;
import com.vectura.core.domain.SpatialNode;
import com.vectura.core.topology.NetworkGraph;

/**
 * A concrete implementation of {@link GraphSource} for the OpenStreetMap (OSM) JSON format.
 * <p>
 * Design Rationale:
 * Handles the ETL (Extract, Transform, Load) logic specific to the OSM 
 * Overpass API schema, mapping raw JSON elements to the internal {@link com.vectura.core.domain} 
 * models.
 * </p>
 */
public class OpenStreetMapLoader implements GraphSource {

    @Override
    public NetworkGraph load(InputStream stream) {
        NetworkGraph graph = new NetworkGraph();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(stream);
            JsonNode features = root.get("features");

            if (features == null || !features.isArray()) {
                throw new IllegalArgumentException("Invalid GeoJSON: 'features' array missing");
            }

            for (JsonNode feature : features) processFeature(feature, graph);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse GeoJSON stream", e);
        }

        return graph;
    }

    private void processFeature(JsonNode feature, NetworkGraph graph){
        JsonNode geometry = feature.get("geometry");
        if(geometry==null) return;

        String type = geometry.get("type").asText();

        // filter LineString to keep the data clean
        if("LineString".equals(type)){
            processPath(geometry.get("coordinates"), graph);
        }
    }

    private void processPath(JsonNode coord, NetworkGraph graph) {
        if(coord.size()<2) return;

        List<SpatialNode> pathNodes = new ArrayList<>();
        
        for(JsonNode point: coord){
            // GeoJSON format is [Longitude, Latitude]
            double lon = point.get(0).asDouble();
            double lat = point.get(1).asDouble();

            String nodeId = String.format("%.6f,%.6f", lat, lon);

            SpatialNode node = new SpatialNode(nodeId, new GeoCoordinate(lat, lon));
            pathNodes.add(node);
            graph.addNode(node);
        }

        for(int i=0; i<pathNodes.size()-1; i++){
            SpatialNode from = pathNodes.get(i);
            SpatialNode to = pathNodes.get(i+1);
            graph.addEdge(to, from);
            graph.addEdge(from, to);
        }
    }
}