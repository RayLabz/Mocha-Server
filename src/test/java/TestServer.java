import com.raylabz.mocha.message.StringMessageProto;
import com.raylabz.mocha.server.*;

import java.io.IOException;

public class TestServer extends Server<StringMessageProto.StringMessage> {

    /**
     * Constructs a TCP server.
     * @param securityMode       The security mode.
     * @param stringMessageClass The class of the message type sent by this server.
     */
    public TestServer(SecurityMode securityMode, Class<StringMessageProto.StringMessage> stringMessageClass) {
        super(securityMode, stringMessageClass);
    }

    public static void main(String[] args) {
        TestServer server = new TestServer(SecurityMode.BLACKLIST, StringMessageProto.StringMessage.class);
        server.addTCPHandler(new TCPHandler<>(7080, (tcpConnection, stringMessage) -> {
            final String data = stringMessage.getData();
            System.out.println("--> " + data);
            final StringMessageProto.StringMessage message = StringMessageProto.StringMessage.newBuilder()
                    .setData("You said: " + data)
                    .build();
            tcpConnection.send(message);
        }));
        server.start();
    }

}
