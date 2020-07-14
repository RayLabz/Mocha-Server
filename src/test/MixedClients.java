package test;

import com.raylabz.mocha.TCPClient;
import com.raylabz.mocha.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MixedClients {

    public static class MyUDPClient extends UDPClient {

        public MyUDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
            super(ipAddress, port);
        }

        @Override
        public void onReceive(String data) {
            System.out.println("Received: " + data);
        }

        @Override
        public void run() {
            while (true) {
                send("UDP FTW!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class MyTCPClient extends TCPClient {

        private long timeSent;
        private long timeReceived;

        public MyTCPClient(String ipAddress, int port) throws IOException {
            super(ipAddress, port);
        }

        @Override
        public void run() {
            while (true) {
                send("TCP All the way!");
                timeSent = System.currentTimeMillis();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onReceive(String data) {
            System.out.println("Received: " + data);
            timeReceived = System.currentTimeMillis();
            System.out.println("LATENCY ==> " + (timeReceived - timeSent) + "ms");
        }
    }

    public static void main(String[] args) throws IOException {
        MyTCPClient tcpClient = new MyTCPClient("localhost", 1234);
//        MyTCPClient tcpClient2 = new MyTCPClient("localhost", 4321);
        MyUDPClient udpClient = new MyUDPClient("localhost", 1234);
//        MyUDPClient udpClient2 = new MyUDPClient("localhost", 4321);
        new Thread(tcpClient).start();
        new Thread(udpClient).start();
    }

}
