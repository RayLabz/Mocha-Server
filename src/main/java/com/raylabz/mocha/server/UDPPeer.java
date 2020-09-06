package com.raylabz.mocha.server;

import java.net.InetAddress;
import java.util.Objects;

public class UDPPeer {

    private final InetAddress address;
    private final int port;

    public UDPPeer(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UDPPeer udpPeer = (UDPPeer) o;
        return port == udpPeer.port &&
                Objects.equals(address, udpPeer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

}
