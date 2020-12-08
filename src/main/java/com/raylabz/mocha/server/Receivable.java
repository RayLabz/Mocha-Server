package com.raylabz.mocha.server;

import com.google.protobuf.GeneratedMessageV3;

/**
 * Models a Receivable, used to determine what is executed when a response is send back from the server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface Receivable<TMessage extends GeneratedMessageV3> {

    /**
     * Executes code when data is received.
     * @param message The message received.
     */
    void onReceive(TMessage message);

}
