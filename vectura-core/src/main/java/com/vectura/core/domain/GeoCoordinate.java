package com.vectura.core.domain;

/**
 * A Value Object representing a precise physical location on the WGS84 ellipsoid.
 * <p>
 * Design Rationale:
 * This class is designed as an immutable Record because coordinates are 
 * fundamental mathematical values, not stateful entities. Two coordinates 
 * with the same latitude and longitude are structurally identical 
 * and interchangeable.
 * </p>
 */
public record GeoCoordinate(double latitude, double longitude) {
}