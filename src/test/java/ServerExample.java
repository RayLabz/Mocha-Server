import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class ServerExample {

    public static void main(String[] args) {

        class TestServer extends Server {

            /**
             * Constructs a new server.
             *
             * @param name The name of the server.
             */
            public TestServer(String name) {
                super(name);
            }

            @Override
            public void initialize() {

            }

            @Override
            public void process() {

            }

        }

        TestServer server = new TestServer("My server");

//        server.addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
//            @Override
//            public void onReceive(TCPConnection tcpConnection, String data) {
//                System.out.println("Received from TCP[" + tcpConnection.getSocket().getPort() + "]: "  + data);
//                tcpConnection.send("Server TCP response");
//            }
//        }));
//
        server.addUDPHandler(new UDPConnection(7080) {
            @Override
            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
                System.out.println("Received from UDP [" + address.toString() + ":" + outPort + "]: " + data);
                String counterData = data.substring(data.length() - 1);
                udpConnection.send(address, outPort, "UDP response " + counterData);
                System.out.println("Sent to " + address.toString() + ":" + outPort);
            }
        });

        Mocha.start(server);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
    }

}
