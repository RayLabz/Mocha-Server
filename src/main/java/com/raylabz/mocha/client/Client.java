package com.raylabz.mocha.client;

import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides common functionality for a client.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class Client implements Runnable, MessageBroker {

    /**
     * The name of this client.
     */
    private final String name;

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
    private final AtomicBoolean listening = new AtomicBoolean(true);

    /**
     * Indicates whether this client is connected to the server.
     */
    private final AtomicBoolean connected = new AtomicBoolean(false);

    /**
     * Constructs a new Client.
     * @param ipAddress Text-based internet address to which this client will connect.
     * @param port The port of this client.
     * @throws UnknownHostException Thrown when an invalid IP address was provided.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(String name, String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        this.name = name;
        this.address = InetAddress.getByName(ipAddress);
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + "). The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
    }

    /**
     * Constructs a new Client.
     * @param inetAddress The IP address to which this client will connect.
     * @param port The port of this client.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(String name, InetAddress inetAddress, int port) throws PortUnreachableException {
        this.name = name;
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
        return listening.get();
    }

    /**
     * Sets the listening attribute for this client.
     * @param listening Set to true if the client should be listening to incoming messages, to false otherwise.
     */
    public final void setListening(boolean listening) {
        this.listening.set(listening);
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
     * Retrieves the name of this client.
     * @return Returns a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves whether this client is connected to a server or not.
     * @return Returns true if the client is connected, false otherwise.
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Sets the connected status of this client.
     * @param connected Set to true if connected, false otherwise.
     */
    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    /**
     * Executes code handling the case where the client may not be able to connect to the server.
     */
    public abstract void onConnectionRefused();

    /**
     * Executes code to initialize the client.
     */
    public abstract void initialize();

    /**
     * Defines the processing instructions for this client
     */
    public abstract void process();

    /**
     * Runs the client.
     */
    @Override
    public final void run() {
        initialize();
        while (isConnected()) {
            process();
        }
    }

}
