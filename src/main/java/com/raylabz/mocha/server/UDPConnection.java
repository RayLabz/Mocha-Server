package com.raylabz.mocha.server;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /**
     * Tracks the connected peers of this UDP connection.
     */
    private final HashSet<UDPPeer> connectedPeers = new HashSet<>();

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
     * Retrieves all the connected peers of this connection.
     * @return Returns a set of UDPPeer.
     */
    public HashSet<UDPPeer> getConnectedPeers() {
        return connectedPeers;
    }

    /**
     * Check is this UDPConnection is enabled.
     * @return Returns true if the connection is enabled, false otherwise.
     */
    public final boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Enables or disables the connection.
     * @param enabled Set to true to enable the connection, false to disable.
     */
    public final void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
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
     * @param address The address to send the data to.
     * @param outPort The port of the client.
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
     * Broadcasts a message to all connected peers.
     * @param data The data to broadcast.
     */
    public final void broadcast(final String data) {
        for (final UDPPeer peer : connectedPeers) {
            send(peer.getAddress(), peer.getPort(), data);
        }
    }

    /**
     * Defines what will be executed when data is received.
     * @param udpConnection The UDPConnection.
     * @param address The address of the client to send the data to.
     * @param port The port of the client.
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
            System.out.println("Waiting for connections on UDP port " + port + ".");
            Logger.logInfo("Waiting for connections on UDP port " + port + ".");
            while (isEnabled()) {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                connectedPeers.add(new UDPPeer(packet.getAddress(), packet.getPort()));
                final String data = new String(packet.getData(), 0, packet.getLength());
                onReceive(this, packet.getAddress(), packet.getPort(), data);
            }
            System.out.println("Stopped listening to UDP port " + port + ".");
            Logger.logInfo("Stopped listening to UDP port " + port + ".");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}
