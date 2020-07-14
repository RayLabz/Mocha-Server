package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class UDPPortListener implements Runnable {

    private DatagramSocket socket;
    private final int port;
    private boolean enabled = true;

    public UDPPortListener(int port) {
        this.port = port;
    }

    public final DatagramSocket getSocket() {
        return socket;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public final int getPort() {
        return port;
    }

    public final InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public final void send(final String message) {
        try {
            final byte[] bytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socket.getInetAddress(), socket.getPort());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onReceive(DatagramPacket packet);

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Listing to UDP port " + port + ".");
            while (enabled) {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                onReceive(packet);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}
