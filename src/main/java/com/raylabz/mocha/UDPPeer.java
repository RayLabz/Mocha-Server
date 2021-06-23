package com.raylabz.mocha;

import java.net.InetAddress;
import java.util.Objects;

/**
 * Models a UDP peer.
 */
public class UDPPeer {

    /**
     * The peer's IP address.
     */
    private final InetAddress address;

    /**
     * The peer's port.
     */
    private final int port;

    /**
     * Constructs a new UDPPeer.
     * @param address The IP address of the peer.
     * @param port The port of the peer.
     */
    public UDPPeer(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Retrieves the IP address of the peer.
     * @return Returns an InetAddress.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Retrieves the port of the peer.
     * @return Returns an integer (port).
     */
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

    @Override
    public String toString() {
        return "UDPPeer{" +
                "address=" + address +
                ", port=" + port +
                '}';
    }

}
