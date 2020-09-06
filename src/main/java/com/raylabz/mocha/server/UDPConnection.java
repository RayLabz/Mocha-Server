package com.raylabz.mocha.server;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;

/**
 * Manages a UDP connection to a client.
 * @author Nicos Kasenides
 * @version 1.0.0
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

    private HashSet<InetAddress> connectedAddresses = new HashSet<>();

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
    public final void send(InetAddress address, int outPort, final String data) {
        try {
            final byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, outPort);
            socket.send(packet);
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Defines what will be executed when data is received.
     * @param data The data received.
     */
    public abstract void onReceive(UDPConnection udpConnection, InetAddress address, int port, String data);

    /**
     * Defines what happens when the UDP connection starts.
     * A UDPConnection continuously listens for incoming data and responds using the onReceive() method.
     */
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Listening to UDP port " + port + ".");
            while (enabled) {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                connectedAddresses.add(packet.getAddress());
                final String data = new String(packet.getData(), 0, packet.getLength());
                onReceive(this, packet.getAddress(), packet.getPort(), data);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}
