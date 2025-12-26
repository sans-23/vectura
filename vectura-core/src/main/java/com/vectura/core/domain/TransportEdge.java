package com.vectura.core.domain;

/**
 * Represents a directed connection between two {@link SpatialNode} entities.
 * <p>
 * Design Rationale:
 * This encapsulates both the static physical properties (length, max speed) 
 * and dynamic state (congestion, closure status). It decouples the mathematical 
 * definition of an edge (u -> v) from the logistics metadata required for 
 * cost calculation.
 * </p>
 */
public class TransportEdge {
    private final SpatialNode to;
    private final SpatialNode from;
    private final double distanceKm;
    private final String surface;

    private volatile boolean isOpen = true;
    private volatile double trafficCoef = 1.0;

    public TransportEdge(SpatialNode from, SpatialNode to, String surface){
        this.to = to;
        this.from = from;
        this.surface = surface;
        this.distanceKm = to.coordinate().distanceTo(from.coordinate());
    }

    public double getCost(){
        if(!isOpen) return Double.POSITIVE_INFINITY;
        return distanceKm*trafficCoef;
    }

    // State Management
    public void close() { this.isOpen = false; }
    public void open() { this.isOpen = true; }
    public boolean isOpen() { return isOpen; }

    public SpatialNode getTarget() {
        return to;
    }

    public String getSurface() {
        return surface;
    }

    public SpatialNode getSource() {
        return from;
    }
}