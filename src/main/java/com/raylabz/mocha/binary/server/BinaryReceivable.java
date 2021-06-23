package com.raylabz.mocha.binary.server;

/**
 * Models a Receivable, used to determine what is executed when a response is send back from the server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface BinaryReceivable {

    /**
     * Executes code when data is received.
     * @param data The data received.
     */
    void onReceive(byte[] data);

}
