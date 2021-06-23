//import com.raylabz.mocha.server.*;
//
//import java.net.InetAddress;
//
//public class ServerExample {
//
//
//    public static void main(String[] args) {
//        BasicServer server = new BasicServer("My Basic Server");
//        Mocha.start(server);
//
//        server.addTCPHandler(new TCPHandler(1234, new TCPReceivable() {
//            @Override
//            public void onReceive(TCPConnection tcpConnection, String data) {
//                //Echo the message back to the client:
//                tcpConnection.send(data);
//            }
//        }));
//
//        server.addUDPHandler(new UDPConnection(1234) {
//            @Override
//            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
//                //Echo the message back to the client:
//                udpConnection.send(address, outPort, data);
//            }
//        });
//
//    }
//
//}
