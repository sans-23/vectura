package com.vectura.core.engine;

import com.vectura.core.domain.SpatialNode;
import java.util.List;

public record Route(List<SpatialNode> path, double totalDistanceKm) {
    public static Route empty() {
        return new Route(List.of(), Double.POSITIVE_INFINITY);
    }
}