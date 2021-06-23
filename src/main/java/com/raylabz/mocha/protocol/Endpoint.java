package com.raylabz.mocha.protocol;

import com.raylabz.bytesurge.container.Container;
import com.raylabz.bytesurge.schema.ObjectSchema;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.servexpresso.InputParams;
import com.raylabz.servexpresso.Service;

public abstract class Endpoint {

    private final short id;

    private final ObjectSchema schema;

    private final Service service;

    public Endpoint(short id, ObjectSchema schema, Service service) {
        this.id = id;
        this.schema = schema;
        this.service = service;
    }

    public short getId() {
        return id;
    }

    public ObjectSchema getSchema() {
        return schema;
    }

    public Service getService() {
        return service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        return id == endpoint.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public abstract InputParams getParams(StreamReader reader);

}
