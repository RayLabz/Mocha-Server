package com.raylabz.mocha.client;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides common functionality for a client.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class Client<TMessage extends GeneratedMessageV3> implements Runnable, BackgroundRunner {

    /**
     * The internet address that this client will connect to.
     */
    protected final InetAddress address;

    /**
     * The port that this client will connect to.
     */
    protected final int port;

    /**
     * Indicates whether this client will be listening for incoming messages.
     */
    protected final AtomicBoolean listening = new AtomicBoolean(true);

    /**
     * Indicates whether this client is connected to the server.
     */
    protected final AtomicBoolean connected = new AtomicBoolean(false);

    /**
     * The execution delay between calls to the process() method.
     */
    protected int executionDelay = 0;

    /**
     * The client's message parser.
     */
    protected Parser<TMessage> messageParser;

    /**
     * Constructs a new Client.
     * @param messageClass The class of the message type.
     * @param ipAddress Text-based internet address to which this client will connect.
     * @param port The port of this client.
     * @throws UnknownHostException Thrown when an invalid IP address was provided.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(Class<TMessage> messageClass, String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        this.address = InetAddress.getByName(ipAddress);
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + "). The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
        try {
            final Field field = messageClass.getField("PARSER");
            this.messageParser = (Parser<TMessage>) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a new Client.
     * @param inetAddress The IP address to which this client will connect.
     * @param port The port of this client.
     * @throws PortUnreachableException Thrown when an invalid port was provided.
     */
    public Client(Class<TMessage> messageClass, InetAddress inetAddress, int port) throws PortUnreachableException {
        this.address = inetAddress;
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + "). The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
        try {
            final Field field = messageClass.getField("PARSER");
            this.messageParser = (Parser<TMessage>) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
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
     * Retrieves the message parser.
     * @return Returns a Parser of type TMessage.
     */
    public Parser<TMessage> getMessageParser() {
        return messageParser;
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
     * Retrieves the execution delay.
     * @return Returns integer.
     */
    public int getExecutionDelay() {
        return executionDelay;
    }

    /**
     * Sets the execution delay.
     * @param executionDelay The execution delay in milliseconds.
     */
    public void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    /**
     * Executes code handling the case where the client may not be able to connect to the server.
     * Does nothing - must be implemented by extending classes if needed.
     */
    public void onConnectionRefused() { }

//    /**
//     * Sends data and blocks execution until a response is received.
//     * @param data The data to send.
//     */
//    public abstract void sendAndReceive(String data, Receivable receivable);

    /**
     * Runs the client.
     */
    @Override
    public final void run() {
        initialize();
        while (isConnected()) {
            doContinuously();
            if (executionDelay > 0) {
                try {
                    Thread.sleep(executionDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Starts the client.
     * @return Returns the thread running this client.
     */
    public final Thread start() {
        Thread thread = new Thread(this, getClass().getSimpleName());
        thread.start();
        return thread;
    }

    @Override
    public void initialize() { }

    @Override
    public void doContinuously() { }

    /**
     * Stops the client.
     */
    public abstract void stop() throws IOException;

}
