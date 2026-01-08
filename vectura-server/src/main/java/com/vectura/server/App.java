package com.vectura.server;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import com.vectura.core.domain.GeoCoordinate;
import com.vectura.core.domain.SpatialNode;
import com.vectura.core.engine.DijkstraRouter;
import com.vectura.core.engine.Route;
import com.vectura.core.engine.RoutingEngine;
import com.vectura.core.io.OpenStreetMapLoader;
import com.vectura.core.topology.NetworkGraph;

public class App {
    public static void main(String[] args) {
        String filename = "/bengaluru.geojson";
        InputStream mapStream = App.class.getResourceAsStream(filename);

        if (mapStream == null) {
            System.err.println("❌ Error: Could not find file " + filename);
            System.exit(1);
        }

        System.out.println("🗺️  Loading Map Data...");
        OpenStreetMapLoader loader = new OpenStreetMapLoader();
        long startTime = System.currentTimeMillis();
        NetworkGraph graph = loader.load(mapStream);
        long endTime = System.currentTimeMillis();

        System.out.println("✅ Graph Loaded in " + (endTime - startTime) + "ms");
        System.out.println("   Nodes: " + graph.nodeCount());
        System.out.println("   Edges: " + graph.edgeCount());

        if (graph.nodeCount() > 0) {
            // Snap nodes to the graph (these could be warehouses, depots, etc.)
            var start = new SpatialNode(1000001L, new GeoCoordinate(12.9716, 77.5946)); // Near MG Road
            var end = new SpatialNode(1000002L, new GeoCoordinate(12.9352, 77.6245)); // Near Koramangala

            graph.snapNode(start);
            graph.snapNode(end);

            System.out.println("🚗 Calculating Route...");
            RoutingEngine router = new DijkstraRouter();
            Route result = router.findRoute(graph, start, end);

            if (!result.path().isEmpty()) {
                System.out.println("✅ Path Found: " + String.format("%.2f km", result.totalDistanceKm()));

                // 1. Export Data (creates debug_route.json AND debug_route.js)
                String exportName = "debug_route.json";
                GeoJsonDebugger.exportRoute(result, exportName);

                // 2. Open Browser Automatically
                openBrowserViewer();
            } else {
                System.out.println("⚠️ No path found.");
            }
        }
    }

    // Helper to open the HTML file
    private static void openBrowserViewer() {
        try {
            File htmlFile = new File("render.html");
            System.out.println(htmlFile);
            Desktop.getDesktop().browse(htmlFile.toURI());
            if (htmlFile.exists() && Desktop.isDesktopSupported()) {
                System.out.println("🌍 Opening visualization in browser...");

            } else {
                System.out.println("⚠️ Cannot open browser automatically. Manually open viewer.html");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}