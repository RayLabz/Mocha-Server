package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public abstract class UDPPortListener implements Runnable {

    private DatagramSocket socket;
    private final int port;
    private boolean enabled = true;

    public UDPPortListener(int port) {
        this.port = port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
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
