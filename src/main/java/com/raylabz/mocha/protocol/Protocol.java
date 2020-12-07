package com.raylabz.mocha.protocol;

import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.servexpresso.InputParams;

import java.util.HashMap;

public abstract class Protocol {

    private final HashMap<Short, Endpoint> endpoints = new HashMap<>();

    public Protocol() { }

    public HashMap<Short, Endpoint> getEndpoints() {
        return endpoints;
    }

    public void addEndpoint(Endpoint endpoint) {
        if (endpoints.containsKey(endpoint.getId())) {
            throw new RuntimeException("Endpoint with ID '" + endpoint.getId() + "' already exists.");
        }
        else {
            endpoints.put(endpoint.getId(), endpoint);
        }
    }

    public final void runEndpoint(short id, byte[] data) {
        final Endpoint endpoint = endpoints.get(id);
        if (endpoint != null) {
            StreamReader reader = new StreamReader(endpoint.getSchema(), data);
            InputParams params = endpoint.getParams(reader);
            endpoint.getService().processRequest(params);
        }
        else {
            throw new RuntimeException("Endpoint with ID '" + id + "' not found.");
        }
    }

}
