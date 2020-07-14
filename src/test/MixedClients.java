package test;

import com.raylabz.mocha.TCPClient;
import com.raylabz.mocha.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MixedClients {

    public static class MyUDPClient extends UDPClient {

        public MyUDPClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
            super(name, ipAddress, port);
        }

        @Override
        public void run() {
            while (true) {
                send("UDP!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class MyTCPClient extends TCPClient {

        public MyTCPClient(String name, String ipAddress, int port) throws IOException {
            super(name, ipAddress, port);
        }

        @Override
        public void run() {
            while (true) {
                send("TCP!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onReceive(String data) {
            System.out.println(data);
        }
    }

    public static void main(String[] args) throws IOException {
        MyTCPClient tcpClient = new MyTCPClient("tcpClient", "localhost", 1234);
        MyTCPClient tcpClient2 = new MyTCPClient("tcpClient2", "localhost", 4321);
        MyUDPClient udpClient = new MyUDPClient("udpClient", "localhost", 1234);
        MyUDPClient udpClient2 = new MyUDPClient("udpClient2", "localhost", 4321);
        tcpClient.start();
        tcpClient2.start();
        udpClient.start();
        udpClient2.start();
    }

}
