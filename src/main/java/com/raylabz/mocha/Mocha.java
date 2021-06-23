package com.raylabz.mocha;

import com.raylabz.mocha.binary.client.BinaryClient;
import com.raylabz.mocha.binary.client.BinaryWebSocketClient;
import com.raylabz.mocha.binary.server.BinaryServer;
import com.raylabz.mocha.text.client.TextClient;
import com.raylabz.mocha.text.client.TextWebSocketClient;
import com.raylabz.mocha.text.server.TextServer;

/**
 * Main library class - starts up servers and clients.
 */
public final class Mocha {

    /**
     * Starts a server.
     * @param server The server to start.
     * @return Returns the thread the server is running on.
     */
    public static Thread start(TextServer server) {
        Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
        serverThread.start();
        return serverThread;
    }

    /**
     * Starts a server.
     * @param server The server to start.
     * @return Returns the thread the server is running on.
     */
    public static Thread start(BinaryServer server) {
        Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
        serverThread.start();
        return serverThread;
    }

    /**
     * Starts a client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(TextClient client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

    /**
     * Starts a client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(BinaryClient client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

    /**
     * Starts a WebSocket client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(TextWebSocketClient client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

    /**
     * Starts a WebSocket client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(BinaryWebSocketClient client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

}
