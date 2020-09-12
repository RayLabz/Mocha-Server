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
        setExecutionDelay(3000);
    }

    private int messageID = 0;

    @Override
    public void process() {
        sendAndReceive("Hi " + messageID, new Receivable() {
            @Override
            public void onReceive(String data) {
                System.out.println("Server: " + data);
            }
        });
        messageID++;
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    public static void main(String[] args) throws IOException {
        Mocha.start(new MyUDPClient("localhost", 7080));
    }

}
