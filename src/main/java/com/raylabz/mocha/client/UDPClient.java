package com.raylabz.mocha.client;

import com.raylabz.mocha.server.Receivable;

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
    private DatagramSocket socket;

    /**
     * The client's buffer.
     */
    private final byte[] buffer = new byte[65535];

    /**
     * Constructs a new UDPClient.
     *
     * @param name The client's name.
     * @param ipAddress The IP address that this client will connect to.
     * @param port The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException Thrown when the client's socket cannot be instantiated.
     */
    public UDPClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
        try {
            this.socket = new DatagramSocket();
            setConnected(true);
        } catch (ConnectException ce) {
            setListening(false);
            setConnected(false);
            onConnectionRefused();
        }
        Thread listeningThread = new Thread(() -> {
            if (isConnected()) {
                while (isConnected()) {
                    if (isListening()) {
                        try {
                            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            final String data = new String(packet.getData(), 0, packet.getLength());
                            onReceive(data);
                        } catch (ConnectException ce) {
                            setListening(false);
                            setConnected(false);
                            onConnectionRefused();
                        } catch (IOException e) {
                            System.err.println("Error receiving: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            else {
                onConnectionRefused();
            }
        }, name + "-Listener");
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
        if (isConnected()) {
            try {
                final byte[] bytes = data.getBytes();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, getAddress(), getPort());
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendAndReceive(String data, Receivable receivable) {
        if (isConnected()) {
            try {
                final byte[] bytes = data.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, getAddress(), getPort());
                socket.send(sendPacket);

                try {
                    final DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);
                    final String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    receivable.onReceive(receiveData);
                } catch (ConnectException ce) {
                    setListening(false);
                    setConnected(false);
                    onConnectionRefused();
                } catch (IOException e) {
                    System.err.println("Error receiving: " + e.getMessage());
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
