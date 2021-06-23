package com.raylabz.mocha.text.client;

public interface TextMessageBroker {

    /**
     * Sends data to the server.
     * @param data The data to send to the server.
     */
    void send(final String data);

    /**
     * Defines what will be executed once data is received by the client.
     * @param data The data received by the client.
     */
    void onReceive(String data);

}
