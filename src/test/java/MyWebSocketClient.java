import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.client.WebSocketClient;

import java.io.IOException;

public class MyWebSocketClient extends WebSocketClient {
    /**
     * Creates a new WebSocketClient
     *
     * @param name        The client's name.
     * @param endpointURI The client's socket URI.
     * @throws IOException        Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public MyWebSocketClient(String name, String endpointURI) throws IOException, WebSocketException {
        super(name, endpointURI);
        setExecutionDelay(500);
    }

    @Override
    public void onReceive(String data) {
        System.out.println("--> " + data);
    }

    int x = 0;

    @Override
    public void process() {
        send("hi, " + x);
        x++;
        if (x > 10) {
            stop();
        }
    }

    public static void main(String[] args) throws IOException, WebSocketException {
        MyWebSocketClient webSocketClient = new MyWebSocketClient("myWSClient", "wss://echo.websocket.org");
        webSocketClient.start();
    }

}
