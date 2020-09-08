package com.raylabz.mocha.server;

public final class Mocha {

    public static void start(Server server) {
        Thread serverThread = new Thread(server, server.getName() + "-ServerThread");
        serverThread.start();
    }

}
