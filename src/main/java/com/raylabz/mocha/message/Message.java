package com.raylabz.mocha.message;

import java.net.InetAddress;

public abstract class Message {

    private final MessageHeader header;
    private final byte[] data;

    public Message(InetAddress senderIP, InetAddress receiverIP, byte[] bytes) {
        this.header = new MessageHeader(bytes.length, senderIP, receiverIP);
        this.data = bytes;
    }

    public byte[] getData() {
        return data;
    }

    public MessageHeader getHeader() {
        return header;
    }

}
