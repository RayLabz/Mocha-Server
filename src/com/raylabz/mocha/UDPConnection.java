package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Manages a UDP connection to a client.
 */
public abstract class UDPConnection implements Runnable {

    /**
     * The client's socket.
     */
    private DatagramSocket socket;

    /**
     * The client's port.
     */
    private final int port;

    /**
     * Determines whether the client is enabled or not.
     */
    private boolean enabled = true;

    /**
     * Constructs a new UDPConnection.
     * @param port The connection's port.
     */
    public UDPConnection(int port) {
        this.port = port;
    }

    /**
     * Retrieves the connection's socket.
     * @return Returns a DatagramSocket.
     */
    public final DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Check is this UDPConnection is enabled.
     * @return Returns true if the connection is enabled, false otherwise.
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables the connection.
     * @param enabled Set to true to enable the connection, false to disable.
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Retrieves the connection's port.
     * @return Return integer (port number).
     */
    public final int getPort() {
        return port;
    }

    /**
     * Retrieves the connection's internet address.
     * @return Returns an InetAddress.
     */
    public final InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    /**
     * Sends data to the server.
     * @param data The data to send.
     */
    public final void send(final String data) {
        try {
            final byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socket.getInetAddress(), socket.getPort());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Defines what will be executed when data is received.
     * @param udpConnection The UDPConnection receiving the data.
     * @param data The data received.
     */
    public abstract void onReceive(UDPConnection udpConnection, String data);

    /**
     * Defines what happens when the UDP connection starts.
     * A UDPConnection continuously listens for incoming data and responds using the onReceive() method.
     */
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Listing to UDP port " + port + ".");
            while (enabled) {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                final String data = new String(packet.getData(), 0, packet.getLength());
                onReceive(this, data);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}
