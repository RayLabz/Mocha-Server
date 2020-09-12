import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.server.Mocha;
import com.raylabz.mocha.server.Receivable;

import java.io.IOException;
import java.util.ArrayList;

public class MyTCPClient extends TCPClient {

    private long timeSent = 0;
    private int messageID = 0;

    private ArrayList<Long> results = new ArrayList<>();

    public MyTCPClient(String ipAddress, int port) throws IOException {
        super("MyTCPClient", ipAddress, port);
    }

    @Override
    public void onConnectionRefused() {
        //Code to run when the connection is lost or refused.
    }

    @Override
    public void initialize() {
        setExecutionDelay(0);
    }

    @Override
    public void process() {
        if (messageID <= 99) {
            send("Hi!");
            timeSent = System.currentTimeMillis();
            messageID++;
        }
        else {
            stop();
        }
    }

    @Override
    public void onReceive(String data) {
        results.add((System.currentTimeMillis() - timeSent));
    }

    public static void main(String[] args) throws IOException {
        final int numOfClients = 1;
        for (int i = 0; i < numOfClients; i++) {
            MyTCPClient myTCPClient = new MyTCPClient("localhost", 7080);
            Mocha.start(myTCPClient);
        }
    }

}
