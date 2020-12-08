package com.raylabz.mocha.server;

import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface TCPReceivable<TMessage extends GeneratedMessageV3> {

    /**
     * Executes code when a message is received.
     * @param tcpConnection The TCPConnection receiving the message.
     * @param message The message received.
     */
    void onReceive(TCPConnection<TMessage> tcpConnection, TMessage message) throws IOException;

}