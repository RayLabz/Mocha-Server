import com.raylabz.mocha.client.TCPClient;

import java.io.IOException;

public class MyTCPClient extends TCPClient {

    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public MyTCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    int x = 0;

    @Override
    public void process() {
        send("hi " + x);
        x++;
        if (x > 9) {
            try {
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MyTCPClient myTCPClient = new MyTCPClient("myTCPClient", "localhost", 7080);
        myTCPClient.start();
    }

}
