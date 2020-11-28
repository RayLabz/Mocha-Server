package com.raylabz.mocha.server;

import com.raylabz.mocha.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides functionality for a server.
 * @author Nicos Kasenides
 * @version 0.1.2?
 */
public abstract class Server implements Runnable {

    public static final String WHITELIST_FILENAME_POSTFIX =  "-whitelist";
    public static final String BLACKLIST_FILENAME_POSTFIX =  "-blacklist";

    /**
     * The server's security mode.
     */
    private final SecurityMode securityMode;

    /**
     * The server's whitelist - a list of IP addresses for which to <b>accept</b> communication when running in SecurityMode.WHITELIST.
     */
    private final HashSet<InetAddress> whitelist = new HashSet<>();

    /**
     * The server's blacklist - a list of IP addresses for which to <b>reject</b> communication when running in SecurityMode.BLACKLIST.
     */
    private final HashSet<InetAddress> blacklist = new HashSet<>();

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
     * @param securityMode The security mode of the server.
     */
    public Server(final String name, final SecurityMode securityMode) {
        this.name = name;

        switch (securityMode) {
            case WHITELIST:

                //Create if not there:
                final File whiteListFile = new File(name + WHITELIST_FILENAME_POSTFIX);
                try {
                    whiteListFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Whitelist error: could not create whitelist file. Will default to SecurityMode.NONE.");
                    Logger.logError("Whitelist error: could not create whitelist file. Will default to SecurityMode.NONE.");
                    this.securityMode = SecurityMode.NONE;
                    break;
                }

                this.securityMode = securityMode;

                //Load:
                try {
                    loadWhitelist();
                } catch (FileNotFoundException e) {
                    System.err.println("Whitelist loading error: file '" + name + WHITELIST_FILENAME_POSTFIX + "' not found.");
                    Logger.logError("Whitelist loading error: file '" + name + WHITELIST_FILENAME_POSTFIX + "' not found.");
                }

                break;

            case BLACKLIST:

                //Create if not there:
                final File blackListFile = new File(name + BLACKLIST_FILENAME_POSTFIX);
                try {
                    blackListFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Whitelist error: could not create blacklist file. Will default to SecurityMode.NONE.");
                    Logger.logError("Whitelist error: could not create blacklist file. Will default to SecurityMode.NONE.");
                    this.securityMode = SecurityMode.NONE;
                    break;
                }

                this.securityMode = securityMode;

                //Load:
                try {
                    loadBlacklist();
                } catch (FileNotFoundException e) {
                    System.err.println("Blacklist loading error: file '" + name + BLACKLIST_FILENAME_POSTFIX + "' not found.");
                    Logger.logError("Blacklist loading error: file '" + name + BLACKLIST_FILENAME_POSTFIX + "' not found.");
                }

                break;

            case NONE:
            default:
                this.securityMode = SecurityMode.NONE;
                System.out.println("Warning: '" + name + "' started without a security mode - consider using SecurityMode.BLACKLIST or SecurityMode.WHITELIST.");
                Logger.logWarning("Warning: '" + name + "' started without a security mode - consider using SecurityMode.BLACKLIST or SecurityMode.WHITELIST.");
        }
    }

