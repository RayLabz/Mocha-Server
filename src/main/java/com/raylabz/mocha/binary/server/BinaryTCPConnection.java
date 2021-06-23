package com.raylabz.mocha.binary.server;

import com.raylabz.mocha.logger.Logger;
import com.raylabz.mocha.text.server.TextTCPReceivable;

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
public class BinaryTCPConnection implements Runnable {

    /**
     * The connection's socket.
     */
    private final Socket socket;

    /**
     * The connection's output writer.
     */
    private final OutputStream writer;

    /**
     * The connections input reader.
     */
    private final InputStream reader;

    /**
     * Determines if this TCP connection is enabled.
     */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /**
     * The TCPReceivable of this connection, which defines what happens once data is received.
     * Important note: TCPReceivables are the same object for all TCPConnections of the same TCPHandler.
     */
    private final BinaryTCPReceivable receivable;

    /**
     * Constructs a new TCPConnection.
     * @param socket The connection's socket.
     * @param receivable The connection's receivable instance.
     * @throws IOException Thrown when the socket's input reader cannot be fetched.
     */
    public BinaryTCPConnection(Socket socket, BinaryTCPReceivable receivable) throws IOException {
        this.socket = socket;
        this.writer = socket.getOutputStream();
        this.reader = socket.getInputStream();
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
     * Sends data to the client on the other end of the TCP connection.
     * @param data The data to send.
     */
    public final void send(final byte[] data) {
        try {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            e.printStackTrace();
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
        while (isEnabled()) {
            try {
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                for (int nChunk = reader.read(buffer); nChunk != -1; nChunk = reader.read(buffer)) {
                    dataStream.write(buffer, 0, nChunk);
                }
                dataStream.close();
                byte[] inputData = dataStream.toByteArray();

                receivable.onReceive(this, inputData);
            } catch (SocketException se) {
                if (!isEnabled()) {
                    System.out.println("Lost connection to TCP client: " + getInetAddress() + ".");
                    Logger.logInfo("Lost connection to TCP client: " + getInetAddress() + ".");
                } else {
                    System.err.println("[TCP " + getInetAddress().toString() + ":" + getPort() + "]" + "Error: " + se.getMessage());
                    Logger.logError(se.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                Logger.logError(e.getMessage());
            }
        }
    }

}
