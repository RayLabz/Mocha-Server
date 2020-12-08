package com.raylabz.mocha.client;

import com.google.protobuf.GeneratedMessageV3;

import java.io.*;
import java.net.*;

/**
 * Defines functionality for a TCP Client.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class TCPClient<TMessage extends GeneratedMessageV3> extends Client<TMessage> implements MessageBroker<TMessage> {

    /**
     * The client's socket.
     */
    private Socket socket;

    /**
     * The client's output writer.
     */
    private DataOutputStream writer;

    /**
     * The client's input reader.
     */
    private DataInputStream reader;

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
            try {
                if (isConnected()) {
                    final int numOfBytes = reader.readInt();
                    byte[] data = new byte[numOfBytes];
                    reader.read(data, 0, numOfBytes);
                    final TMessage message = messageParser.parseFrom(data);
                    onReceive(message);
                }
            } catch (UnknownHostException e) {
                setListening(false);
                setConnected(false);
                onConnectionRefused();
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Error receiving: " + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    /**
     * Constructs a TCP Client.
     *
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public TCPClient(Class<TMessage> messageClass, String ipAddress, int port) throws IOException {
        super(messageClass, ipAddress, port);
        try {
            this.socket = new Socket(getAddress(), getPort());
            writer = new DataOutputStream(socket.getOutputStream());
            reader = new DataInputStream(socket.getInputStream());
            setConnected(true);
        } catch (ConnectException ce) {
            setListening(false);
            setConnected(false);
            onConnectionRefused();
        }

        receptionThread = new Thread(receptionThreadRunnable, getClass().getSimpleName() + "-Listener");
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
     * Defines functionality which sends message to a server.
     *
     * @param message The message to send to the server.
     */
    @Override
    public void send(TMessage message) {
        if (isConnected()) {
            try {
                byte[] messageBytes = message.toByteArray();
                writer.writeInt(messageBytes.length);
                writer.write(messageBytes);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() throws IOException {
        setConnected(false);
        setListening(false);
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
    }

}
