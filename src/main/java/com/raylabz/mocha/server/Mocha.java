package com.raylabz.mocha.server;

public final class Mocha {

    private static boolean serverStarted = false;

    public static void start(Server server) {
        if (!serverStarted) {
            Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
            serverThread.start();
            serverStarted = true;
        }
        else {
            System.err.println("A server has already been started (" + server.getName() + ").");
        }
    }

    public static void stop(Server server) {
        if (serverStarted) {
            server.removeAllTCPHandlers();
            server.removeAllUDPHandlers();
            server.setRunning(false);
            serverStarted = false;
        }
        else {
            System.err.println("No servers are currently running.");
        }
    }

}
