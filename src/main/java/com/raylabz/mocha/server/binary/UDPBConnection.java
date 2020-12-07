package com.raylabz.mocha.server.binary;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.bytesurge.stream.StreamWriter;
import com.raylabz.mocha.logger.Logger;
import com.raylabz.mocha.message.Message;
import com.raylabz.mocha.server.SecurityMode;
import com.raylabz.mocha.server.UDPPeer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a UDP connection to a client.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class UDPBConnection implements Runnable {

    /**
     * The server that this UDP connection belongs to.
     */
    private BinaryServer server;

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
     * A set of unique connected peers of this UDP connection.
     */
    private final HashSet<InetAddress> udpPeers = new HashSet<>();

    /**
     * Constructs a new UDPConnection.
     * @param port The connection's port.
     */
    public UDPBConnection(int port) {
        this.port = port;
    }

    /**
     * Retrieves the server of this UDPConnection.
     * @return Returns a Server.
     */
    protected BinaryServer getServer() {
        return this.server;
    }

    /**
     * Sets the server of this UDPConnection.
     * @param server A server
     */
    void setServer(BinaryServer server) {
        this.server = server;
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
        if (!enabled) {
            socket.close();
        }
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
     * Sends a message to a client.
     * @param address The address to send the message to.
     * @param outPort The port of the client.
     * @param message The message to send.
     */
    public final void send(InetAddress address, int outPort, final Message message) {
        try {
            StreamWriter streamWriter = new StreamWriter(message.toSchema());
            streamWriter.writeArray((ArrayContainer) message.toContainer());
            streamWriter.close();
            final byte[] bytes = streamWriter.getBytes();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteStream);
            dos.writeInt(bytes.length);
            dos.write(bytes);
            dos.close();
            byteStream.close();
            DatagramPacket packet = new DatagramPacket(byteStream.toByteArray(), bytes.length, address, outPort);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Multicasts a message to selected clients.
     * @param message The message to send
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicast(final Message message, ArrayList<InetAddress> ipAddresses) {
        if (isEnabled()) {
            for (UDPPeer peer : connectedPeers) {
                if (ipAddresses.contains(peer.getAddress())) {
                    send(peer.getAddress(), peer.getPort(), message);
                }
            }
        }
        else {
            System.err.println("[UDP " + port + "]" + "Error - Cannot multicast. UDPConnection [" + getPort() + "] disabled");
            Logger.logError("[UDP " + port + "]" + "Error - Cannot multicast. UDPConnection [" + getPort() + "] disabled");
        }
    }

    /**
     * Broadcasts a message to all connected peers.
     * @param message The message to broadcast.
     */
    public final void broadcast(final Message message) {
        if (isEnabled()) {
            for (final UDPPeer peer : connectedPeers) {
                send(peer.getAddress(), peer.getPort(), message);
            }
        }
        else {
            System.err.println("[UDP " + port + "]" + "Error - Cannot broadcast. UDPConnection [" + getInetAddress() + ":" + getPort() + "] disabled");
            Logger.logError("[UDP " + port + "]" + "Error - Cannot broadcast. UDPConnection [" + getInetAddress() + ":" + getPort() + "] disabled");
        }
    }

    /**
     * Defines what will be executed when message is received.
     * @param udpConnection The UDPConnection.
     * @param address The address of the client to send the message to.
     * @param outPort The outPort of the client (used to send outgoing messages).
     * @param message The message received.
     */
    public abstract void onReceive(UDPBConnection udpConnection, InetAddress address, int outPort, Message message);

    /**
     * Defines what happens when the UDP connection starts.
     * A UDPConnection continuously listens for incoming data and responds using the onReceive() method.
     */
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Waiting for messages on UDP port " + port + ".");
            Logger.logInfo("Waiting for messages on UDP port " + port + ".");
            while (isEnabled()) {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                if (server.getSecurityMode() == SecurityMode.BLACKLIST && server.getBlacklist().contains(packet.getAddress())) {
                    System.out.println("Banned IP address " + packet.getAddress().toString() + " attempted to send package on UDP port " + port + " but the package was rejected.");
                }
                else if (server.getSecurityMode() == SecurityMode.WHITELIST && !server.getWhitelist().contains(packet.getAddress())) {
                    System.out.println("Non-whitelisted IP address " + packet.getAddress().toString() + " attempted to send package on UDP port " + port + " but the package was rejected");
                }
                else {
                    connectedPeers.add(new UDPPeer(packet.getAddress(), packet.getPort()));
                    if (udpPeers.add(packet.getAddress())) {
                        System.out.println("New peer " + packet.getAddress() + " connected on UDP port " + port + ".");
                        Logger.logInfo("New peer " + packet.getAddress() + " connected on UDP port " + port + ".");
                    }

                    final byte[] data = packet.getData();
                    DataInputStream reader = new DataInputStream(new ByteArrayInputStream(data));

                    final int size = reader.readInt();

                    //Get the rest of the data:
                    final byte[] actualData = new byte[size];
                    for (int i = 0; i < size; i++) {
                        data[i] = reader.readByte();
                    }

                    StreamReader reader2 = new StreamReader(Message.getSchema(size), actualData);
                    final byte[] bytes = reader2.readByteArray();

                    Message message = new Message(bytes);

                    //Handle the message:
                    onReceive(this, packet.getAddress(), packet.getPort(), message);
                }
            }
        } catch (SocketException se) {
            if (!isEnabled()) {
                System.out.println("Stopped listening to UDP port " + port + ".");
                Logger.logInfo("Stopped listening to UDP port " + port + ".");
            }
        } catch (IOException e) {
            System.err.println("[UDP " + port + "]" + "Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}