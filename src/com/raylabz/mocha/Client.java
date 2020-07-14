package com.raylabz.mocha;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.UnknownHostException;

public abstract class Client extends Thread {

    private final InetAddress address;
    private final int port;

    public Client(String name, String ipAddress, int port) throws UnknownHostException, PortUnreachableException {
        setName(name);
        this.address = InetAddress.getByName(ipAddress);
        if (port > 65535 || port < 0) {
            throw new PortUnreachableException("Invalid port number (" + port + ") for client '" + name + "'. The port must be in the range 0-65535.");
        }
        else {
            this.port = port;
        }
    }

    public final InetAddress getAddress() {
        return address;
    }

    public final int getPort() {
        return port;
    }

    public abstract void send(final String data);

    public abstract void run();

}
