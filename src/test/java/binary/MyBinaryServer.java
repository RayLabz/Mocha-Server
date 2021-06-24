package binary;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.server.*;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class MyBinaryServer extends BinaryServer {

    public MyBinaryServer() {
        addTCPHandler(new BinaryTCPHandler(1234, new BinaryTCPReceivable() {
            @Override
            public void onReceive(BinaryTCPConnection tcpConnection, byte[] data) throws IOException {
                final int anInt = ByteBuffer.wrap(data).getInt();
                System.out.println(anInt);

                int res = anInt * anInt;
                byte[] reply = ByteBuffer.allocate(4).putInt(res).array();

                tcpConnection.send(reply);
            }
        }));
        addUDPHandler(new BinaryUDPConnection(4321) {
            @Override
            public void onReceive(BinaryUDPConnection udpConnection, InetAddress address, int outPort, byte[] data) {
                final int anInt = ByteBuffer.wrap(data).getInt();
                System.out.println(anInt);

                int res = anInt * anInt;
                byte[] reply = ByteBuffer.allocate(4).putInt(res).array();

                udpConnection.send(address, outPort, reply);
            }
        });
    }

    public static void main(String[] args) {
        Mocha.start(new MyBinaryServer());
    }

}
