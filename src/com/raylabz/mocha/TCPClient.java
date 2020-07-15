package com.raylabz.mocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Nicos Kasenides
 * @version 1.0.0
 * Defines functionality for a TCP Client.
 */
public abstract class TCPClient extends Client {

    /**
     * The client's socket.
     */
    private final Socket socket;

    /**
     * The client's output writer.
     */
    private final PrintWriter writer;

    /**
     * The client's input reader.
     */
    private final BufferedReader reader;

    /**
     * Constructs a TCP Client.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
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
                e.printStackTrace();
            }
        });
        receptionThread.start();
    }

    /**
     * Retrieves the client's socket.
     * @return Returns a Socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Defines functionality which sends data to a server.
     * @param data The data to send to the server.
     */
    @Override
    public void send(String data) {
        writer.println(data);
    }

}
