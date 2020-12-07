package com.raylabz.mocha.message;

import com.raylabz.bytesurge.container.*;
import com.raylabz.bytesurge.schema.ArraySchema;
import com.raylabz.bytesurge.schema.ByteSchema;
import com.raylabz.bytesurge.schema.ObjectSchema;
import com.raylabz.bytesurge.schema.SchemaType;
import com.raylabz.bytesurge.stream.Streamable;

import java.net.InetAddress;

public class MessageHeader implements Streamable {

    public static final ArraySchema IP_ADDRESS_SCHEMA = new ArraySchema(new ByteSchema(), 4);

    public static final ObjectSchema SCHEMA = new ObjectSchema.Builder()
            .expectData("size", SchemaType.INT)
            .expectArray("senderIP", IP_ADDRESS_SCHEMA)
            .expectArray("receiverIP", IP_ADDRESS_SCHEMA)
            .expectData("timestamp", SchemaType.LONG)
            .build();

    private final int size;
    private final InetAddress senderIP;
    private final InetAddress receiverIP;
    private final long timestamp;

    public MessageHeader(int size, InetAddress senderIP, InetAddress receiverIP) {
        this.size = size;
        this.senderIP = senderIP;
        this.receiverIP = receiverIP;
        this.timestamp = System.currentTimeMillis();
    }

    public final InetAddress getSenderIP() {
        return senderIP;
    }

    public final InetAddress getReceiverIP() {
        return receiverIP;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final int getSize() {
        return size;
    }


    @Override
    public final ObjectContainer toContainer() {
        return new ObjectContainer(SCHEMA)
            .putAttribute("size", new IntContainer(size))
            .putAttribute("senderIP", ArrayContainer.fromByteArray(senderIP.getAddress()))
            .putAttribute("receiverIP", ArrayContainer.fromByteArray(receiverIP.getAddress()))
            .putAttribute("timestamp", new LongContainer(timestamp));
    }

    @Override
    public final ObjectSchema toSchema() {
        return SCHEMA;
    }

}
