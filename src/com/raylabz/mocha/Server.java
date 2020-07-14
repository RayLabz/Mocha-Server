package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.net.InetAddress;
import java.util.Vector;

public class Server implements Runnable {

    private final String name;
    private boolean running = false;
    private final Vector<UDPPortListener> udpListeners = new Vector<>();
    private final Vector<Thread> udpListenerThreads = new Vector<>();
    private final Vector<TCPHandler> tcpHandlers = new Vector<>();
    private final Vector<Thread> tcpHandlerThreads = new Vector<>();

    public Server(String name) {
        this.name = name;
    }

    public boolean addUDPListener(UDPPortListener portListener) {
        for (UDPPortListener p : udpListeners) {
            if (p.getPort() == portListener.getPort()) {
                return false;
            }
        }
        udpListeners.add(portListener);
        return true;
    }

    public boolean addTCPHandler(TCPHandler tcpHandler) {
        for (TCPHandler h : tcpHandlers) {
            if (h.getPort() == tcpHandler.getPort()) {
                return false;
            }
        }
        tcpHandlers.add(tcpHandler);
        return true;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void setRunning(boolean running) {
        this.running = running;
        if (!running) {
            for (UDPPortListener udpPortListener : udpListeners) {
                udpPortListener.setEnabled(false);
            }
            for (TCPHandler handler : tcpHandlers) {
                handler.setEnabled(false);
            }
        }
    }

    public final Vector<UDPPortListener> getUdpListeners() {
        return udpListeners;
    }

    public final Vector<Thread> getUdpListenerThreads() {
        return udpListenerThreads;
    }

    public Vector<TCPHandler> getTcpHandlers() {
        return tcpHandlers;
    }

    public Vector<Thread> getTcpHandlerThreads() {
        return tcpHandlerThreads;
    }

    public String getName() {
        return name;
    }

    public void initialize() {

    }

    public void process() {

    }

    private final boolean validateRunningBeforeSend() {
        if (!isRunning()) {
            final String text = "Warning: Cannot send while the server is not running.";
            System.err.println(text);
            Logger.logWarning(text);
            return false;
        }
        return true;
    }

    public final void sendTCP(TCPConnection tcpConnection, final String message) {
        if (validateRunningBeforeSend()) {
            tcpConnection.send(message);
        }
    }

    public final void sendTCP(final String ipAddress, final int port, final String message) {
        if (validateRunningBeforeSend()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (port >= 0 && port <= 65535) {
                    for (TCPHandler h : tcpHandlers) {
                        if (h.getPort() == port) {
                            for (TCPConnection tcpConnection : h.getTcpConnections()) {
                                if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                    tcpConnection.send(message);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public final void sendTCP(final InetAddress inetAddress, final int port, final String message) {
        if (validateRunningBeforeSend()) {
            if (port >= 0 && port <= 65535) {
                for (TCPHandler h : tcpHandlers) {
                    if (h.getPort() == port) {
                        for (TCPConnection tcpConnection : h.getTcpConnections()) {
                            if (tcpConnection.getInetAddress().equals(inetAddress)) {
                                tcpConnection.send(message);
                            }
                        }
                    }
                }
            }
        }
    }

    public final void sendUDP(final UDPPortListener udpPortListener, final String message) {
        if (validateRunningBeforeSend()) {
            udpPortListener.send(message);
        }
    }

    public final void sendUDP(final String ipAddress, final int port, final String message) {
        if (validateRunningBeforeSend()) {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (port >= 0 && port <= 65535) {
                    for (UDPPortListener udpPortListener : udpListeners) {
                        if (udpPortListener.getPort() == port && udpPortListener.getInetAddress().equals(inetAddress)) {
                            udpPortListener.send(message);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public final void run() {
        System.out.println("Server '" + name + "' started.");
        setRunning(true);
        initialize();
        for (UDPPortListener udpPortListener : udpListeners) {
            Thread t = new Thread(udpPortListener);
            udpListenerThreads.add(t);
            t.start();
        }
        for (TCPHandler tcpHandler : tcpHandlers) {
            Thread t = new Thread(tcpHandler);
            tcpHandlerThreads.add(t);
            t.start();
        }
        process();
    }

}
