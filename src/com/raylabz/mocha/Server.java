package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.net.InetAddress;
import java.util.Vector;

/**
 * @author Nicos Kasenides
 * @version 1.0.0
 * Provides functionality for a server.
 */
public class Server implements Runnable {

    /**
     * The name of the server.
     */
    private final String name;

    /**
     * Whether the server is running or not.
     */
    private boolean running = false;

    /**
     * Execution delay between calls to the process() method - In <b>MILLISECONDS</b>.
     */
    private int executionDelay = 0;

    /**
     * A list of UDP listeners for this server.
     */
    private final Vector<UDPConnection> udpListeners = new Vector<>();

    /**
     * A list of threads running the UDP listeners of this server.
     */
    private final Vector<Thread> udpListenerThreads = new Vector<>();

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
     * Adds a UDP listener to the server.
     * @param udpConnection The UDP listener to add.
     * @return Returns true if the UDP listener was added, false otherwise.
     */
    public final boolean addUDPListener(UDPConnection udpConnection) {
        for (UDPConnection p : udpListeners) {
            if (p.getPort() == udpConnection.getPort()) {
                return false;
            }
        }
        udpListeners.add(udpConnection);
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
     * Checks if the server is running.
     * @return Returns true if the server is running, false otherwise.
     */
    public final boolean isRunning() {
        return running;
    }

    /**
     * Retrieves the execution delay in milliseconds.
     * @return Returns an integer.
     */
    public int getExecutionDelay() {
        return executionDelay;
    }

    /**
     * Sets the execution delay.
     * @param executionDelay The delay in milliseconds.
     */
    public void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    /**
     * Sets the server to be running (true) or not running (false).
     * This method further disables the server's UDP listeners and TCP handlers if the server is set to stop running.
     * @param running Set to true for running, false for not running.
     */
    public final void setRunning(boolean running) {
        this.running = running;
        if (!running) {
            for (UDPConnection udpConnection : udpListeners) {
                udpConnection.setEnabled(false);
            }
            for (TCPHandler handler : tcpHandlers) {
                handler.setEnabled(false);
            }
        }
    }

    /**
     * Retrieves the list of UDP listeners for this server.
     * @return Returns a Vector of UDPConnection.
     */
    public final Vector<UDPConnection> getUdpListeners() {
        return udpListeners;
    }

    /**
     * Retrieves the list of threads running the server's UDP listeners.
     * @return Returns a Vector of Thread.
     */
    public final Vector<Thread> getUdpListenerThreads() {
        return udpListenerThreads;
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
    public void initialize() {

    }

    /**
     * Defines functionality that is executed by the server during its runtime.
     * Important note: This method is executed CONTINUOUSLY during the server's runtime. Make sure that this method
     * only contains necessary code that should continuously be executed.
     */
    public void process() {

    }

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
     * @param data The data.
     */
    public final void sendUDP(final UDPConnection udpConnection, final String data) {
        if (validateRunningBeforeSend()) {
            udpConnection.send(data);
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
                    for (UDPConnection udpConnection : udpListeners) {
                        if (udpConnection.getPort() == port && udpConnection.getInetAddress().equals(inetAddress)) {
                            udpConnection.send(data);
                        }
                    }
                }
            } catch (Exception ignored) {
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
        setRunning(true);
        initialize();

        for (UDPConnection udpConnection : udpListeners) {
            Thread t = new Thread(udpConnection);
            udpListenerThreads.add(t);
            t.start();
        }
        for (TCPHandler tcpHandler : tcpHandlers) {
            Thread t = new Thread(tcpHandler);
            tcpHandlerThreads.add(t);
            t.start();
        }

        while (isRunning()) {
            process();
            try {
                Thread.sleep(executionDelay);
            } catch (InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                Logger.logError(e.getMessage());
            }
        }
    }

}
