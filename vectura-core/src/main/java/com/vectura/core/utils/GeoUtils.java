package com.vectura.core.utils;

public class GeoUtils {

    private static final int EARTH_RADIUS_KM = 6371;
    
    public static double haversineDistance(double lat, double lon, double lat1, double lon1){
        double dLat = Math.toRadians(lat1 - lat);
        double dLon = Math.toRadians(lon1 - lon);

        double l1 = Math.toRadians(lat);
        double l2 = Math.toRadians(lat1);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.pow(Math.sin(dLon / 2), 2) *
                   Math.cos(l1) * Math.cos(l2);
        
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_KM * c;
    }
}
