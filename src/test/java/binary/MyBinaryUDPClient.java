package binary;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryTCPClient;
import com.raylabz.mocha.binary.client.BinaryUDPClient;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MyBinaryUDPClient extends BinaryUDPClient {

    public MyBinaryUDPClient(String address, int port) throws IOException {
        super(address, port);
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

    public static void main(String[] args) throws IOException {
        Mocha.start(new MyBinaryUDPClient("localhost", 4321));
    }

}
