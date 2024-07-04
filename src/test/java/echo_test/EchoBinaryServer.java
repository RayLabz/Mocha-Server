package echo_test;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.server.*;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class EchoBinaryServer extends BinaryServer {

    public EchoBinaryServer() {
        addUDPHandler(new BinaryUDPConnection(4321) {
            @Override
            public void onReceive(BinaryUDPConnection udpConnection, InetAddress address, int outPort, byte[] data) {
                System.out.println("receiveTime (server): " + System.nanoTime());
                byte[] reply = ByteBuffer.allocate(0).array();
                udpConnection.send(address, outPort, reply);
            }
        });
    }

    public static void main(String[] args) {
        Mocha.start(new EchoBinaryServer());
    }

}
