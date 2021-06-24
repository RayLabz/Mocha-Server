import com.raylabz.mocha.text.client.TextUDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MyTextUDPClient extends TextUDPClient {

    public MyTextUDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
        super(ipAddress, port);
    }

    @Override
    public void onReceive(String data) {
        //This method defines what happens when data is received.
    }

}
