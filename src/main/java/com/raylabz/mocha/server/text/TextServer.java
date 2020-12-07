package com.raylabz.mocha.server.text;

import com.raylabz.mocha.logger.Logger;
import com.raylabz.mocha.server.SecurityMode;
import com.raylabz.mocha.server.Server;
import com.raylabz.mocha.server.UDPPeer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Provides functionality for a server.
 * @author Nicos Kasenides
 * @version 0.1.2?
 */
public abstract class TextServer extends Server {

    /**
     * A list of UDP listeners for this server.
     */
    private final Vector<UDPTConnection> udpHandlers = new Vector<>();

    /**
     * A list of TCP handlers for this server.
     */
    private final Vector<TCPTHandler> tcpHandlers = new Vector<>();

    /**
     * Constructs a TCP server.
     * @param name
     * @param securityMode
     */
    public TextServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
    }

    /**
     * Adds a UDP handler to the server.
     * @param udpConnection The UDP handler to add.
     * @return Returns true if the UDP handler was added, false otherwise.
     */
    public final boolean addUDPHandler(UDPTConnection udpConnection) {
        udpConnection.setServer(this);
        for (UDPTConnection p : udpHandlers) {
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
    public final boolean addTCPHandler(TCPTHandler tcpHandler) {
        tcpHandler.setServer(this);
        for (TCPTHandler h : tcpHandlers) {
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
    public final boolean removeTCPHandler(TCPTHandler tcpHandler) {
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
        TCPTHandler handlerToRemove = null;
        for (TCPTHandler tcpHandler : tcpHandlers) {
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
        for (TCPTHandler tcpHandler : tcpHandlers) {
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
    public final boolean removeUDPHandler(UDPTConnection udpConnection) {
        udpConnection.setEnabled(false);
        return udpHandlers.remove(udpConnection);
    }

    /**
     * Removes a UDP handler.
     * @param port The handler's port.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeUDPHandler(int port) {
        UDPTConnection handlerToRemove = null;
        for (UDPTConnection h : udpHandlers) {
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
        for (UDPTConnection udpConnection : udpHandlers) {
            udpConnection.setEnabled(false);
        }
        udpHandlers.clear();
    }

    /**
     * Retrieves the list of UDP listeners for this server.
     * @return Returns a Vector of UDPConnection.
     */
    public final Vector<UDPTConnection> getUdpHandlers() {
        return udpHandlers;
    }

    /**
     * Retrieves the list of TCP handlers for this server.
     * @return Returns a Vector of TCPHandler.
     */
    public final Vector<TCPTHandler> getTcpHandlers() {
        return tcpHandlers;
    }

    /**
     * Sends data through TCP.
     * @param tcpConnection The TCPConnection to send the data through.
     * @param data The data.
     */
    public final void sendTCP(TCPTConnection tcpConnection, final String data) {
        if (tcpConnection.isEnabled()) {
            tcpConnection.send(data);
        }
        else {
            System.err.println("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
            Logger.logError("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
        }
    }

    /**
     * Sends data through TCP.
     * @param ipAddress The IP address to send the data to.
     * @param port The port to send the data through.
     * @param data The data.
     */
    public final void sendTCP(final String ipAddress, final int port, final String data) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (port >= 0 && port <= 65535) {
                for (TCPTHandler h : tcpHandlers) {
                    if (h.getPort() == port) {
                        for (TCPTConnection tcpConnection : h.getTcpConnections()) {
                            if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                if (tcpConnection.isEnabled()) {
                                    tcpConnection.send(data);
                                }
                                else {
                                    System.err.println("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
                                    Logger.logError("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Send data through TCP.
     * @param inetAddress The internet address to send the data to.
     * @param port The port to send the data through.
     * @param data The data.
     */
    public final void sendTCP(final InetAddress inetAddress, final int port, final String data) {
        if (port >= 0 && port <= 65535) {
            for (TCPTHandler h : tcpHandlers) {
                if (h.getPort() == port) {
                    for (TCPTConnection tcpConnection : h.getTcpConnections()) {
                        if (tcpConnection.getInetAddress().equals(inetAddress)) {
                            if (tcpConnection.isEnabled()) {
                                tcpConnection.send(data);
                            }
                            else {
                                System.err.println("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
                                Logger.logError("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastTCP(String data, int port, InetAddress... ipAddresses) {
        for (TCPTHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(data, new ArrayList<>(Arrays.asList(ipAddresses)));
                break;
            }
        }
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastTCP(String data, int port, List<InetAddress> ipAddresses) {
        for (TCPTHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(data, new ArrayList<>(ipAddresses));
                break;
            }
        }
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastTCP(String data, int port, String... ipAddresses) {
        ArrayList<InetAddress> inetAddresses = new ArrayList<>();
        for (String ipString : ipAddresses) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipString);
                inetAddresses.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Invalid multicast target [TCP]: " + ipString);
                Logger.logError("Invalid multicast target [TCP]: " + ipString);
            }
        }

        for (TCPTHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(data, inetAddresses);
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
        for (TCPTHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.broadcast(data);
                break;
            }
        }
    }

    /**
     * Sends data through UDP.
     * @param udpConnection The UDPConnection to send the data to.
     * @param outPort The port of the client.
     * @param data The data.
     */
    public final void sendUDP(final UDPTConnection udpConnection, int outPort, final String data) {
        if (udpConnection.isEnabled()) {
            udpConnection.send(udpConnection.getInetAddress(), outPort, data);
        }
        else {
            System.err.println("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
            Logger.logError("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
        }
    }

    /**
     * Sends data through UDP.
     * @param ipAddress The IP address to send the data to.
     * @param outPort The outPort to send the data through.
     * @param data The data.
     */
    public final void sendUDP(final String ipAddress, final int outPort, final String data) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (outPort >= 0 && outPort <= 65535) {
                for (UDPTConnection udpConnection : udpHandlers) {
                    if (udpConnection.getPort() == outPort && udpConnection.getInetAddress().equals(inetAddress)) {
                        if (udpConnection.isEnabled()) {
                            udpConnection.send(inetAddress, outPort, data);
                        }
                        else {
                            System.err.println("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
                            Logger.logError("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Sends data through UDP.
     * @param udpPeer The UDPPeer instance to send the data to.
     * @param data The data to send.
     */
    public final void sendUDP(UDPPeer udpPeer, final String data) {
        sendUDP(udpPeer.getAddress().toString(), udpPeer.getPort(), data);
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastUDP(String data, int port, InetAddress... ipAddresses) {
        for (UDPTConnection udpConnection : udpHandlers) {
            if (udpConnection.getPort() == port) {
                udpConnection.multicast(data, new ArrayList<>(Arrays.asList(ipAddresses)));
                break;
            }
        }
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastUDP(String data, int port, List<InetAddress> ipAddresses) {
        for (UDPTConnection udpConnection : udpHandlers) {
            if (udpConnection.getPort() == port) {
                udpConnection.multicast(data, new ArrayList<>(ipAddresses));
                break;
            }
        }
    }

    /**
     * Multicasts data to a set of IP addresses on a specific port.
     * @param data The data to send.
     * @param port The port to set the data through.
     * @param ipAddresses A list of IP addresses to send the data to.
     */
    public final void multicastUDP(String data, int port, String... ipAddresses) {
        ArrayList<InetAddress> inetAddresses = new ArrayList<>();
        for (String ipString : ipAddresses) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipString);
                inetAddresses.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Invalid multicast target [UDP]: " + ipString);
                Logger.logError("Invalid multicast target [UDP]: " + ipString);
            }
        }

        for (UDPTConnection connection : udpHandlers) {
            if (connection.getPort() == port) {
                connection.multicast(data, inetAddresses);
                break;
            }
        }
    }

    /**
     * Broadcasts a given message to all of the peers of a particular UDP connection.
     * @param port The connection to broadcast the data to.
     * @param data The data to broadcast.
     */
    public final void broadcastUDP(final int port, final String data) {
        Vector<UDPTConnection> udpListeners = getUdpHandlers();
        for (UDPTConnection connection : udpListeners) {
            if (connection.getPort() == port) {
                connection.broadcast(data);
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

        for (UDPTConnection udpConnection : udpHandlers) {
            Thread t = new Thread(udpConnection, "UDP-Handler-Thread-Port-" + udpConnection.getPort());
            udpHandlerThreads.add(t);
            t.start();
        }
        for (TCPTHandler tcpHandler : tcpHandlers) {
            Thread t = new Thread(tcpHandler, "TCP-Handler-Thread-Port-" + tcpHandler.getPort());
            tcpHandlerThreads.add(t);
            t.start();
        }

        while (isRunning()) {
            runIndefinitely();
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
     * Stops the server.
     */
    public final void stop() {
        onStop();
        removeAllTCPHandlers();
        removeAllUDPHandlers();
        setRunning(false);
    }

}
