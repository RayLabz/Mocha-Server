//import com.raylabz.mocha.client.TCPClient;
//import com.raylabz.mocha.client.UDPClient;
//import Mocha;
//import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
//
//import java.io.IOException;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//public class ClientsExample {
//
//    public static class MyUDPClient extends UDPClient {
//
//        public MyUDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
//            super("My UDP Client", ipAddress, port);
//        }
//
//        @Override
//        public void onConnectionRefused() {
//
//        }
//
//        @Override
//        public void initialize() {
//            send("Hi");
//        }
//
//        @Override
//        public void process() {
////            int counter = 0;
////            while (isConnected()) {
////                send("UDP Message " + counter);
////                counter++;
////                try {
////                    Thread.sleep(2000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
//        }
//
//        @Override
//        public void onReceive(String data) {
//            System.out.println("Received: " + data);
//        }
//
//    }
//
//    public static class MyTCPClient extends TCPClient {
//
//        private long timeSent;
//        private long timeReceived;
//
//        public MyTCPClient(String ipAddress, int port) throws IOException {
//            super("TCPClient", ipAddress, port);
//        }
//
//        @Override
//        public void onConnectionRefused() {
//            System.err.println("Error - Cannot connect client to " + getAddress());
//        }
//
//        @Override
//        public void initialize() {
//
//        }
//
//        @Override
//        public void process() {
////            send("TCP All the way!");
////            timeSent = System.currentTimeMillis();
////            try {
////                Thread.sleep(2000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }
//
//        @Override
//        public void onReceive(String data) {
//            System.out.println("Received: " + data);
////            timeReceived = System.currentTimeMillis();
////            System.out.println("LATENCY ==> " + (timeReceived - timeSent) + "ms");
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
////        MyTCPClient tcpClient = new MyTCPClient("localhost", 7080);
////        MyTCPClient tcpClient2 = new MyTCPClient("localhost", 7082);
////        MyTCPClient tcpClient2 = new MyTCPClient("localhost", 4321);
//        MyUDPClient udpClient = new MyUDPClient("localhost", 7080);
////        MyUDPClient udpClient2 = new MyUDPClient("localhost", 4321);
////        Mocha.start(tcpClient);
////        Mocha.start(tcpClient2);
//        Mocha.start(udpClient);
//    }
//
//}
