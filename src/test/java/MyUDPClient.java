import com.raylabz.mocha.client.UDPClient;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MyUDPClient extends UDPClient {


    /**
     * Constructs a new UDPClient.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address that this client will connect to.
     * @param port      The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException      Thrown when the client's socket cannot be instantiated.
     */
    public MyUDPClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
        super(name, ipAddress, port);
        setExecutionDelay(200);
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    int x = 0;

    @Override
    public void process() {
        send("hi " + x);
//        System.out.println("hi " + x);
        x++;
        if (x > 9) {
            stop();
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        MyUDPClient udpClient = new MyUDPClient("udpClient", "localhost", 8888);
        udpClient.start();
    }

}
