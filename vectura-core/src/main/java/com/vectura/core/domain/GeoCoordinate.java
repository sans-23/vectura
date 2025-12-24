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
    
    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Calculates the Great Circle (Haversine) distance to another coordinate.
     * @return Distance in Kilometers.
     */
    public double distanceTo(GeoCoordinate other) {
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.pow(Math.sin(dLon / 2), 2) *
                   Math.cos(lat1) * Math.cos(lat2);
        
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_KM * c;
    }
}