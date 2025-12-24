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
    
    private final Map<String, SpatialNode> nodes;
    private final Map<String, List<TransportEdge>> adjList;
    
    public NetworkGraph(){
        nodes = new ConcurrentHashMap<>();
        adjList = new ConcurrentHashMap<>();
    }

    public void addNode(SpatialNode node){
        nodes.putIfAbsent(node.id(), node);
        adjList.putIfAbsent(node.id(), new ArrayList<>());
    }

    public void addEdge(SpatialNode to, SpatialNode from){
        addNode(to);
        addNode(from);

        adjList.get(from.id()).add(new TransportEdge(from, to));
    }

    public List<TransportEdge> getOutboundEdges(String nodeId) {
        return adjList.getOrDefault(nodeId, Collections.emptyList());
    }

    public Optional<SpatialNode> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    public int nodeCount() { return nodes.size(); }
    public int edgeCount() { 
        return adjList.values().stream().mapToInt(List::size).sum(); 
    }
    
}
