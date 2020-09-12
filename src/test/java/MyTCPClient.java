import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.server.Mocha;
import com.raylabz.mocha.server.Receivable;

import java.io.IOException;

public class MyTCPClient extends TCPClient {

    private int messageID = 0;

    public MyTCPClient(String ipAddress, int port) throws IOException {
        super("MyTCPClient", ipAddress, port);
    }

    @Override
    public void onConnectionRefused() {
        //Code to run when the connection is lost or refused.
    }

    @Override
    public void initialize() {
        setExecutionDelay(3000);
    }

    @Override
    public void process() {
        send("Message " + messageID);
        messageID++;
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    public static void main(String[] args) throws IOException {
        MyTCPClient myTCPClient = new MyTCPClient("localhost", 7080);
        Mocha.start(myTCPClient);
    }

}
