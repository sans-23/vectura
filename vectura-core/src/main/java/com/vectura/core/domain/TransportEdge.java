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
}