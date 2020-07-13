//package com.raylabz.mocha;
//
//import com.raylabz.mocha.logger.Logger;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.HashSet;
//
//public abstract class TCPHandler implements Runnable {
//
//    private final Server server;
//    private final HashSet<Integer> ports;
//    private boolean enabled = true;
//
//    public TCPHandler(Server server, int port) {
//        this.server = server;
//        this.port = port;
//    }
//
//    public Server getServer() {
//        return server;
//    }
//
//    public Socket getSocket() {
//        return socket;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public BufferedReader getReader() {
//        return reader;
//    }
//
//    public abstract void onReceive(String data);
//
//    @Override
//    public void run() {
//
//        System.out.println("Listing to TCP port " + getPort());
//        String input;
//        try {
//            while (((input = reader.readLine()) != null) && server.isRunning() && enabled) {
//                onReceive(input);
//            }
//        } catch (IOException e) {
//            System.err.println("Error: " + e.getMessage());
//            e.printStackTrace();
//            Logger.logError(server, e.getMessage());
//        }
//    }
//
//    @Override
//    public final int getPort() {
//        return socket.getPort();
//    }
//
//}
