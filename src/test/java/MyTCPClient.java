import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.server.Mocha;
import com.raylabz.mocha.server.Receivable;
import com.raylabz.mocha.server.TCPConnection;

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
        //Code to initialize the client - ran once at the start.
    }

    @Override
    public void process() {
        sendAndReceive("Hi " + messageID, new Receivable() {
            @Override
            public void onReceive(String data) {
                System.out.println("Server: " + data);
            }
        });
        messageID++;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
