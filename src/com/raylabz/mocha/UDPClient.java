package com.raylabz.mocha;

import java.io.IOException;
import java.net.*;

public abstract class UDPClient extends Client {

    private final DatagramSocket socket;
    private byte[] buffer = new byte[65535];

    public UDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
        super(ipAddress, port);
        this.socket = new DatagramSocket();
        Thread listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    final String data = new String(packet.getData(), 0, packet.getLength());
                    onReceive(data);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        listeningThread.start();
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

    public abstract void onReceive(String data);

}
