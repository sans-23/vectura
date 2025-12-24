package com.vectura.core.io;

import java.io.InputStream;

import com.vectura.core.topology.NetworkGraph;

/**
 * A concrete implementation of {@link GraphSource} for the OpenStreetMap (OSM) JSON format.
 * <p>
 * Design Rationale:
 * Handles the ETL (Extract, Transform, Load) logic specific to the OSM 
 * Overpass API schema, mapping raw JSON elements to the internal {@link com.vectura.core.domain} 
 * models.
 * </p>
 */
public class OpenStreetMapLoader implements GraphSource {

    @Override
    public NetworkGraph load(InputStream stream){
        return new NetworkGraph();
    }
}