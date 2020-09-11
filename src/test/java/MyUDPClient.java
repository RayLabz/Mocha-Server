import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.client.UDPClient;

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
        //Code to initialize the client - ran once at the start.
    }

    @Override
    public void process() {
        //Code to run continuously for processing.
    }

    @Override
    public void onReceive(String data) {
        //Code that is executed once a message is received.
    }

}
