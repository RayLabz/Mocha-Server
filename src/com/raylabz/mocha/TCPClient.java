package com.raylabz.mocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class TCPClient extends Client {

    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;

    public TCPClient(String ipAddress, int port) throws IOException {
        super(ipAddress, port);
        this.socket = new Socket(getAddress(), getPort());
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Thread receptionThread = new Thread(() -> {
            String input;
            try {
                while ((input = reader.readLine()) != null && isListening()) {
                    onReceive(input);
                }
            } catch (IOException e) {
                System.err.println("Error receiving: " + e.getMessage());
            }
        });
        receptionThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void send(String data) {
        writer.println(data);
    }

    public abstract void onReceive(String data);

}
