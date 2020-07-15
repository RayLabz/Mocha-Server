package com.raylabz.mocha.client;

import java.io.IOException;
import java.net.*;

/**
 * Defines functionality for a UDP Client.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class UDPClient extends Client {

    /**
     * The client's socket.
     */
    private final DatagramSocket socket;

    /**
     * The client's buffer.
     */
    private final byte[] buffer = new byte[65535];

    /**
     * Constructs a new UDPClient.
     * @param ipAddress The IP address that this client will connect to.
     * @param port The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException Thrown when the client's socket cannot be instantiated.
     */
    public UDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
        super(ipAddress, port);
        this.socket = new DatagramSocket();
        Thread listeningThread = new Thread(() -> {
            try {
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                final String data = new String(packet.getData(), 0, packet.getLength());
                onReceive(data);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        listeningThread.start();
    }

    /**
     * Retrieves the client's socket.
     * @return Returns a DatagramSocket.
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Sends data to the server.
     * @param data The data to send to the server.
     */
    @Override
    public final void send(final String data) {
        try {
            final byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, getAddress(), getPort());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
