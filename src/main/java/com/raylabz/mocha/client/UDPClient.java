package com.raylabz.mocha.client;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;

/**
 * Defines functionality for a UDP Client.
 *
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class UDPClient<TMessage extends GeneratedMessageV3> extends Client<TMessage> implements MessageBroker<TMessage> {

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
    private final Thread receptionThread;

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
                            final byte[] actualData = new byte[size];
                            reader.read(actualData, 0, size);

                            final TMessage message = messageParser.parseFrom(actualData);

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
     * @param ipAddress The IP address that this client will connect to.
     * @param port      The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException      Thrown when the client's socket cannot be instantiated.
     */
    public UDPClient(Class<TMessage> messageClass, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(messageClass, ipAddress, port);
        try {
            this.socket = new DatagramSocket();
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
    public final void send(final TMessage message) {
        if (isConnected()) {
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(byteStream);
                byte[] data = message.toByteArray();
                dos.writeInt(data.length);
                dos.write(data);
                dos.close();
                byteStream.close();
                DatagramPacket packet = new DatagramPacket(byteStream.toByteArray(), byteStream.size(), getAddress(), getPort());
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
