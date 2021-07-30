import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryWebSocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;

public class WebSocketClient extends BinaryWebSocketClient {

    public WebSocketClient(String name, String endpointURI) throws IOException, WebSocketException {
        super(name, endpointURI);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(byte[] data) {
        final int anInt = ByteBuffer.wrap(data).getInt();
        System.out.println(anInt);
    }

    int i = 0;

    @Override
    public void process() {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
        send(bytes);
        i++;
    }

    public static void main(String[] args) throws WebSocketException, IOException {
        WebSocketClient c = new WebSocketClient("ws", "ws://localhost:8080/api/test/websocket");
        Mocha.start(c);
    }

}
