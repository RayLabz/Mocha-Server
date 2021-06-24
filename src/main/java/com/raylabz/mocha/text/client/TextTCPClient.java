package com.raylabz.mocha.text.client;

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
public abstract class TextTCPClient extends TextClient {

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
     * A thread listening to messages from the server.
     */
    private Thread receptionThread;

    /**
     * The runnable ran by receptionThread.
     */
    private final Runnable receptionThreadRunnable = new Runnable() {
        @Override
        public void run() {
            if (isConnected()) {
                String input;
                try {
                    while ((input = reader.readLine()) != null && isListening() && isConnected()) {
                        onReceive(input);
                    }
                } catch (SocketException se) {
//                    if (!unblock) {
                        setListening(false);
                        setConnected(false);
                        onConnectionRefused(se);
//                    }
                } catch (IOException e) {
                    System.err.println("Error receiving: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public TextTCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
        try {
            this.socket = new Socket(getAddress(), getPort());
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            setConnected(true);
        } catch (ConnectException ce) {
            setListening(false);
            setConnected(false);
            onConnectionRefused(ce);
        }

        receptionThread = new Thread(receptionThreadRunnable, name + "-Listener");
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

    @Override
    public void stop() {
        setConnected(false);
        setListening(false);
        try {
            socket.shutdownInput();
        } catch (IOException e) { }
    }

    //    @Override
//    public void sendAndReceive(String data, Receivable receivable) {
//        unblock = true;
//        try {
//            socket.close();
//            receptionThread.join();
//            this.socket = new Socket(getAddress(), getPort());
//            writer = new PrintWriter(socket.getOutputStream(), true);
//            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (Exception e) {
//            //Do nothing.
//        }
//
//        if (isConnected()) {
//            writer.println(data);
//            String input;
//            try {
//                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                input = reader.readLine();
//                receivable.onReceive(input);
//                setListening(true);
//            } catch (SocketException se) {
//                setListening(false);
//                setConnected(false);
//                onConnectionRefused();
//            } catch (IOException e) {
//                System.err.println("Error receiving: " + e.getMessage());
//                e.printStackTrace();
//                setListening(true);
//            }
//        }
//
//        unblock = false;
//
//        receptionThread = new Thread(receptionThreadRunnable, getName() + "-Listener");
//        receptionThread.start();
//    }

}
