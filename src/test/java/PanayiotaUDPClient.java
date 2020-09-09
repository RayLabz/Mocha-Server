//import com.raylabz.mocha.client.UDPClient;
//
//import java.io.IOException;
//
//public class PanayiotaUDPClient {
//
//    public static class TestUDP extends UDPClient {
//
//        private long timeSent;
//        private long timeReceived;
//
//        public TestUDP(String ipAddress, int port) throws IOException {
//            super(ipAddress, port);
//        }
//
//        @Override
//        public void onConnectionRefused() {
//
//        }
//
//        @Override
//        public void onReceive(String data) {
//            timeReceived = System.currentTimeMillis();
//            System.out.println("Received: " + data);
//            System.out.println("Latency: " + (timeReceived - timeSent) + "ms");
//        }
//
//        @Override
//        public void run() {
//            int messageCounter = 0;
//            while (true) {
//                try {
//                    timeSent = System.currentTimeMillis();
//                    send("Message " + messageCounter);
//                    messageCounter++;
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        TestUDP testUDP = new TestUDP("192.168.10.11", 7080);
//        Thread t = new Thread(testUDP);
//        t.start();
//    }
//
//}
