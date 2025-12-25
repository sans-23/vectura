package com.vectura.core.engine;

import com.vectura.core.domain.SpatialNode;
import com.vectura.core.topology.NetworkGraph;

public interface RoutingEngine {
    /**
     * Calculates the optimal path between two nodes.
     * @return A Route object containing the path and total cost.
     */
    Route findRoute(NetworkGraph graph, SpatialNode start, SpatialNode end);
}
