import com.raylabz.mocha.text.client.TextTCPClient;

import java.io.IOException;

public class MyTextTCPClient extends TextTCPClient {

    public MyTextTCPClient(String ipAddress, int port) throws IOException {
        super(ipAddress, port);
    }

    @Override
    public void onReceive(String data) {

    }

}
