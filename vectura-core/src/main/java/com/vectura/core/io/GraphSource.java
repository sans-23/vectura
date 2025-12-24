package com.vectura.core.io;

import com.vectura.core.topology.NetworkGraph;
import java.io.InputStream;

/**
 * Defines the contract for graph data ingestion.
 * <p>
 * Design Rationale:
 * Inversion of Control (IoC). The library does not concern itself with 
 * file systems or protocols (HTTP/S3). It expects a raw {@link InputStream}, 
 * allowing the data source to be abstracted away from the parsing logic.
 * </p>
 */
public interface GraphSource {
    NetworkGraph load(InputStream stream);
}