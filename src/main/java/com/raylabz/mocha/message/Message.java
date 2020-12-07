package com.raylabz.mocha.message;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.container.ObjectContainer;
import com.raylabz.bytesurge.schema.ArraySchema;
import com.raylabz.bytesurge.schema.ByteSchema;
import com.raylabz.bytesurge.schema.ObjectSchema;
import com.raylabz.bytesurge.stream.Streamable;

public class Message implements Streamable {

    private final MessageHeader header;
    private final byte[] data;

    public Message(MessageHeader header, byte[] bytes) {
        this.header = header;
        this.data = bytes;
    }

    public byte[] getData() {
        return data;
    }

    public MessageHeader getHeader() {
        return header;
    }

    @Override
    public ObjectContainer toContainer() {
        return new ObjectContainer(toSchema())
                .putAttribute("header", header.toContainer())
                .putAttribute("data", ArrayContainer.fromByteArray(data));
    }

    @Override
    public ObjectSchema toSchema() {
        return new ObjectSchema.Builder()
                .expectObject("header", MessageHeader.SCHEMA)
                .expectArray("data", new ArraySchema(new ByteSchema(), header.getSize()))
                .build();
    }

}
