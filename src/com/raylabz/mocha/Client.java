package com.raylabz.mocha;

import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;

public abstract class Client implements Runnable {

    private final InetAddress address;
    private final int port;
    private boolean listening = true;

    public Client(String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        this.address = InetAddress.getByName(ipAddress);
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + "). The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
    }

    public final boolean isListening() {
        return listening;
    }

    public final void setListening(boolean listening) {
        this.listening = listening;
    }

    public final InetAddress getAddress() {
        return address;
    }

    public final int getPort() {
        return port;
    }

    public abstract void send(final String data);

}
