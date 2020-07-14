package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TCPHandler implements Runnable {

    private final int port;
    private ServerSocket serverSocket;
    private boolean enabled = true;
    private final Vector<TCPConnection> tcpConnections = new Vector<>();
    private final Vector<Thread> tcpConnectionThreads = new Vector<>();
    private final TCPReceivable receivable;

    public TCPHandler(int port, final TCPReceivable receivable) {
        this.port = port;
        this.receivable = receivable;
    }

    public int getPort() {
        return port;
    }

    public TCPReceivable getReceivable() {
        return receivable;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Vector<TCPConnection> getTcpConnections() {
        return tcpConnections;
    }

    public Vector<Thread> getTcpConnectionThreads() {
        return tcpConnectionThreads;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for connections on TCP port " + port + ".");
            while (enabled) {
                Socket socket = serverSocket.accept();
                System.out.println("New TCP connection on port " + port + " from IP: " + socket.getInetAddress());
                TCPConnection tcpConnection = new TCPConnection(socket, receivable);
                tcpConnections.add(tcpConnection);
                Thread t = new Thread(tcpConnection, "TCP-Thread-" + socket.getInetAddress().toString());
                tcpConnectionThreads.add(t);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }

    }

}
