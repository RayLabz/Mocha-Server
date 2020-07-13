package com.raylabz.mocha;

import java.util.Vector;

public class Server extends Thread {

    public static String SERVER_NAME;

    private boolean running = false;
    private final Vector<UDPPortListener> udpListeners;

    private Server(String name, Vector<UDPPortListener> udpListeners) {
        setName(name);
        SERVER_NAME = name;
        this.udpListeners = udpListeners;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void initialize() {

    }

    @Override
    public void run() {
        initialize();
        for (UDPPortListener udpPortListener : udpListeners) {
            Thread t = new Thread(udpPortListener);
            t.start();
        }
    }

    public static class Builder {

        private final String name;
        private final Vector<UDPPortListener> udpListeners = new Vector<>();

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

        public Server build() {
            return new Server(name, udpListeners);
        }

    }

}
