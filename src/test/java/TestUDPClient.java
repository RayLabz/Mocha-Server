import com.raylabz.mocha.client.TCPClient;
import com.raylabz.mocha.client.UDPClient;
import com.raylabz.mocha.message.StringMessageProto;

import java.io.IOException;

public class TestUDPClient extends UDPClient<StringMessageProto.StringMessage> {
    /**
     * Constructs a TCP Client.
     *
     * @param stringMessageClass ...
     * @param ipAddress          The IP address of that this TCP client will connect to.
     * @param port               The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public TestUDPClient(Class<StringMessageProto.StringMessage> stringMessageClass, String ipAddress, int port) throws IOException {
        super(stringMessageClass, ipAddress, port);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(StringMessageProto.StringMessage stringMessage) {
        final String data = stringMessage.getData();
        System.out.println("--> " + data);
    }

    int x = 0;

    @Override
    public void doContinuously() {
        final StringMessageProto.StringMessage message = StringMessageProto.StringMessage.newBuilder()
                .setData(String.valueOf(x))
                .build();
        send(message);
        x++;
    }

    public static void main(String[] args) throws IOException {
        TestUDPClient client = new TestUDPClient(StringMessageProto.StringMessage.class, "localhost", 7080);
        client.start();
    }

}
