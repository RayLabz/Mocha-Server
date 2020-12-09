package com.raylabz.mocha.server;

import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 * @author Nicos Kasenides
 * @version 1.0.0
 * @param <TMessage> The type of message.
 */
public interface TCPReceivable<TMessage extends GeneratedMessageV3> {

    /**
     * Executes code when a message is received.
     * @param tcpConnection The TCPConnection receiving the message.
     * @param message The message received.
     * @throws IOException when an error occurs while reading the message.
     */
    void onReceive(TCPConnection<TMessage> tcpConnection, TMessage message) throws IOException;

}