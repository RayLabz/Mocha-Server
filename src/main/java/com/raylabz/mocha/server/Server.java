package com.raylabz.mocha.server;

import com.raylabz.mocha.logger.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides functionality for a server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public abstract class Server implements Runnable {

    /**
     * The name of the server.
     */
    private final String name;

    /**
     * Whether the server is running or not.
     */
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Execution delay between calls to the process() method - In <b>MILLISECONDS</b>.
     */
    private int executionDelay = 0;

    /**
     * A list of UDP listeners for this server.
     */
    private final Vector<UDPConnection> udpHandlers = new Vector<>();

    /**
     * A list of threads running the UDP listeners of this server.
     */
    private final Vector<Thread> udpHandlerThreads = new Vector<>();

    /**
     * A list of TCP handlers for this server.
     */
    private final Vector<TCPHandler> tcpHandlers = new Vector<>();

    /**
     * A list of threads running the TCP handlers of this server.
     */
    private final Vector<Thread> tcpHandlerThreads = new Vector<>();

    /**
     * Constructs a new server.
     * @param name The name of the server.
     */
    public Server(String name) {
        this.name = name;
    }

    /**
     * Adds a UDP handler to the server.
     * @param udpConnection The UDP handler to add.
     * @return Returns true if the UDP handler was added, false otherwise.
     */
    public final boolean addUDPHandler(UDPConnection udpConnection) {
        for (UDPConnection p : udpHandlers) {
            if (p.getPort() == udpConnection.getPort()) {
                return false;
            }
        }
        udpHandlers.add(udpConnection);
        return true;
    }

    /**
     * Adds a TCP handler to the server.
     * @param tcpHandler The TCP handler to add.
     * @return Returns true if the TCP handler was added, false otherwise.
     */
    public final boolean addTCPHandler(TCPHandler tcpHandler) {
        for (TCPHandler h : tcpHandlers) {
            if (h.getPort() == tcpHandler.getPort()) {
                return false;
            }
        }
        tcpHandlers.add(tcpHandler);
        return true;
    }

    /**
     * Removes a TCP handler.
     * @param tcpHandler The TCP handler to remove.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeTCPHandler(TCPHandler tcpHandler) throws IOException {
        tcpHandler.setEnabled(false);
        tcpHandler.removeTCPConnectionsAndThreads();
        return tcpHandlers.remove(tcpHandler);
    }

    /**
     * Removes a TCP handler.
     * @param port The handler's port.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeTCPHandler(int port) {
        TCPHandler handlerToRemove = null;
        for (TCPHandler tcpHandler : tcpHandlers) {
            if (tcpHandler.getPort() == port) {
                handlerToRemove = tcpHandler;
                break;
            }
        }
        if (handlerToRemove != null) {
            handlerToRemove.setEnabled(false);
            handlerToRemove.removeTCPConnectionsAndThreads();
        }
        return tcpHandlers.remove(handlerToRemove);
    }

    /**
     * Removes all TCP handlers.
     */
    final void removeAllTCPHandlers() {
        for (TCPHandler tcpHandler : tcpHandlers) {
            tcpHandler.setEnabled(false);
            tcpHandler.removeTCPConnectionsAndThreads();
        }
        tcpHandlers.clear();
    }

    /**
     * Removes a UDP handler.
     * @param udpConnection The UDP handler to remove.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeUDPHandler(UDPConnection udpConnection) {
        udpConnection.setEnabled(false);
        return udpHandlers.remove(udpConnection);
    }

    /**
     * Removes a UDP handler.
     * @param port The handler's port.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeUDPHandler(int port) {
        UDPConnection handlerToRemove = null;
        for (UDPConnection h : udpHandlers) {
            if (h.getPort() == port) {
                handlerToRemove = h;
                break;
            }
        }
        if (handlerToRemove != null) {
            handlerToRemove.setEnabled(false);
        }
        return udpHandlers.remove(handlerToRemove);
    }

    /**
     * Removes all UDP handlers.
     */
    final void removeAllUDPHandlers() {
        for (UDPConnection udpConnection : udpHandlers) {
            udpConnection.setEnabled(false);
        }
        udpHandlers.clear();
    }

    /**
     * Checks if the server is running.
     * @return Returns true if the server is running, false otherwise.
     */
    public final boolean isRunning() {
        return running.get();
    }

    /**
     * Retrieves the execution delay in milliseconds.
     * @return Returns an integer.
     */
    public final int getExecutionDelay() {
        return executionDelay;
    }

    /**
     * Sets the execution delay.
     * @param executionDelay The delay in milliseconds.
     */
    public final void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    /**
     * Sets the server to be running (true) or not running (false).
     * This method further disables the server's UDP listeners and TCP handlers if the server is set to stop running.
     * @param running Set to true for running, false for not running.
     */
    public final void setRunning(boolean running) {
        this.running.set(running);
    }

    /**
     * Retrieves the list of UDP listeners for this server.
     * @return Returns a Vector of UDPConnection.
     */
    public final Vector<UDPConnection> getUdpHandlers() {
        return udpHandlers;
    }

    /**
     * Retrieves the list of threads running the server's UDP listeners.
     * @return Returns a Vector of Thread.
     */
    public final Vector<Thread> getUdpHandlerThreads() {
        return udpHandlerThreads;
    }

    /**
     * Retrieves the list of TCP handlers for this server.
     * @return Returns a Vector of TCPHandler.
     */
    public final Vector<TCPHandler> getTcpHandlers() {
        return tcpHandlers;
    }

    /**
     * Retrieves the list of threads running the server's TCP handlers.
     * @return Returns a Vector of Threads.
     */
    public final Vector<Thread> getTcpHandlerThreads() {
        return tcpHandlerThreads;
    }

    /**
     * Retrieves the server's name.
     * @return Returns a string.
     */
    public final String getName() {
        return name;
    }

    /**
     * Initializes the server.
     */
    public abstract void initialize();

    /**
     * Defines functionality that is executed by the server during its runtime.
     * Important note: This method is executed CONTINUOUSLY during the server's runtime. Make sure that this method
     * only contains necessary code that should continuously be executed.
     */
    public abstract void process();

    /**
     * Utility method which checks if the server is running before attempting to send data.
     * @return Returns true if the server is running, false otherwise.
     */
    private boolean validateRunningBeforeSend() {
        if (!isRunning()) {
            final String text = "Warning: Cannot send while the server is not running.";
            System.err.println(text);
            Logger.logWarning(text);
            return false;
        }
        return true;
    }

    /**
     * Sends data through TCP.
     * @param tcpConnection The TCPConnection to send the data through.
     * @param data The data.
     */
    public final void sendTCP(TCPConnection tcpConnection, final String data) {
        if (validateRunningBeforeSend()) {
            tcpConnection.send(data);
        }
    }

    /**
     * Sends data through TCP.
     * @param ipAddress The IP address to send the data to.
     * @param port The port to send the data through.
     * @param data The data.
     */
    public final void sendTCP(final String ipAddress, final int port, final String data) {
        if (validateRunningBeforeSend()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (port >= 0 && port <= 65535) {
                    for (TCPHandler h : tcpHandlers) {
                        if (h.getPort() == port) {
                            for (TCPConnection tcpConnection : h.getTcpConnections()) {
                                if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                    tcpConnection.send(data);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Send data through TCP.
     * @param inetAddress The internet address to send the data to.
     * @param port The port to send the data through.
     * @param data The data.
     */
    public final void sendTCP(final InetAddress inetAddress, final int port, final String data) {
        if (validateRunningBeforeSend()) {
            if (port >= 0 && port <= 65535) {
                for (TCPHandler h : tcpHandlers) {
                    if (h.getPort() == port) {
                        for (TCPConnection tcpConnection : h.getTcpConnections()) {
                            if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                tcpConnection.send(data);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends data through UDP.
     * @param udpConnection The UDPConnection to send the data to.
     * @param address The address of the client.
     * @param outPort The port of the client.
     * @param data The data.
     */
    public final void sendUDP(final UDPConnection udpConnection, InetAddress address, int outPort, final String data) {
        if (validateRunningBeforeSend()) {
            udpConnection.send(address, outPort, data);
        }
    }

    /**
     * Sends data through UDP.
     * @param ipAddress The IP address to send the data to.
     * @param port The port to tsend the data through.
     * @param data The data.
     */
    public final void sendUDP(final String ipAddress, final int port, final String data) {
        if (validateRunningBeforeSend()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (port >= 0 && port <= 65535) {
                    for (UDPConnection udpConnection : udpHandlers) {
                        if (udpConnection.getPort() == port && udpConnection.getInetAddress().equals(inetAddress)) {
                            udpConnection.send(inetAddress, port, data);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Broadcasts a given message to all of the peers of a particular UDP connection.
     * @param port The connection to broadcast the data to.
     * @param data The data to broadcast.
     */
    public final void broadcastUDP(final int port, final String data) {
        Vector<UDPConnection> udpListeners = getUdpHandlers();
        for (UDPConnection connection : udpListeners) {
            if (connection.getPort() == port) {
                connection.broadcast(data);
                break;
            }
        }
    }

    /**
     * Broadcasts a given message to all of the peer of a particular TCP connection.
     * @param port The connection to broadcast the data to.
     * @param data The data to broadcast.
     */
    public final void broadcastTCP(final int port, final String data) {
        final Vector<TCPHandler> tcpHandlers = getTcpHandlers();
        for (TCPHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.broadcast(data);
                break;
            }
        }
    }

    /**
     * Defines the runtime functionality of this server.
     * This method:
     * 1) Initializes the server using initialize().
     * 2) Creates and starts threads for each UDPConnection and TCPHandler.
     * 3) Continuously executes the process() method while the server is running.
     *
     */
    @Override
    public final void run() {
        System.out.println("Server '" + name + "' started.");
        Logger.logInfo("Server '" + name + "' started.");
        initialize();

        for (UDPConnection udpConnection : udpHandlers) {
            Thread t = new Thread(udpConnection, "UDP-Handler-Thread-Port-" + udpConnection.getPort());
            udpHandlerThreads.add(t);
            t.start();
        }
        for (TCPHandler tcpHandler : tcpHandlers) {
            Thread t = new Thread(tcpHandler, "TCP-Handler-Thread-Port-" + tcpHandler.getPort());
            tcpHandlerThreads.add(t);
            t.start();
        }

        while (isRunning()) {
            process();
            if (executionDelay > 0) {
                try {
                    Thread.sleep(executionDelay);
                } catch (InterruptedException e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                    Logger.logError(e.getMessage());
                }
            }
        }
        System.out.println("Server '" + name + "' stopped.");
        Logger.logInfo("Server '" + name + "' stopped.");
    }

    /**
     * Executes code before shutting down the server.
     */
    public void onStop() {

    }

    /**
     * Stops the server.
     */
    public final void stop() {
        onStop();
        removeAllTCPHandlers();
        removeAllUDPHandlers();
        setRunning(false);
    }

}
