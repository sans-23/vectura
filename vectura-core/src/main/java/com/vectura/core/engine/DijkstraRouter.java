package com.vectura.core.engine;

import com.vectura.core.domain.SpatialNode;
import com.vectura.core.domain.TransportEdge;
import com.vectura.core.topology.NetworkGraph;

import java.util.*;

public class DijkstraRouter implements RoutingEngine {

    @Override
    public Route findRoute(NetworkGraph graph, SpatialNode start, SpatialNode end) {
        // 1. Setup Data Structures
        // Distance map: Stores the cheapest known cost to reach each node
        Map<SpatialNode, Double> distances = new HashMap<>();
        // Parent map: Stores "Where did I come from?" to reconstruct the path later
        Map<SpatialNode, SpatialNode> predecessors = new HashMap<>();
        // Priority Queue: Always gives us the next closest node to process
        PriorityQueue<NodeWrapper> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));

        // 2. Initialization
        distances.put(start, 0.0);
        pq.add(new NodeWrapper(start, 0.0));

        Set<SpatialNode> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeWrapper current = pq.poll();
            SpatialNode u = current.node;

            // Optimization: If we found the destination, we can stop early
            if (u.equals(end)) {
                return reconstructPath(predecessors, end, distances.get(end));
            }

            if (visited.contains(u)) continue;
            visited.add(u);

            // 3. Relax Edges (Check neighbors)
            for (TransportEdge edge : graph.getOutboundEdges(u.id())) {
                if (!edge.isOpen()) continue; // Skip closed roads (Resilience check)

                SpatialNode v = edge.getTarget();
                double newDist = distances.get(u) + edge.getCost();

                if (newDist < distances.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    distances.put(v, newDist);
                    predecessors.put(v, u);
                    pq.add(new NodeWrapper(v, newDist));
                }
            }
        }

        // If we get here, no path exists
        return Route.empty();
    }

    private Route reconstructPath(Map<SpatialNode, SpatialNode> predecessors, SpatialNode end, double totalDist) {
        List<SpatialNode> path = new LinkedList<>();
        SpatialNode current = end;
        
        while (current != null) {
            path.add(0, current); // Add to front
            current = predecessors.get(current);
        }
        return new Route(path, totalDist);
    }

    // Helper class for PriorityQueue
    private record NodeWrapper(SpatialNode node, double cost) {}
}