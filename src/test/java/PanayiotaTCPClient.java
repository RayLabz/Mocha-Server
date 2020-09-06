import com.raylabz.mocha.client.TCPClient;

import java.io.IOException;

public class PanayiotaTCPClient {

    public static class TestTCP extends TCPClient {

        private long timeSent;
        private long timeReceived;

        public TestTCP(String ipAddress, int port) throws IOException {
            super(ipAddress, port);
        }

        @Override
        public void onReceive(String data) {
            timeReceived = System.currentTimeMillis();
            System.out.println("Received: " + data);
            System.out.println("Latency: " + (timeReceived - timeSent) + "ms");
        }

        @Override
        public void run() {
            int messageCounter = 0;
            while (true) {
                try {
                    timeSent = System.currentTimeMillis();
                    send("Message " + messageCounter);
                    messageCounter++;
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TestTCP testTCP = new TestTCP("192.168.10.11", 7080);
        Thread t = new Thread(testTCP);
        t.start();
    }

}
