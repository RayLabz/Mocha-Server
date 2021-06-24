package binary;

import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryWebSocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MyBinaryWebSocketClient extends BinaryWebSocketClient {

    public MyBinaryWebSocketClient(String endpointURI) throws IOException, WebSocketException {
        super(endpointURI);
        setExecutionDelay(1000);
    }

    int i = 0;

    @Override
    public void process() {
        final byte[] array = ByteBuffer.allocate(4).putInt(i).array();
        send(array);
        i++;
    }

    @Override
    public void onReceive(byte[] data) {
        final int anInt = ByteBuffer.wrap(data).getInt();
        System.out.println(anInt);
    }

    public static void main(String[] args) throws WebSocketException, IOException {
        Mocha.start(new MyBinaryWebSocketClient("ws://localhost:8080/api/test/websocket"));
    }

}
