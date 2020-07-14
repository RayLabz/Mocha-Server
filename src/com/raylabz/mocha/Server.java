package com.raylabz.mocha;

import java.net.InetAddress;
import java.util.Vector;

public class Server implements Runnable {

    private final String name;
    private boolean running = false;
    private final Vector<UDPPortListener> udpListeners;
    private final Vector<Thread> udpListenerThreads = new Vector<>();
    private final Vector<TCPHandler> tcpHandlers;
    private final Vector<Thread> tcpHandlerThreads = new Vector<>();

    private Server(String name, Vector<UDPPortListener> udpListeners, Vector<TCPHandler> tcpHandlers) {
        this.name = name;
        this.udpListeners = udpListeners;
        this.tcpHandlers = tcpHandlers;
    }

    public final boolean isRunning() {
        return running;
    }

    public final void setRunning(boolean running) {
        this.running = running;
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

    public void sendTCP(TCPConnection tcpConnection, final String message) {
         tcpConnection.send(message);
    }

    public void sendTCP(final String ipAddress, final int port, final String message) {
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
        } catch (Exception ignored) { }
    }

    public void sendUDP(final String ipAddress, final int port) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            if (port >= 0 && port <= 65535) {
                for (UDPPortListener udpPortListener : udpListeners) {
                    if (udpPortListener.getPort() == port && udpPortListener.getInetAddress().equals(inetAddress)) {
                        udpPortListener.
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    @Override
    public final void run() {
        System.out.println("Server '" + name + "' started.");
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

    public static class Builder {

        private final String name;
        private final Vector<UDPPortListener> udpListeners = new Vector<>();
        private final Vector<TCPHandler> tcpHandlers = new Vector<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addUDPListener(UDPPortListener portListener) {
            for (UDPPortListener p : udpListeners) {
                if (p.getPort() == portListener.getPort()) {
                    return this;
                }
            }
            udpListeners.add(portListener);
            return this;
        }

        public Builder addTCPHandler(TCPHandler tcpHandler) {
            for (TCPHandler h : tcpHandlers) {
                if (h.getPort() == tcpHandler.getPort()) {
                    return this;
                }
            }
            tcpHandlers.add(tcpHandler);
            return this;
        }

        public Server build() {
            return new Server(name, udpListeners, tcpHandlers);
        }

    }

}
