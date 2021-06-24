package text;

import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.client.TextWebSocketClient;

import java.io.IOException;

public class MyTextWebSocketClient extends TextWebSocketClient {

    public MyTextWebSocketClient(String endpointURI) throws IOException, WebSocketException {
        super(endpointURI);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    @Override
    public void process() {
        send("{\"type\": \"HANDSHAKE\", \"worldSessionID\": \"abcd\"}");
    }

    public static void main(String[] args) throws WebSocketException, IOException {
        Mocha.start(new MyTextWebSocketClient("ws://localhost:8080/api/play"));
    }

}
