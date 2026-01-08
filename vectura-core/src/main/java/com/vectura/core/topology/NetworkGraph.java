package com.vectura.core.topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.vectura.core.domain.SpatialNode;
import com.vectura.core.domain.TransportEdge;

public class NetworkGraph {

    private final Map<Long, SpatialNode> nodes;
    private final Map<Long, List<TransportEdge>> adjList;

    public NetworkGraph() {
        nodes = new ConcurrentHashMap<>();
        adjList = new ConcurrentHashMap<>();
    }

    public void snapNode(SpatialNode node) {
        // find nearest neigbour and link it with a edge
        SpatialNode neighbour = null;
        double min_dist = Double.POSITIVE_INFINITY;
        for (SpatialNode n : nodes.values()) {
            double distance = node.coordinate().distanceTo(n.coordinate());
            if (min_dist > distance) {
                min_dist = distance;
                neighbour = n;
            }
        }

        // Add edges in both directions so we can route TO and FROM the new node
        addEdge(node, neighbour);
        addEdge(neighbour, node);
    }

    public void addNode(SpatialNode node) {
        nodes.putIfAbsent(node.id(), node);
        adjList.putIfAbsent(node.id(), new ArrayList<>());
    }

    public void addEdge(SpatialNode to, SpatialNode from, String surface) {
        addNode(to);
        addNode(from);

        adjList.get(from.id()).add(new TransportEdge(from, to, surface));
    }

    public void addEdge(SpatialNode to, SpatialNode from) {
        addNode(to);
        addNode(from);

        adjList.get(from.id()).add(new TransportEdge(from, to, "paved"));
    }

    public List<TransportEdge> getOutboundEdges(long nodeId) {
        return adjList.getOrDefault(nodeId, Collections.emptyList());
    }

    public Optional<SpatialNode> getNode(long id) {
        return Optional.ofNullable(nodes.get(id));
    }

    public int nodeCount() {
        return nodes.size();
    }

    public int edgeCount() {
        return adjList.values().stream().mapToInt(List::size).sum();
    }

    public List<SpatialNode> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

}
