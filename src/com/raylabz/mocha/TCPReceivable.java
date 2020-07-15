package com.raylabz.mocha;

/**
 * Models a TCPReceivable, used to determine what is executed when a TCPConnection receives data.
 */
public interface TCPReceivable {

    /**
     * Executes code when data is received.
     * @param tcpConnection The TCPConnection receiving the data.
     * @param data The data received.
     */
    void onReceive(TCPConnection tcpConnection, String data);

}