    /**
     * Adds a UDP handler to the server.
     * @param udpConnection The UDP handler to add.
     * @return Returns true if the UDP handler was added, false otherwise.
     */
    public final boolean addUDPHandler(UDPConnection udpConnection) {
        udpConnection.setServer(this);
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
        tcpHandler.setServer(this);
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
    public final boolean removeTCPHandler(TCPHandler tcpHandler) {
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
     * Retrieves the server's blacklist.
     * @return Returns a HashSet of InetAddress.
     */
    public HashSet<InetAddress> getBlacklist() {
        return blacklist;
    }

    /**
     * Retrieves the server's whitelist.
     * @return Returns a HashSet of InetAddress.
     */
    public HashSet<InetAddress> getWhitelist() {
        return whitelist;
    }

    /**
     * Retrieves the server's SecurityMode.
     * @return Returns a SecurityMode.
     */
    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    /**
     * Initializes the server.
     * Does nothing - must be implemented by extending classes if needed.
     */
    protected void initialize() { }

    /**
     * Executes a piece of code indefinitely in the background, every <i>executionDelay</i> milliseconds
     * Does nothing - must be implemented by extending classes if needed.
     * <b>Important note</b>: This method is executed CONTINUOUSLY during the server's runtime.
     * Make sure to properly optimize this method to avoid performance degradation.
     */
    protected void runIndefinitely() { }

    /**
     * Executes a piece of code in the background.
     * This method is not meant for use with code that must run in a loop indefinitely.
     * For that, consider implementing <i>runIndefinitely()</i> instead.
     * @param runnable A runnable to run.
     * @param tag The runnable's tag, used for identification.
     * @return Returns the thread running the runnable.
     */
    public Thread runInBackground(final Runnable runnable, final String tag) {
        Thread thread = new Thread(runnable, tag);
        thread.start();
        return thread;
    }

    /**
     * Sends data through TCP.
     * @param tcpConnection The TCPConnection to send the data through.
     * @param data The data.
     */
    public final void sendTCP(TCPConnection tcpConnection, final String data) {
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
                for (TCPHandler h : tcpHandlers) {
                    if (h.getPort() == port) {
                        for (TCPConnection tcpConnection : h.getTcpConnections()) {
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
            for (TCPHandler h : tcpHandlers) {
                if (h.getPort() == port) {
                    for (TCPConnection tcpConnection : h.getTcpConnections()) {
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
        for (TCPHandler handler : tcpHandlers) {
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
        for (TCPHandler handler : tcpHandlers) {
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

        for (TCPHandler handler : tcpHandlers) {
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
        for (TCPHandler handler : tcpHandlers) {
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
    public final void sendUDP(final UDPConnection udpConnection, int outPort, final String data) {
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
                for (UDPConnection udpConnection : udpHandlers) {
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
        for (UDPConnection udpConnection : udpHandlers) {
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
        for (UDPConnection udpConnection : udpHandlers) {
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

        for (UDPConnection connection : udpHandlers) {
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
        Vector<UDPConnection> udpListeners = getUdpHandlers();
        for (UDPConnection connection : udpListeners) {
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
     * Loads the blacklist from the external file.
     * @throws FileNotFoundException thrown when a malformed IP address is found.
     */
    private void loadBlacklist() throws FileNotFoundException {
        final Scanner fileScanner = new Scanner(new File(name + BLACKLIST_FILENAME_POSTFIX));
        while (fileScanner.hasNextLine()) {
            final String ipAddressStr = fileScanner.nextLine();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddressStr);
                blacklist.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Blacklist loading error: invalid IP '" + ipAddressStr + "'.");
                Logger.logError("Blacklist loading error: invalid IP '" + ipAddressStr + "'.");
            }
        }
        fileScanner.close();
    }

    /**
     * Loads the whitelist from the external file.
     * @throws FileNotFoundException thrown when a malformed IP address is found.
     */
    private void loadWhitelist() throws FileNotFoundException {
        final Scanner fileScanner = new Scanner(new File(name + WHITELIST_FILENAME_POSTFIX));
        while (fileScanner.hasNextLine()) {
            final String ipAddressStr = fileScanner.nextLine();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddressStr);
                whitelist.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Whitelist loading error: invalid IP '" + ipAddressStr + "'.");
                Logger.logError("Whitelist loading error: invalid IP '" + ipAddressStr + "'.");
            }
        }
        fileScanner.close();
    }

    /**
     * Saves the blacklist into a file.
     */
    private void saveBlacklistToFile() {
        try {
            FileWriter fileWriter = new FileWriter(new File(name + BLACKLIST_FILENAME_POSTFIX));
            final StringBuilder stringBuilder = new StringBuilder();
            for (final InetAddress ip : blacklist) {
                stringBuilder.append(ip.toString()).append(System.lineSeparator());
            }
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Blacklist save error: could not write list to file '" + name + BLACKLIST_FILENAME_POSTFIX);
            Logger.logError("Blacklist save error: could not write list to file '" + name + BLACKLIST_FILENAME_POSTFIX);
        }
    }

    /**
     * Saves the whitelist into a file.
     */
    private void saveWhitelistToFile() {
        try {
            FileWriter fileWriter = new FileWriter(new File(name + WHITELIST_FILENAME_POSTFIX));
            final StringBuilder stringBuilder = new StringBuilder();
            for (final InetAddress ip : whitelist) {
                stringBuilder.append(ip.toString()).append(System.lineSeparator());
            }
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Whitelist save error: could not write list to file '" + name + WHITELIST_FILENAME_POSTFIX);
            Logger.logError("Whitelist save error: could not write list to file '" + name + WHITELIST_FILENAME_POSTFIX);
        }
    }

    /**
     * Bans an IP address.
     * @param inetAddress The IP address to ban.
     */
    public final void banIP(InetAddress inetAddress) {
        if (securityMode == SecurityMode.BLACKLIST) {
            blacklist.add(inetAddress);
            saveBlacklistToFile();
            System.out.println("'" + name + "' Banned IP: " + inetAddress.toString());
            Logger.logInfo("'" + name + "' Banned IP: " + inetAddress.toString());
        }
        else {
            System.err.println("Cannot ban IP '" + inetAddress.toString() + "' while not on blacklist mode.");
            Logger.logWarning("Cannot ban IP while not on blacklist mode.");
        }
    }

    /**
     * Bans an IP address.
     * @param ipAddress The IP address to ban.
     */
    public final void banIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            banIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Unban error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Un-bans an IP address.
     * @param inetAddress The IP address to unban.
     */
    public final void unbanIP(InetAddress inetAddress) {
        if (securityMode == SecurityMode.BLACKLIST) {
            blacklist.remove(inetAddress);
            saveBlacklistToFile();
            System.out.println("'" + name + "' Un-banned IP: " + inetAddress.toString());
            Logger.logInfo("'" + name + "' Un-banned IP: " + inetAddress.toString());
        }
        else {
            System.err.println("Cannot unban IP '" + inetAddress.toString() + "' while not on blacklist mode.");
            Logger.logWarning("Cannot unban IP while not on blacklist mode.");
        }
    }

    /**
     * Un-bans an IP address.
     * @param ipAddress The IP address to unban.
     */
    public final void unbanIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            unbanIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Unban error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Whitelists an IP address.
     * @param address The address to whitelist.
     */
    public final void whitelistIP(InetAddress address) {
        if (securityMode == SecurityMode.WHITELIST) {
            whitelist.add(address);
            saveWhitelistToFile();
            System.out.println("'" + name + "' Whitelisted IP: " + address.toString());
            Logger.logInfo("'" + name + "' Whitelisted IP: " + address.toString());
        }
        else {
            System.err.println("Cannot whitelist IP '" + address.toString() + "' while not on whitelist mode.");
            Logger.logWarning("Cannot whitelist IP '" + address.toString() + "' while not on whitelist mode.");
        }
    }

    /**
     * Whitelists an IP address.
     * @param ipAddress The IP address to whitelist.
     */
    public final void whitelistIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            whitelistIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Whitelist error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    public final void unWhitelistIP(InetAddress address) {
        if (securityMode == SecurityMode.WHITELIST) {
            whitelist.remove(address);
            saveWhitelistToFile();
            System.out.println("'" + name + "' IP: " + address.toString() + " removed from whitelist.");
            Logger.logInfo("'" + name + "' IP: " + address.toString() + " removed from whitelist.");
        }
        else {
            System.err.println("Cannot un-whitelist IP '" + address.toString() + "' while not on whitelist mode.");
            Logger.logWarning("Cannot un-whitelist IP '" + address.toString() + "' while not on whitelist mode.");
        }
    }

    public final void unWhitelistIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            unWhitelistIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Un-whitelist error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Executes code before shutting down the server.
     * Does nothing by default - must be implemented (if needed) by the extending class.
     */
    public void onStop() { }

    /**
     * Stops the server.
     */
    public final void stop() {
        onStop();
        removeAllTCPHandlers();
        removeAllUDPHandlers();
        setRunning(false);
    }

    /**
     * Starts the server.
     * @return Returns the thread running this client.
     */
    public final Thread start() {
        Thread thread = new Thread(this, name);
        thread.start();
        return thread;
    }

}
