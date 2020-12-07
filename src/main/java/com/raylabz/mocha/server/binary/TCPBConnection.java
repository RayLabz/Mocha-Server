package com.raylabz.mocha.server.binary;

import com.raylabz.bytesurge.container.ArrayContainer;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.bytesurge.stream.StreamWriter;
import com.raylabz.mocha.logger.Logger;
import com.raylabz.mocha.message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a TCP connection to a client.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public class TCPBConnection implements Runnable {

    /**
     * The connection's socket.
     */
    private final Socket socket;

    /**
     * The connection's output writer.
     */
    private final DataOutputStream writer;

    /**
     * The connections input reader.
     */
    private final DataInputStream reader;

    /**
     * Determines if this TCP connection is enabled.
     */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /**
     * The TCPReceivable of this connection, which defines what happens once data is received.
     * Important note: TCPReceivables are the same object for all TCPConnections of the same TCPHandler.
     */
    private final TCPBReceivable receivable;

    /**
     * Constructs a new TCPConnection.
     * @param socket The connection's socket.
     * @param receivable The connection's receivable instance.
     * @throws IOException Thrown when the socket's input reader cannot be fetched.
     */
    public TCPBConnection(Socket socket, TCPBReceivable receivable) throws IOException {
        this.socket = socket;
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.reader = new DataInputStream(socket.getInputStream());
        this.receivable = receivable;
    }

    /**
     * Retrieves if this TCP connection is running or not.
     * @return Returns true if the connection is running, false otherwise.
     */
    public boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Starts or stops the connection.
     * @param enabled Provide true to start the connection, false to stop.
     */
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    /**
     * Sends a message to the client on the other end of the TCP connection.
     * @param message The message to send.
     */
    public final void send(final Message message) throws RuntimeException {
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

    /**
     * Retrieves the connection's port.
     * @return Returns an integer (port number).
     */
    public final int getPort() {
        return socket.getPort();
    }

    /**
     * Retrieves the internet address of this connection.
     * @return Returns an internet address.
     */
    public final InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    /**
     * Retrieves the connection's socket.
     * @return Returns a Socket.
     */
    public final Socket getSocket() {
        return socket;
    }

    /**
     * Defines functionality executed by this TCPConnection.
     * The TCP connections constantly listen for incoming data and act on it using their receivables.
     */
    @Override
    public final void run() {
        try {
            while (isEnabled()) {

                //Read the header:
                final int size = reader.readInt();
                System.out.println("size = " + size);

                //Get the rest of the data:
                final byte[] data = new byte[size];
                for (int i = 0; i < size; i++) {
                    data[i] = reader.readByte();
                }

                StreamReader reader = new StreamReader(Message.getSchema(size), data);
                final byte[] bytes = reader.readByteArray();

                Message message = new Message(bytes);

                //Handle the message:
                receivable.onReceive(this, message);
            }
        } catch (SocketException se) {
            if (!isEnabled()) {
                System.out.println("Lost connection to TCP client: " + getInetAddress() + ".");
                Logger.logInfo("Lost connection to TCP client: " + getInetAddress() + ".");
            }
            else {
                System.err.println("[TCP " + getInetAddress().toString() + ":" + getPort() + "]" + "Error: " + se.getMessage());
                Logger.logError(se.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

}