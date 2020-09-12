import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.server.Mocha;
import com.raylabz.mocha.server.Receivable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class MyTCPClient extends TCPClient {

    private long timeSent = 0;
    private int messageID = 0;

    private Vector<Long> results = new Vector<>();

    public MyTCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
    }

    @Override
    public void onConnectionRefused() {
        //Code to run when the connection is lost or refused.
    }

    @Override
    public void initialize() {
//        setExecutionDelay(500);
        send("init");
    }

    @Override
    public void process() {
    }

    long sendTime;
    long receiveTime;
    boolean init = false;

    @Override
    public void onReceive(String data) {
        if (init) {
            receiveTime = System.currentTimeMillis();
        }
        else {
            init = true;
        }
        results.add(receiveTime - sendTime);
        if (messageID < 49) {
//            System.out.println(data);
            messageID++;
            send("hi " + messageID);
            sendTime = System.currentTimeMillis();
        }
        else {
            stop();
            double average = 0;
            long sum = 0;
            for (long l : results) {
                sum += l;
            }
            average = (double) sum / results.size();
            System.out.println(average);
        }
    }

    public static void main(String[] args) throws IOException {
        final int numOfClients = 8;
        for (int i = 0; i < numOfClients; i++) {
            MyTCPClient myTCPClient = new MyTCPClient("client-" + i, "localhost", 7080);
            Mocha.start(myTCPClient);
        }
    }

}
