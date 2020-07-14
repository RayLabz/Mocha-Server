package com.raylabz.mocha;

import java.util.Vector;

public class Server extends Thread {

    public static String SERVER_NAME;

    private boolean running = false;
    private final Vector<UDPPortListener> udpListeners;
    private final Vector<Thread> udpListenerThreads = new Vector<>();
    private final Vector<TCPHandler> tcpHandlers;
    private final Vector<Thread> tcpHandlerThreads = new Vector<>();

    private Server(String name, Vector<UDPPortListener> udpListeners, Vector<TCPHandler> tcpHandlers) {
        setName(name);
        SERVER_NAME = name;
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

    public void initialize() {

    }

    @Override
    public void run() {
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
