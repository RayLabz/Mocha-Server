package test;

import com.raylabz.mocha.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient1234 extends UDPClient {

    public UDPClient1234(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient1234 client = new UDPClient1234("cleint", "localhost", 1234);
        client.start();
    }

    @Override
    public void run() {
        while (true) {
            send("Data@1234");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
