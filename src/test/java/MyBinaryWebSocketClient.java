import com.neovisionaries.ws.client.WebSocketException;
import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryWebSocketClient;

import java.io.*;

public class MyBinaryWebSocketClient extends BinaryWebSocketClient {
    /**
     * Creates a new WebSocketClient
     *
     * @param name        The client's name.
     * @throws IOException        Throws an exception when the socket cannot be created.
     * @throws WebSocketException Throws an exception when the socket cannot be created.
     */
    public MyBinaryWebSocketClient(String name) throws IOException, WebSocketException {
        super(name, "ws://localhost:8080/api/test/websocket");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process() {
        try {
            for (int i = 0; i < 100; i++) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(byteArrayOutputStream);
                dos.writeInt(i);
                send(byteArrayOutputStream.toByteArray());

                dos.close();
                byteArrayOutputStream.close();
                System.out.println("Sent " + i);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(byte[] data) {
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(data);
            final DataInputStream dis = new DataInputStream(bis);
            final int i = dis.readInt();
            System.out.println("Response -> " + i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws WebSocketException, IOException {
        Mocha.start(new MyBinaryWebSocketClient("x"));
    }

}
