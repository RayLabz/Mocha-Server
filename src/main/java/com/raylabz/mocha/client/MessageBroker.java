package com.raylabz.mocha.client;

import com.google.protobuf.GeneratedMessageV3;

public interface MessageBroker<TMessage extends GeneratedMessageV3> {

    /**
     * Sends a message to the server.
     * @param message The message to send to the server.
     */
    void send(final TMessage message);

    /**
     * Defines what will be executed once a message is received by the client.
     * @param message The message received by the client.
     */
    void onReceive(TMessage message);

}
