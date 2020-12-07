package com.raylabz.mocha.client;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.bytesurge.stream.StreamWriter;
import com.raylabz.mocha.message.Message;

import java.io.*;
import java.net.*;

/**
 * Defines functionality for a UDP Client.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class UDPBClient extends Client implements MessageBroker<Message> {

    /**
     * The client's socket.
     */
    private DatagramSocket socket;

    /**
     * The client's buffer.
     */
    private final byte[] buffer = new byte[65535];

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
            if (isConnected() && !socket.isClosed()) {
                while (isConnected() && !socket.isClosed()) {
                    if (isListening()) {
                        try {
                            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            final byte[] data = packet.getData();
                            DataInputStream reader = new DataInputStream(new ByteArrayInputStream(data));

                            final int size = reader.readInt();

                            //Get the rest of the data:
                            final byte[] actualData = new byte[size];
                            for (int i = 0; i < size; i++) {
                                data[i] = reader.readByte();
                            }

                            StreamReader reader2 = new StreamReader(Message.getSchema(size), actualData);
                            final byte[] bytes = reader2.readByteArray();

                            Message message = new Message(bytes);

                            //Handle the message:
                            onReceive(message);

                        } catch (ConnectException ce) {
//                            if (!unblock) {
                            setListening(false);
                            setConnected(false);
                            onConnectionRefused();
//                            }
                        } catch (IOException e) {
//                            if (!unblock) {
                            System.err.println("Error receiving: " + e.getMessage());
                            e.printStackTrace();
//                            }
                        }
                    }
                }
            } else {
                onConnectionRefused();
            }
        }
    };

    /**
     * Constructs a new UDPClient.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address that this client will connect to.
     * @param port      The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException      Thrown when the client's socket cannot be instantiated.
     */
    public UDPBClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
        try {
            this.socket = new DatagramSocket();
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
     * @return Returns a DatagramSocket.
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to send to the server.
     */
    @Override
    public final void send(final Message message) {
        if (isConnected()) {
            try {
                StreamWriter streamWriter = new StreamWriter(message.toSchema());
                streamWriter.writeArray((ArrayContainer) message.toContainer());
                streamWriter.close();
                final byte[] bytes = streamWriter.getBytes();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(byteStream);
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.close();
                byteStream.close();
                DatagramPacket packet = new DatagramPacket(byteStream.toByteArray(), bytes.length, getAddress(), getPort());
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        setConnected(false);
        setListening(false);
        socket.close();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void process() {
    }

    //    @Override
//    public void sendAndReceive(String data, Receivable receivable) {
//
//        unblock = true;
//
//        try {
//            socket.close();
//            receptionThread.join();
//            this.socket = new DatagramSocket();
//        } catch (Exception e) {
//            //Do nothing.
//        }
//
//        if (isConnected()) {
//            try {
//                final byte[] bytes = data.getBytes();
//                DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, getAddress(), getPort());
//                socket.send(sendPacket);
//
//                try {
//                    final DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
//                    socket.receive(receivePacket);
//                    final String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength());
//                    receivable.onReceive(receiveData);
//                } catch (ConnectException ce) {
//                    setListening(false);
//                    setConnected(false);
//                    onConnectionRefused();
//                } catch (IOException e) {
//                    System.err.println("Error receiving: " + e.getMessage());
//                    e.printStackTrace();
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        unblock = false;
//
//        receptionThread = new Thread(receptionThreadRunnable, getName()  + "-Listener");
//        receptionThread.start();
//
//    }

}
