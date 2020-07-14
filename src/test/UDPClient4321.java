package test;

import com.raylabz.mocha.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient4321 extends UDPClient {

    public UDPClient4321(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient4321 client = new UDPClient4321("cleint", "localhost", 4321);
        client.start();
    }

    @Override
    public void run() {
        while (true) {
            send("Data@4321");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
