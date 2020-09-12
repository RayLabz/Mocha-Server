package com.raylabz.mocha.server;

/**
 * Models a Receivable, used to determine what is executed when a response is send back from the server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface Receivable {

    /**
     * Executes code when data is received.
     * @param data The data received.
     */
    void onReceive(String data);

}