package test;

import com.raylabz.mocha.Server;
import com.raylabz.mocha.TCPHandler;
import com.raylabz.mocha.TCPReceivable;
import com.raylabz.mocha.UDPPortListener;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class TestServer {

    public static void main(String[] args) {
        Server server = new Server.Builder("My Server")
                .addUDPListener(new UDPPortListener(1234) {
                    @Override
                    public void onReceive(DatagramPacket packet) {
                        String data = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        System.out.println("Received UDP-1234 -> " + data);
                    }
                })
                .addUDPListener(new UDPPortListener(4321) {
                    @Override
                    public void onReceive(DatagramPacket packet) {
                        String data = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        System.out.println("Received UDP-4321 -> " + data);
                    }
                })
                .addTCPHandler(new TCPHandler(1234, new TCPReceivable() {
                    @Override
                    public void onReceive(InetAddress inetAddress, String data) {
                        System.out.println("Received TCP-1234 -> " + data);
                    }
                }))
                .addTCPHandler(new TCPHandler(4321, new TCPReceivable() {
                    @Override
                    public void onReceive(InetAddress inetAddress, String data) {
                        System.out.println("Received TCP-4321 -> " + data);
                    }
                }))
                .build();

        server.start();
    }

}
