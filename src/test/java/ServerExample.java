import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class ServerExample {

    public static void main(String[] args) throws InterruptedException {

        Server server = new Server("My server");

        TCPHandler tcpHandler = new TCPHandler(1234, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                System.out.println("Received message: " + data + " from " + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort());
            }
        });

        server.addTCPHandler(tcpHandler);

//        server.addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
//            @Override
//            public void onReceive(TCPConnection tcpConnection, String data) {
//                System.out.println("Received from TCP[" + tcpConnection.getSocket().getPort() + "]: "  + data);
//                tcpConnection.send("Server TCP response");
//            }
//        }));
//
//        server.addUDPHandler(new UDPConnection(7080) {
//            @Override
//            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
//                System.out.println("Received from UDP [" + address.toString() + ":" + outPort + "]: " + data);
//                String counterData = data.substring(data.length() - 1);
//                udpConnection.send(address, outPort, "UDP response " + counterData);
//                System.out.println("Sent to " + address.toString() + ":" + outPort);
//            }
//        });

        Thread thread = new Thread(server);
        thread.start();

        Thread.sleep(5000);

        server.stop();
    }

}
