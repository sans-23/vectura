package com.vectura.core.domain;

import com.vectura.core.utils.GeoUtils;

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
    /**
     * Calculates the Great Circle (Haversine) distance to another coordinate.
     * @return Distance in Kilometers.
     */
    public double distanceTo(GeoCoordinate other) {
        return GeoUtils.haversineDistance(this.latitude, this.longitude, other.latitude, other.longitude);
    }
}