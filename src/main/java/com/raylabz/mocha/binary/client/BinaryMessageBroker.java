package com.raylabz.mocha.binary.client;

public interface BinaryMessageBroker {

    /**
     * Sends data to the server.
     * @param data The data to send to the server.
     */
    void send(final byte[] data);

    /**
     * Defines what will be executed once data is received by the client.
     * @param data The data received by the client.
     */
    void onReceive(byte[] data);

}
