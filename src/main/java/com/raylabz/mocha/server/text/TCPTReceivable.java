package com.raylabz.mocha.server.text;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface TCPTReceivable {

    /**
     * Executes code when data is received.
     * @param tcpConnection The TCPConnection receiving the data.
     * @param data The data received.
     */
    void onReceive(TCPTConnection tcpConnection, String data);

}