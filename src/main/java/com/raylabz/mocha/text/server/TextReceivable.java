package com.raylabz.mocha.text.server;

/**
 * Models a Receivable, used to determine what is executed when a response is send back from the server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface TextReceivable {

    /**
     * Executes code when data is received.
     * @param data The data received.
     */
    void onReceive(String data);

}
