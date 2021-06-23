import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.client.TextTCPClient;

import java.io.IOException;

public class TCPClient extends TextTCPClient {
    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public TCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process() {
        send("Hi");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionRefused() {

    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    public static void main(String[] args) throws IOException {
        Mocha.start(new TCPClient("a", "localhost", 7080));
    }

}
