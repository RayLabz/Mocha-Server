package binary;

import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryWebSocketClient;

import java.io.IOException;

public class MPTest extends BinaryWebSocketClient {
    public MPTest(String name, String endpointURI) throws IOException, WebSocketException {
        super(name, endpointURI);
    }

    @Override
    public void onReceive(byte[] data) {
        System.out.println("Data received: " + data.length);
    }

    public static void main(String[] args) throws WebSocketException, IOException {
        for (int i = 0; i < 100; i++) {
            MPTest test = new MPTest("client", "ws://localhost:8080/api/test/websocket");
            Mocha.start(test);
        }

    }

}
