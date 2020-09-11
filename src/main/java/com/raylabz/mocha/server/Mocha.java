package com.raylabz.mocha.server;

import com.raylabz.mocha.client.Client;
import com.raylabz.mocha.client.WebSocketClient;

/**
 * Main library class - starts up servers and clients.
 */
public final class Mocha {

    /**
     * Starts a server.
     * @param server The server to start.
     * @return Returns the thread the server is running on.
     */
    public static Thread start(Server server) {
        Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
        serverThread.start();
        return serverThread;
    }

    /**
     * Starts a client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(Client client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

    /**
     * Starts a WebSocket client.
     * @param client The client to start.
     * @return Returns the thread the client is running on.
     */
    public static Thread start(WebSocketClient client) {
        Thread clientThread = new Thread(client, client.getName() + "-ClientThread");
        clientThread.start();
        return clientThread;
    }

}
