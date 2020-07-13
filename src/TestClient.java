import com.raylabz.mocha.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;

public class TestClient extends UDPClient {

    public TestClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        TestClient client = new TestClient("cleint", "localhost", 1234);
        client.start();
    }

    @Override
    public void run() {
        while (true) {
            send("Data");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
