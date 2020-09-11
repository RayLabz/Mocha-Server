package com.raylabz.mocha.client;

import com.raylabz.mocha.server.Mocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Defines functionality for a TCP Client.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class TCPClient extends Client {

    /**
     * The client's socket.
     */
    private Socket socket;

    /**
     * The client's output writer.
     */
    private PrintWriter writer;

    /**
     * The client's input reader.
     */
    private BufferedReader reader;

    /**
     * Constructs a TCP Client.
     *
     * @param name The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public TCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
        try {
            this.socket = new Socket(getAddress(), getPort());
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            setConnected(true);
        } catch (ConnectException ce) {
            setListening(false);
            setConnected(false);
            onConnectionRefused();
        }

        Thread receptionThread = new Thread(() -> {
            if (isConnected()) {
                String input;
                try {
                    while ((input = reader.readLine()) != null && isListening() && isConnected()) {
                        onReceive(input);
                    }
                } catch (SocketException se) {
                    setListening(false);
                    setConnected(false);
                    onConnectionRefused();
                } catch (IOException e) {
                    System.err.println("Error receiving: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, name + "-Listener");
        receptionThread.start();
    }

    /**
     * Retrieves the client's socket.
     *
     * @return Returns a Socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Defines functionality which sends data to a server.
     *
     * @param data The data to send to the server.
     */
    @Override
    public void send(String data) {
        if (isConnected()) {
            writer.println(data);
        }
    }

}
