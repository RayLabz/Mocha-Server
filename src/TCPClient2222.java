import com.raylabz.mocha.TCPClient;
import com.raylabz.mocha.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TCPClient2222 extends TCPClient {

    public TCPClient2222(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
    }

    @Override
    public void onReceive(String data) {
        System.out.println("Received -> " + data);
    }

    public static void main(String[] args) throws IOException {
        TCPClient2222 client = new TCPClient2222("tcpClient", "localhost", 2222);
        client.start();
    }

    @Override
    public void run() {
        while (true) {
            send("Data@2222 [TCP]");
            System.out.println("Sent");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
