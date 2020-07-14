package com.raylabz.mocha;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;

public abstract class Client implements Runnable {

    private final String name;
    private final InetAddress address;
    private final int port;

    public Client(String name, String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        this.name = name;
        this.address = InetAddress.getByName(ipAddress);
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + ") for client '" + name + "'. The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
    }

    public final String getName() {
        return name;
    }

    public final InetAddress getAddress() {
        return address;
    }

    public final int getPort() {
        return port;
    }

    public abstract void send(final String data);

    public final void start() {
        Thread thread = new Thread(this, getName());
        thread.start();
    }

}
