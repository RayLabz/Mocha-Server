package com.raylabz.mocha.client;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.bytesurge.stream.StreamWriter;
import com.raylabz.mocha.message.Message;
import org.omg.CORBA.portable.Streamable;

import java.io.*;
import java.net.*;

/**
 * Defines functionality for a TCP Client.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class TCPBClient extends Client implements MessageBroker<Message> {

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
                    //Read the header:
                    final int size = reader.readInt();

                    //Get the rest of the data:
                    final byte[] data = new byte[size];
                    for (int i = 0; i < size; i++) {
                        data[i] = reader.readByte();
                    }

                    StreamReader reader = new StreamReader(Message.getSchema(size), data);
                    final byte[] bytes = reader.readByteArray();

                    Message message = new Message(bytes);

                    //Handle the message:
                    onReceive(message);
                }
            } catch (UnknownHostException e) {
                setListening(false);
                setConnected(false);
                onConnectionRefused();
            } catch (IOException e) {
                System.err.println("Error receiving: " + e.getMessage());
                e.printStackTrace();
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
    public TCPBClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
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
     * Defines functionality which sends message to a server.
     *
     * @param message The message to send to the server.
     */
    @Override
    public void send(Message message) {
        if (isConnected()) {
            try {
                StreamWriter streamWriter = new StreamWriter(message.toSchema());
                streamWriter.writeArray((ArrayContainer) message.toContainer());
                streamWriter.close();
                final byte[] bytes = streamWriter.getBytes();
                writer.writeInt(bytes.length); //Forward total message size declaration
                writer.write(bytes);
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

    @Override
    public void initialize() {
    }

    @Override
    public void process() {
    }

}