import com.raylabz.bytesurge.schema.IntSchema;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.mocha.message.Message;
import com.raylabz.mocha.server.SecurityMode;
import com.raylabz.mocha.server.binary.BinaryServer;
import com.raylabz.mocha.server.binary.TCPBConnection;
import com.raylabz.mocha.server.binary.TCPBHandler;
import com.raylabz.mocha.server.binary.TCPBReceivable;

import java.io.IOException;

public class MyBinaryServer extends BinaryServer {
    /**
     * Constructs a TCP server.
     *
     * @param name         The name of the server.
     * @param securityMode The security mode.
     */
    public MyBinaryServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
    }

    public static void main(String[] args) {
        MyBinaryServer myBinaryServer = new MyBinaryServer("binServer", SecurityMode.BLACKLIST);
        myBinaryServer.addTCPHandler(new TCPBHandler(7080, new TCPBReceivable() {
            @Override
            public void onReceive(TCPBConnection tcpConnection, Message message) throws IOException {
                StreamReader reader = new StreamReader(new IntSchema(), message.getData());
                final int i = reader.readInt();
                System.out.println("--> " + i);
            }
        }));
        myBinaryServer.start();
    }

}
