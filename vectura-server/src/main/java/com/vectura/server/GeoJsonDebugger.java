package com.vectura.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vectura.core.domain.SpatialNode;
import com.vectura.core.engine.Route;

import java.io.File;
import java.io.IOException;

public class GeoJsonDebugger {

    public static void exportRoute(Route route, String filename) {
        ObjectMapper mapper = new ObjectMapper();

        // Create the Root "FeatureCollection"
        ObjectNode root = mapper.createObjectNode();
        root.put("type", "FeatureCollection");
        ArrayNode features = root.putArray("features");

        // Create the "Feature" (The Route Line)
        ObjectNode feature = features.addObject();
        feature.put("type", "Feature");
        
        // Style it Red so it pops out on the map
        ObjectNode properties = feature.putObject("properties");
        properties.put("stroke", "#ff0000");
        properties.put("stroke-width", 4);
        properties.put("distance_km", route.totalDistanceKm());

        // Create the Geometry "LineString"
        ObjectNode geometry = feature.putObject("geometry");
        geometry.put("type", "LineString");
        ArrayNode coordinates = geometry.putArray("coordinates");

        // Add every node in the path
        for (SpatialNode node : route.path()) {
            ArrayNode point = coordinates.addArray();
            // IMPORTANT: GeoJSON is [Longitude, Latitude]
            point.add(node.coordinate().longitude());
            point.add(node.coordinate().latitude());
        }

        // Write to file
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), root);
            System.out.println("💾 Route exported to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}