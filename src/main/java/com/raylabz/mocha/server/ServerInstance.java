package com.raylabz.mocha.server;

/**
 * Creates an instance of a server
 */
public class ServerInstance {

    /**
     * The server
     */
    private final Server server;

    /**
     * Creates a ServerInstance.
     * @param server The server to run.
     */
    public ServerInstance(Server server) {
        this.server = server;
    }

    /**
     * Retrieves the server.
     * @return Returns a Server.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Starts the instance.
     */
    public void start() {
        Thread serverThread = new Thread(server, server.getName());
        serverThread.start();
    }

}
