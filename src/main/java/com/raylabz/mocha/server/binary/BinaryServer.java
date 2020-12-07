package com.raylabz.mocha.server.binary;

import com.raylabz.mocha.logger.Logger;
import com.raylabz.mocha.message.Message;
import com.raylabz.mocha.server.SecurityMode;
import com.raylabz.mocha.server.Server;
import com.raylabz.mocha.server.UDPPeer;
import com.raylabz.mocha.server.text.TCPTConnection;
import com.raylabz.mocha.server.text.TCPTHandler;
import com.raylabz.mocha.server.text.UDPTConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Provides functionality for a server.
 * @author Nicos Kasenides
 * @version 0.1.2?
 */
public abstract class BinaryServer extends Server {

    /**
     * A list of UDP listeners for this server.
     */
    private final Vector<UDPBConnection> udpHandlers = new Vector<>();

    /**
     * A list of TCP handlers for this server.
     */
    private final Vector<TCPBHandler> tcpHandlers = new Vector<>();

    /**
     * Constructs a TCP server.
     * @param name
     * @param securityMode
     */
    public BinaryServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
    }

    /**
     * Adds a UDP handler to the server.
     * @param udpConnection The UDP handler to add.
     * @return Returns true if the UDP handler was added, false otherwise.
     */
    public final boolean addUDPHandler(UDPBConnection udpConnection) {
        udpConnection.setServer(this);
        for (UDPBConnection p : udpHandlers) {
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
    public final boolean addTCPHandler(TCPBHandler tcpHandler) {
        tcpHandler.setServer(this);
        for (TCPBHandler h : tcpHandlers) {
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
    public final boolean removeTCPHandler(TCPBHandler tcpHandler) {
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
        TCPBHandler handlerToRemove = null;
        for (TCPBHandler tcpHandler : tcpHandlers) {
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
        for (TCPBHandler tcpHandler : tcpHandlers) {
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
    public final boolean removeUDPHandler(UDPBConnection udpConnection) {
        udpConnection.setEnabled(false);
        return udpHandlers.remove(udpConnection);
    }

    /**
     * Removes a UDP handler.
     * @param port The handler's port.
     * @return Returns true if the handler was successfully removed, false otherwise.
     */
    public final boolean removeUDPHandler(int port) {
        UDPBConnection handlerToRemove = null;
        for (UDPBConnection h : udpHandlers) {
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
        for (UDPBConnection udpConnection : udpHandlers) {
            udpConnection.setEnabled(false);
        }
        udpHandlers.clear();
    }

    /**
     * Retrieves the list of UDP listeners for this server.
     * @return Returns a Vector of UDPConnection.
     */
    public final Vector<UDPBConnection> getUdpHandlers() {
        return udpHandlers;
    }

    /**
     * Retrieves the list of TCP handlers for this server.
     * @return Returns a Vector of TCPHandler.
     */
    public final Vector<TCPBHandler> getTcpHandlers() {
        return tcpHandlers;
    }

    /**
     * Sends a message through TCP.
     * @param tcpConnection The TCPConnection to send the message through.
     * @param message The message.
     */
    public final void sendTCP(TCPBConnection tcpConnection, final Message message) {
        if (tcpConnection.isEnabled()) {
            tcpConnection.send(message);
        }
        else {
            System.err.println("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
            Logger.logError("Error - Cannot send message. TCPConnection [" + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort() + "] disabled");
        }
    }

    /**
     * Sends a message through TCP.
     * @param ipAddress The IP address to send the message to.
     * @param port The port to send the message through.
     * @param message The message.
     */
    public final void sendTCP(final String ipAddress, final int port, final Message message) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (port >= 0 && port <= 65535) {
                for (TCPBHandler h : tcpHandlers) {
                    if (h.getPort() == port) {
                        for (TCPBConnection tcpConnection : h.getTcpConnections()) {
                            if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                if (tcpConnection.isEnabled()) {
                                    tcpConnection.send(message);
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
     * Send a message through TCP.
     * @param inetAddress The internet address to send the message to.
     * @param port The port to send the message through.
     * @param message The message.
     */
    public final void sendTCP(final InetAddress inetAddress, final int port, final Message message) {
        if (port >= 0 && port <= 65535) {
            for (TCPBHandler h : tcpHandlers) {
                if (h.getPort() == port) {
                    for (TCPBConnection tcpConnection : h.getTcpConnections()) {
                        if (tcpConnection.getInetAddress().equals(inetAddress)) {
                            if (tcpConnection.isEnabled()) {
                                tcpConnection.send(message);
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
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastTCP(Message message, int port, InetAddress... ipAddresses) {
        for (TCPBHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(message, new ArrayList<>(Arrays.asList(ipAddresses)));
                break;
            }
        }
    }

    /**
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastTCP(Message message, int port, List<InetAddress> ipAddresses) {
        for (TCPBHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(message, new ArrayList<>(ipAddresses));
                break;
            }
        }
    }

    /**
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastTCP(Message message, int port, String... ipAddresses) {
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

        for (TCPBHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.multicast(message, inetAddresses);
                break;
            }
        }
    }

    /**
     * Broadcasts a given message to all of the peer of a particular TCP connection.
     * @param port The connection to broadcast the message to.
     * @param message The message to broadcast.
     */
    public final void broadcastTCP(final int port, final Message message) {
        for (TCPBHandler handler : tcpHandlers) {
            if (handler.getPort() == port) {
                handler.broadcast(message);
                break;
            }
        }
    }

    /**
     * Sends a message through UDP.
     * @param udpConnection The UDPConnection to send the message to.
     * @param outPort The port of the client.
     * @param message The message.
     */
    public final void sendUDP(final UDPBConnection udpConnection, int outPort, final Message message) {
        if (udpConnection.isEnabled()) {
            udpConnection.send(udpConnection.getInetAddress(), outPort, message);
        }
        else {
            System.err.println("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
            Logger.logError("Error - Cannot send message. UDPConnection [" + udpConnection.getInetAddress() + ":" + udpConnection.getPort() + "] disabled");
        }
    }

    /**
     * Sends a message through UDP.
     * @param ipAddress The IP address to send the message to.
     * @param outPort The outPort to send the message through.
     * @param message The message.
     */
    public final void sendUDP(final String ipAddress, final int outPort, final Message message) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (outPort >= 0 && outPort <= 65535) {
                for (UDPBConnection udpConnection : udpHandlers) {
                    if (udpConnection.getPort() == outPort && udpConnection.getInetAddress().equals(inetAddress)) {
                        if (udpConnection.isEnabled()) {
                            udpConnection.send(inetAddress, outPort, message);
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
     * Sends a message through UDP.
     * @param udpPeer The UDPPeer instance to send the message to.
     * @param message The message to send.
     */
    public final void sendUDP(UDPPeer udpPeer, final Message message) {
        sendUDP(udpPeer.getAddress().toString(), udpPeer.getPort(), message);
    }

    /**
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastUDP(Message message, int port, InetAddress... ipAddresses) {
        for (UDPBConnection udpConnection : udpHandlers) {
            if (udpConnection.getPort() == port) {
                udpConnection.multicast(message, new ArrayList<>(Arrays.asList(ipAddresses)));
                break;
            }
        }
    }

    /**
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastUDP(Message message, int port, List<InetAddress> ipAddresses) {
        for (UDPBConnection udpConnection : udpHandlers) {
            if (udpConnection.getPort() == port) {
                udpConnection.multicast(message, new ArrayList<>(ipAddresses));
                break;
            }
        }
    }

    /**
     * Multicasts a message to a set of IP addresses on a specific port.
     * @param message The message to send.
     * @param port The port to set the message through.
     * @param ipAddresses A list of IP addresses to send the message to.
     */
    public final void multicastUDP(Message message, int port, String... ipAddresses) {
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

        for (UDPBConnection connection : udpHandlers) {
            if (connection.getPort() == port) {
                connection.multicast(message, inetAddresses);
                break;
            }
        }
    }

    /**
     * Broadcasts a given message to all of the peers of a particular UDP connection.
     * @param port The connection to broadcast the message to.
     * @param message The message to broadcast.
     */
    public final void broadcastUDP(final int port, final Message message) {
        Vector<UDPBConnection> udpListeners = getUdpHandlers();
        for (UDPBConnection connection : udpListeners) {
            if (connection.getPort() == port) {
                connection.broadcast(message);
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

        for (UDPBConnection udpConnection : udpHandlers) {
            Thread t = new Thread(udpConnection, "UDP-Handler-Thread-Port-" + udpConnection.getPort());
            udpHandlerThreads.add(t);
            t.start();
        }
        for (TCPBHandler tcpHandler : tcpHandlers) {
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
