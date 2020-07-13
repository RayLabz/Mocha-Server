package com.raylabz.mocha;

import java.io.IOException;
import java.net.*;

public abstract class UDPClient extends Client {

    private final DatagramSocket socket;
    byte[] buffer = new byte[65535];

    public UDPClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
        this.socket = new DatagramSocket();
    }

    public final void send(final String data) {
        try {
            final byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, getAddress(), getPort());
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final String receive() {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
