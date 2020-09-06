package com.raylabz.mocha.client;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.net.SocketException;

public abstract class WebSocketClient implements Runnable, MessageBroker {

    private final String endpointURI;
    private final WebSocket socket;
    private boolean running = true;
    private boolean listening = true;

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

    public WebSocket getSocket() {
        return socket;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getEndpointURI() {
        return endpointURI;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    @Override
    public void send(String data) {
        socket.sendText(data);
    }

}
