package com.raylabz.mocha.server.binary;

import com.raylabz.mocha.message.Message;

import java.io.IOException;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface TCPBReceivable {

    /**
     * Executes code when a message is received.
     * @param tcpConnection The TCPConnection receiving the message.
     * @param message The message received.
     */
    void onReceive(TCPBConnection tcpConnection, Message message) throws IOException;

}