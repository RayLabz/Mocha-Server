package com.raylabz.mocha.server;

public final class Mocha {

    private static boolean serverStarted = false;

    public static Thread start(Server server) {
        if (!serverStarted) {
            Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
            serverThread.start();
            serverStarted = true;
            return serverThread;
        }
        else {
            throw new RuntimeException("A server has already been started (" + server.getName() + ").");
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
            throw new RuntimeException("No servers are currently running.");
        }
    }

}
