package com.raylabz.mocha.message;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.container.Container;
import com.raylabz.bytesurge.schema.ArraySchema;
import com.raylabz.bytesurge.schema.ByteSchema;
import com.raylabz.bytesurge.schema.Schema;
import com.raylabz.bytesurge.stream.Streamable;

public class Message implements Streamable {

    protected final byte[] data;

    public Message(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public Container toContainer() {
        return ArrayContainer.fromByteArray(data);
    }

    @Override
    public Schema toSchema() {
        return getSchema(data.length);
    }

    public static ArraySchema getSchema(int size) {
        return new ArraySchema(new ByteSchema(), size);
    }

}
