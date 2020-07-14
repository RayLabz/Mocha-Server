package test;

import com.raylabz.mocha.*;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class TestServer {

    public static void main(String[] args) {

        Server server = new Server("My server");
        server.addTCPHandler(new TCPHandler(1234, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                System.out.println("Received from TCP[1234]: "  + data);
                tcpConnection.send("Server response!");
            }
        }));

        Thread thread = new Thread(server);
        thread.start();
    }

}
