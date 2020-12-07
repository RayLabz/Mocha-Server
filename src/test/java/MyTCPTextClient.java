import com.raylabz.mocha.client.TCPTClient;

import java.io.IOException;

public class MyTCPTextClient extends TCPTClient {

    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public MyTCPTextClient(String name, String ipAddress, int port) throws IOException {
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
        if (x > 10) {
            try {
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MyTCPTextClient myTCPClient = new MyTCPTextClient("myTCPClient", "localhost", 7080);
        myTCPClient.start();
    }

}
