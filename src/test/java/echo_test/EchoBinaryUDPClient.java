package echo_test;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryUDPClient;

import java.io.IOException;
import java.nio.ByteBuffer;

public class EchoBinaryUDPClient extends BinaryUDPClient {

    public EchoBinaryUDPClient(String address, int port) throws IOException {
        super(address, port);
        setExecutionDelay(1000);
    }

    long send = -1;

    @Override
    public void process() {
        final byte[] array = ByteBuffer.allocate(0).array();
        send = System.nanoTime();
        send(array);
    }

    @Override
    public void onReceive(byte[] data) {
        System.out.println((System.nanoTime() - send));
//        final int anInt = ByteBuffer.wrap(data).getInt();
//        System.out.println(anInt);
    }

    public static void main(String[] args) throws IOException {
        Mocha.start(new EchoBinaryUDPClient("localhost", 4321));
    }

}
