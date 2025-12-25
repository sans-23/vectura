package com.vectura.server;

import java.io.InputStream;

import com.vectura.core.engine.DijkstraRouter;
import com.vectura.core.engine.Route;
import com.vectura.core.engine.RoutingEngine;
import com.vectura.core.io.OpenStreetMapLoader;
import com.vectura.core.topology.NetworkGraph;

public class App 
{
    public static void main( String[] args )
    {   
        // Get the file from 'src/main/resources'
        String filename = "/bengaluru.geojson"; 
        InputStream mapStream = App.class.getResourceAsStream(filename);

        if (mapStream == null) {
            System.err.println("❌ Error: Could not find file " + filename + " in resources!");
            System.exit(1);
        }

        // Initialize the Engine
        System.out.println("🗺️  Loading Map Data...");
        OpenStreetMapLoader loader = new OpenStreetMapLoader();

        long startTime = System.currentTimeMillis();
        NetworkGraph graph = loader.load(mapStream);
        long endTime = System.currentTimeMillis();


        // Print Stats (Proof it works)
        System.out.println("✅ Graph Loaded Successfully in " + (endTime - startTime) + "ms");
        System.out.println("------------------------------------------------");
        System.out.println("   📍 Total Nodes (Intersections): " + graph.nodeCount());
        System.out.println("   🛣️  Total Edges (Road Segments): " + graph.edgeCount());
        System.out.println("------------------------------------------------");


        if (graph.nodeCount() > 0) {
            // Pick first and last node from the graph for a test run
            var nodes = graph.getAllNodes();
            var start = nodes.get(0);
            var end = nodes.get(Math.min(172330, nodes.size() - 1)); // Pick a node somewhat far away

            System.out.println("🚗 Calculating Route: " + start.id() + " -> " + end.id());

            RoutingEngine router = new DijkstraRouter();
            Route result = router.findRoute(graph, start, end);

            if (result.path().isEmpty()) {
                System.out.println("⚠️ No path found");
            } else {
                System.out.println("✅ Path Found!");
                System.out.println("   Distance: " + String.format("%.2f km", result.totalDistanceKm()));
                
                // --- ADD THIS LINE ---
                GeoJsonDebugger.exportRoute(result, "debug_route.json");
                // ---------------------
            }
        }
    }
}
