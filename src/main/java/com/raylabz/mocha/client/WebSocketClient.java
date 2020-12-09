package com.raylabz.mocha.client;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import com.neovisionaries.ws.client.*;

import java.io.*;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a WebSocket client.
 */
public abstract class WebSocketClient<TMessage extends GeneratedMessageV3> implements Runnable, MessageBroker<TMessage>, BackgroundRunner {

    /**
     * The client's name.
     */
    private final String name;

    /**
     * A parser for this client's message type.
     */
    private final Parser<TMessage> messageParser;

    /**
     * The web socket endpoint URI.
     */
    private final String endpointURI;

    /**
     * The client's socket.
     */
    private final WebSocket socket;

    /**
     * Indicates if the client is running or not.
     */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /**
     * Indicates if the client is listening for incoming messages.
     */
    private final AtomicBoolean listening =  new AtomicBoolean(true);

    /**
     * The execution delay between calls to the process() method.
     */
    private int executionDelay = 0;

    /**
     * Creates a new WebSocketClient
     * @param messageClass The message class.
     * @param name The client's name.
     * @param endpointURI The client's socket URI.
     * @throws IOException Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public WebSocketClient(String name, Class<TMessage> messageClass, String endpointURI) throws IOException, WebSocketException {
        this.name = name;
        if (!endpointURI.startsWith("ws://") && !endpointURI.startsWith("wss://")) {
            throw new SocketException("WebSocket address must start with either 'ws://' or 'wss://'.");
        }
        else {
            this.endpointURI = endpointURI;
            socket = new WebSocketFactory().createSocket(endpointURI);
            socket.addListener(new WebSocketAdapter() {
                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    if (isListening()) {
                        DataInputStream reader = new DataInputStream(new ByteArrayInputStream(binary));

                        final int size = reader.readInt();
                        final byte[] actualData = new byte[size];
                        reader.read(actualData, 0, size);

                        final TMessage message = messageParser.parseFrom(actualData);
                        onReceive(message);
                    }
                }
            });
            socket.connect();
            Thread receptionThread = new Thread(() -> {
                while (isEnabled()) { }
            });
            receptionThread.start();
        }
        try {
            final Field field = messageClass.getField("PARSER");
            this.messageParser = (Parser<TMessage>) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the name of the client.
     * @return Returns a String.
     */
    public final String getName() {
        return name;
    }

    /**
     * Retrieves the client's socket
     * @return Returns a WebSocket.
     */
    public final WebSocket getSocket() {
        return socket;
    }

    /**
     * Retrieves the status of this client.
     * @return Returns true if the client is running, false otherwise.
     */
    public final boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Sets the status of this client.
     * @param enabled Set to true for running, false for not running.
     */
    public final void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    /**
     * Retrieves the client's websocket endpoint URI.
     * @return Returns a String.
     */
    public final String getEndpointURI() {
        return endpointURI;
    }

    /**
     * Retrieves whether or not the client is listening.
     * @return Returns true if the client is listening, false otherwise.
     */
    public final boolean isListening() {
        return listening.get();
    }

    /**
     * Sets the listening status of this client.
     * @param listening Set to true for listening, false for not listening.
     */
    public final void setListening(boolean listening) {
        this.listening.set(listening);
    }

    /**
     * Retrieves the execution delay.
     * @return Returns integer.
     */
    public final int getExecutionDelay() {
        return executionDelay;
    }

    /**
     * Sets the execution delay.
     * @param executionDelay The execution delay in milliseconds.
     */
    public final void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    @Override
    public final void run() {
        initialize();
        while (isEnabled()) {
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
     * Sends a message to the web socket.
     * @param message The message to send to the server.
     */
    @Override
    public final void send(TMessage message) {
        if (isEnabled()) {
            try {
                byte[] messageBytes = message.toByteArray();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(messageBytes.length);
                dos.write(messageBytes);
                dos.flush();
                dos.close();
                baos.close();
                socket.sendBinary(baos.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Starts the client.
     * @return Returns the thread running the client runnable.
     */
    public final Thread start() {
        final Thread thread = new Thread(this, name + "-WebSocketClient");
        thread.start();
        return thread;
    }

    /**
     * Stops the client.
     */
    public final void stop() {
        setListening(false);
        setEnabled(false);
        socket.sendClose(WebSocketCloseCode.NORMAL);
    }

}
