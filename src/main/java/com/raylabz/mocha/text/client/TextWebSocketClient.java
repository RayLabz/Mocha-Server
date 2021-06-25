package com.raylabz.mocha.text.client;

import com.neovisionaries.ws.client.*;
import com.raylabz.mocha.BackgroundProcessor;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a WebSocket client.
 */
public abstract class TextWebSocketClient implements Runnable, TextMessageBroker, BackgroundProcessor {

    /**
     * The client's name.
     */
    private final String name;

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
     * @param name The client's name.
     * @param endpointURI The client's socket URI.
     * @throws IOException Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public TextWebSocketClient(String name, String endpointURI) throws IOException, WebSocketException {
        this.name = name;
        if (!endpointURI.startsWith("ws://") && !endpointURI.startsWith("wss://")) {
            throw new SocketException("WebSocket address must start with either 'ws://' or 'wss://'.");
        }
        else {
            this.endpointURI = endpointURI;
            socket = new WebSocketFactory().createSocket(endpointURI);
            socket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    if (isListening()) {
                        onReceive(text);
                    }
                }
            });
            socket.connect();
            Thread receptionThread = new Thread(() -> {
                while (isEnabled()) { }
            });
            receptionThread.start();
        }
    }

    /**
     * Creates a new WebSocketClient
     * @param endpointURI The client's socket URI.
     * @throws IOException Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public TextWebSocketClient(String endpointURI) throws IOException, WebSocketException {
        this.name = this.getClass().getName();
        if (!endpointURI.startsWith("ws://") && !endpointURI.startsWith("wss://")) {
            throw new SocketException("WebSocket address must start with either 'ws://' or 'wss://'.");
        }
        else {
            this.endpointURI = endpointURI;
            socket = new WebSocketFactory().createSocket(endpointURI);
            socket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    if (isListening()) {
                        onReceive(text);
                    }
                }
            });
            socket.connect();
            Thread receptionThread = new Thread(() -> {
                while (isEnabled()) { }
            });
            receptionThread.start();
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
    public void process() {
        //Do nothing, implemented by derived class.
    }

    @Override
    public void initialize() {
        //Do nothing, implemented by derived class.
    }

    @Override
    public final void run() {
        initialize();
        while (isEnabled()) {
            process();
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
     * Stops the client.
     */
    public final void stop() {
        setEnabled(false);
        setListening(false);
        socket.disconnect();
    }

    /**
     * Sends data to the web socket.
     * @param data The data to send to the server.
     */
    @Override
    public final void send(String data) {
        if (isEnabled()) {
            socket.sendText(data);
        }
    }

}
