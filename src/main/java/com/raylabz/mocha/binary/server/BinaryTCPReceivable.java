package com.raylabz.mocha.binary.server;

import com.raylabz.mocha.text.server.TextTCPConnection;

import java.io.IOException;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface BinaryTCPReceivable {

    /**
     * Executes code when data is received.
     * @param tcpConnection The TCPConnection receiving the data.
     * @param data The data received.
     */
    void onReceive(BinaryTCPConnection tcpConnection, byte[] data) throws IOException;

}
