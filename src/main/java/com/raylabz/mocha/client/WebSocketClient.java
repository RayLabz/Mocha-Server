package com.raylabz.mocha.client;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a WebSocket client.
 */
public abstract class WebSocketClient implements Runnable, MessageBroker {

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
     * Creates a new WebSocketClient
     * @param endpointURI The client's socket URI.
     * @throws IOException Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public WebSocketClient(String endpointURI) throws IOException, WebSocketException {
        if (!endpointURI.startsWith("ws://") || endpointURI.startsWith("wss://")) {
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
     * Retrieves the client's socket
     * @return Returns a WebSocket.
     */
    public WebSocket getSocket() {
        return socket;
    }

    /**
     * Retrieves the status of this client.
     * @return Returns true if the client is running, false otherwise.
     */
    public boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Sets the status of this client.
     * @param enabled Set to true for running, false for not running.
     */
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    /**
     * Retrieves the client's websocket endpoint URI.
     * @return Returns a String.
     */
    public String getEndpointURI() {
        return endpointURI;
    }

    /**
     * Retrieves whether or not the client is listening.
     * @return Returns true if the client is listening, false otherwise.
     */
    public boolean isListening() {
        return listening.get();
    }

    /**
     * Sets the listening status of this client.
     * @param listening Set to true for listening, false for not listening.
     */
    public void setListening(boolean listening) {
        this.listening.set(listening);
    }

    /**
     * Sends data to the web socket.
     * @param data The data to send to the server.
     */
    @Override
    public void send(String data) {
        if (isEnabled()) {
            socket.sendText(data);
        }
    }

}
