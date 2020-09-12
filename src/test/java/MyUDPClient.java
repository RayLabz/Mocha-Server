import com.raylabz.mocha.client.UDPClient;
import com.raylabz.mocha.server.Mocha;
import com.raylabz.mocha.server.Receivable;

import java.io.IOException;

public class MyUDPClient extends UDPClient {

    public MyUDPClient(String ipAddress, int port) throws IOException {
        super("MyUDPClient", ipAddress, port);
    }

    @Override
    public void onConnectionRefused() {
        //Code to run when the connection is lost or refused.
    }

    @Override
    public void initialize() {
        send("Message " + messageID);
    }

    private int messageID = 0;

    @Override
    public void process() {

    }

    long sendTime;
    long receiveTime;

    @Override
    public void onReceive(String data) {
        receiveTime = System.currentTimeMillis();

        if (messageID < 49) {
            System.out.println(data);
            messageID++;
            send("hi " + messageID);
            sendTime = System.currentTimeMillis();
        }
        else {
            stop();
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        Mocha.start(new MyUDPClient("localhost", 7080));
    }

}
