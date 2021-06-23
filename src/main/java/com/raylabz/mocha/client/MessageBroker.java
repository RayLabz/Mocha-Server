package com.raylabz.mocha.client;

public interface MessageBroker<TMessageType> {

    /**
     * Sends a message to the server.
     * @param message The message to send to the server.
     */
    void send(final TMessageType message);

    /**
     * Defines what will be executed once a message is received by the client.
     * @param message The message received by the client.
     */
    void onReceive(TMessageType message);

}
