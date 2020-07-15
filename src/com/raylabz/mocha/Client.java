package com.raylabz.mocha;

import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;

/**
 * @author Nicos Kasenides
 * @version 1.0.0
 * Provides common functionality for a client.
 */
public abstract class Client implements Runnable {

    /**
     * The internet address that this client will connect to.
     */
    private final InetAddress address;

    /**
     * The port that this client will connect to.
     */
    private final int port;

    /**
     * Indicates whether this client will be listening for incoming messages.
     */
    private boolean listening = true;

    /**
     * Constructs a new Client.
     * @param ipAddress Text-based internet address to which this client will connect.
     * @param port The port of this client.
     * @throws UnknownHostException Thrown when an invalid IP address was provided.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        this(InetAddress.getByName(ipAddress), port);
    }

    /**
     * Constructs a new Client.
     * @param inetAddress The IP address to which this client will connect.
     * @param port The port of this client.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(InetAddress inetAddress, int port) throws PortUnreachableException {
        this.address = inetAddress;
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + "). The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
    }

    /**
     * Checks if this client is listening for incoming messages.
     * @return Returns true if the client is listening for incoming messages, false otherwise.
     */
    public final boolean isListening() {
        return listening;
    }

    /**
     * Sets the listening attribute for this client.
     * @param listening Set to true if the client should be listening to incoming messages, to false otherwise.
     */
    public final void setListening(boolean listening) {
        this.listening = listening;
    }

    /**
     * Retrieves the internet address that this client is connected to.
     * @return Returns InetAddress.
     */
    public final InetAddress getAddress() {
        return address;
    }

    /**
     * Retrieves the port that this client is connected to.
     * @return Returns an integer (port number).
     */
    public final int getPort() {
        return port;
    }

    /**
     * Sends data to the server.
     * @param data The data to send to the server.
     */
    public abstract void send(final String data);

    /**
     * Defines what will be executed once data is received by the client.
     * @param data The data received by the client.
     */
    public abstract void onReceive(String data);

}
