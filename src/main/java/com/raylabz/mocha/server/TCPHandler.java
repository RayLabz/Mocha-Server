package com.raylabz.mocha.server;

import com.google.protobuf.GeneratedMessageV3;
import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages TCP connections for the server for a specified port.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public class TCPHandler<TMessage extends GeneratedMessageV3> implements Runnable {

    /**
     * The server that this TCP handler belongs to.
     */
    private Server<TMessage> server;

    /**
     * The TCP port handled by the handler.
     */
    private final int port;

    /**
     * The server socket of the handler.
     */
    private ServerSocket serverSocket;

    /**
     * Determines if the handler is enabled or not.
     */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /**
     * A list of TCPConnections handled by this handler.
     */
    private final Vector<TCPConnection<TMessage>> tcpConnections = new Vector<>();

    /**
     * A list of TCPConnection threads handled by this handler.
     */
    private final Vector<Thread> tcpConnectionThreads = new Vector<>();

    /**
     * The handler's TCP receivable, which determines what its TCPConnections will execute once they receive data.
     */
    private final TCPReceivable<TMessage> receivable;

    private final HashSet<InetAddress> tcpPeers = new HashSet<>();

    /**
     * Constructs a TCPHandler.
     * @param port The TCPHandler's port number.
     * @param receivable The TCPHandler's receivable.
     */
    public TCPHandler(int port, final TCPReceivable<TMessage> receivable) {
        this.port = port;
        this.receivable = receivable;
    }

    /**
     * Retrieves the server of this TCPHandler.
     * @return Returns a Server.
     */
    protected Server<TMessage> getServer() {
        return server;
    }

    /**
     * Sets the server of this TCPHandler.
     * @param server A server
     */
    void setServer(Server<TMessage> server) {
        this.server = server;
    }

    /**
     * Retrieves the handler's port.
     * @return Returns an integer (port number).
     */
    public int getPort() {
        return port;
    }

    /**
     * Retrieves the handler's receivable.
     * @return Returns a TCPReceivable.
     */
    public TCPReceivable<TMessage> getReceivable() {
        return receivable;
    }

    /**
     * Retrieves the handler's ServerSocket.
     * @return Returns a ServerSocket.
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Checks if the handler is enabled.
     * @return Returns true if the handler is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return enabled.get();
    }

    /**
     * Enables or disables the handler.
     * @param enabled Set to true to enable the handler, false to disable.
     * @throws RuntimeException when the socket cannot be closed.
     */
    public void setEnabled(boolean enabled) throws RuntimeException {
        this.enabled.set(enabled);
        if (!enabled) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Retrieves the handler's TCPConnections list.
     * @return Returns a Vector of TCPConnection.
     */
    public Vector<TCPConnection<TMessage>> getTcpConnections() {
        return tcpConnections;
    }

    /**
     * Retrieves a list of threads running the handlers TCPConnections.
     * @return Returns a Vector of Thread.
     */
    public Vector<Thread> getTcpConnectionThreads() {
        return tcpConnectionThreads;
    }

    /**
     * Removes a TCP connection.
     * @param tcpConnection The TCP connection to remove.
     */
    void removeTCPConnection(TCPConnection<TMessage> tcpConnection) {
        tcpConnection.setEnabled(false);
        tcpConnections.remove(tcpConnection);
    }

    /**
     * Removes a TCP connection thread.
     * @param tcpThread The TCP connection thread to remove.
     */
    void removeTCPConnectionThread(Thread tcpThread) {
        tcpConnectionThreads.remove(tcpThread);
    }

    /**
     * Removes all TCP connections and connection threads.
     */
    void removeTCPConnectionsAndThreads() {
        try {
            for (TCPConnection<TMessage> connection : tcpConnections) {
                connection.setEnabled(false);
                connection.getSocket().close();
            }
            tcpConnectionThreads.clear();
            tcpConnections.clear();
        }
        catch (SocketException se) {
            if (!isEnabled()) {
                System.out.println("Stopped listening to TCP port " + port + ".");
                Logger.logInfo("Stopped listening to TCP port " + port + ".");
            }
        }
        catch (IOException e) {
            System.err.println("[TCP " + port + "]" + "Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     * @param message The message to broadcast.
     */
    public void broadcast(TMessage message) {
        if (isEnabled()) {
            for (TCPConnection<TMessage> connection : tcpConnections) {
                connection.send(message);
            }
        }
        else {
            System.err.println("[TCP " + port + "]" + "Error - Cannot broadcast. TCPHandler [" + getPort() + "] disabled");
            Logger.logError("[TCP " + port + "]" + "Error - Cannot broadcast. TCPHandler [" + getPort() + "] disabled");
        }
    }

    /**
     * Retrieves the set of peers for this handler.
     * @return Returns a HashSet of InetAddress.
     */
    public HashSet<InetAddress> getTcpPeers() {
        return tcpPeers;
    }

    /**
     * Multicasts a message to selected clients based on their IP address.
     * @param message The message to send.
     * @param ipAddresses A list of IP addresses.
     */
    public void multicast(TMessage message, ArrayList<InetAddress> ipAddresses) {
        if (isEnabled()) {
            for (TCPConnection<TMessage> connection : tcpConnections) {
                if (ipAddresses.contains(connection.getInetAddress())) {
                    connection.send(message);
                }
            }
        }
        else {
            System.err.println("[TCP " + port + "]" + "Error - Cannot multicast. TCPHandler [" + getPort() + "] disabled");
            Logger.logError("[TCP " + port + "]" + "Error - Cannot multicast. TCPHandler [" + getPort() + "] disabled");
        }
    }

    /**
     * Defines what will be executed by the handler once it starts.
     * A TCPHandler does the following:
     * 1) Instantiates its ServerSocket on a given port.
     * 2) Listens for connections to this port and accepts them.
     * 3) Once a connection is accepted, the handler dispatches a new TCPConnection object/thread,
     * which handles that specific connection.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for connections on TCP port " + port + ".");
            Logger.logInfo("Waiting for connections on TCP port " + port + ".");
            while (isEnabled()) {
                Socket socket = serverSocket.accept();

                String socketAddress = socket.getInetAddress().toString();
                if (server.getSecurityMode() == SecurityMode.BLACKLIST && server.getBlacklist().contains(socket.getInetAddress())) {
                    socket.close();
                    System.out.println("Banned IP address " + socketAddress + " attempted to connect on TCP port " + port + " but was disconnected.");
                    Logger.logWarning("Banned IP address " + socketAddress + " attempted to connect on TCP port " + port + " but was disconnected.");
                }
                else if (server.getSecurityMode() == SecurityMode.WHITELIST && !server.getWhitelist().contains(socket.getInetAddress())) {
                    socket.close();
                    System.out.println("Non-whitelisted IP address " + socketAddress + " attempted to connect on TCP port " + port + " but was disconnected.");
                    Logger.logWarning("Non-whitelisted IP address " + socketAddress + " attempted to connect on TCP port " + port + " but was disconnected.");
                }
                else {
                    TCPConnection<TMessage> tcpConnection = new TCPConnection<>(server, socket, receivable);
                    tcpConnections.add(tcpConnection);
                    if (tcpPeers.add(socket.getInetAddress())) {
                        System.out.println("New TCP connection on port " + port + " from IP: " + socket.getInetAddress());
                        Logger.logInfo("New TCP connection on port " + port + " from IP: " + socket.getInetAddress());
                    }
                    Thread t = new Thread(tcpConnection, "TCP-Thread-" + socket.getInetAddress().toString());
                    tcpConnectionThreads.add(t);
                    t.start();
                }
            }
            removeTCPConnectionsAndThreads();
        } catch (SocketException se) {
            if (!isEnabled()) {
                System.out.println("Stopped listening to TCP port " + port + ".");
                Logger.logInfo("Stopped listening to TCP port " + port + ".");
            }
        } catch (IOException e) {
            System.err.println("[TCP " + port + "]" + "Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }

    }

}
