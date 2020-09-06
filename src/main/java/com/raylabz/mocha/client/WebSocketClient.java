package com.raylabz.mocha.client;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.net.SocketException;

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
    private boolean running = true;

    /**
     * Indicates if the client is listening for incoming messages.
     */
    private boolean listening = true;

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
                    if (listening) {
                        onReceive(text);
                    }
                }
            });
            socket.connect();
            Thread receptionThread = new Thread(() -> {
                while (running) { }
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
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets the status of this client.
     * @param running Set to true for running, false for not running.
     */
    public void setRunning(boolean running) {
        this.running = running;
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
        return listening;
    }

    /**
     * Sets the listening status of this client.
     * @param listening Set to true for listening, false for not listening.
     */
    public void setListening(boolean listening) {
        this.listening = listening;
    }

    /**
     * Sends data to the web socket.
     * @param data The data to send to the server.
     */
    @Override
    public void send(String data) {
        socket.sendText(data);
    }

}
