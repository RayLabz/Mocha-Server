import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.text.client.TextWebSocketClient;

import java.io.IOException;

public class MyTextWebSocketClient extends TextWebSocketClient {

    public MyTextWebSocketClient(String endpointURI) throws IOException, WebSocketException {
        super(endpointURI);
    }

    @Override
    public void onReceive(String data) {
        //This method defines what happens when data is received.
    }

}
