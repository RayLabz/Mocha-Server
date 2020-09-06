import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class ServerExample {

    public static void main(String[] args) {

        Server server = new Server("My server");
//        server.addTCPHandler(new TCPHandler(1234, new TCPReceivable() {
//            @Override
//            public void onReceive(TCPConnection tcpConnection, String data) {
//                System.out.println("Received from TCP[" + tcpConnection.getSocket().getPort() + "]: "  + data);
//                tcpConnection.send("Server TCP response");
//            }
//        }));

        server.addUDPListener(new UDPConnection(9999) {
            @Override
            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
                System.out.println("Received from UDP [" + address.toString() + ":" + outPort + "]: " + data);
                udpConnection.send(address, outPort, "Server UDP response");
                System.out.println("Sent to " + address.toString() + ":" + outPort);
            }
        });

        Thread thread = new Thread(server);
        thread.start();
    }

}
