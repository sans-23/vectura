package com.vectura.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vectura.core.domain.SpatialNode;
import com.vectura.core.engine.Route;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeoJsonDebugger {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void exportRoute(Route route, String filename) {
        ObjectNode geoJson = buildGeoJson(route);
        try {
            // 1. Write standard JSON (for drag-and-drop or other tools)
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), geoJson);
            System.out.println("💾 Route exported to: " + filename);

            // 2. Write JS file for Auto-Opening (The Trick)
            // We strip the extension and append .js
            String jsFilename = filename.replace(".json", "") + ".js";
            String jsonString = mapper.writeValueAsString(geoJson);
            String jsContent = "window.ROUTE_DATA = " + jsonString + ";";
            
            try (FileWriter writer = new FileWriter(jsFilename)) {
                writer.write(jsContent);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ObjectNode buildGeoJson(Route route) {
        ObjectNode root = mapper.createObjectNode();
        root.put("type", "FeatureCollection");
        ArrayNode features = root.putArray("features");

        ObjectNode feature = features.addObject();
        feature.put("type", "Feature");
        
        ObjectNode properties = feature.putObject("properties");
        properties.put("stroke", "#ff0000");
        properties.put("stroke-width", 4);
        properties.put("distance_km", route.totalDistanceKm());

        ObjectNode geometry = feature.putObject("geometry");
        geometry.put("type", "LineString");
        ArrayNode coordinates = geometry.putArray("coordinates");

        for (SpatialNode node : route.path()) {
            ArrayNode point = coordinates.addArray();
            point.add(node.coordinate().longitude());
            point.add(node.coordinate().latitude());
        }
        return root;
    }
}