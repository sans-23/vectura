package com.vectura.core.domain;

/**
 * Represents a distinct vertex in the spatial network (e.g., an Intersection or a Warehouse).
 * <p>
 * Design Rationale:
 * Unlike {@link GeoCoordinate}, a Node is an Entity with a unique identity (ID).
 * Even if two nodes occupy the exact same physical coordinate (e.g., an overpass 
 * and the road beneath it), they are distinct logical entities in the graph topology.
 * </p>
 */
public class SpatialNode {
}