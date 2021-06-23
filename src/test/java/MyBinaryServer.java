import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.server.BinaryServer;
import com.raylabz.mocha.binary.server.BinaryTCPConnection;
import com.raylabz.mocha.binary.server.BinaryTCPHandler;
import com.raylabz.mocha.binary.server.BinaryTCPReceivable;

import java.io.*;

public class MyBinaryServer extends BinaryServer {
    /**
     * Constructs a new server.
     *
     * @param name The name of the server.
     */
    public MyBinaryServer(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        addTCPHandler(new BinaryTCPHandler(7080, (tcpConnection, data) -> {
            final ByteArrayInputStream bis = new ByteArrayInputStream(data);
            final DataInputStream dis = new DataInputStream(bis);
            final int i = dis.readInt();
            int square = i * i;
            System.out.println("received: " + i);

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(square);
                dos.flush();
                sendTCP("localhost", 7080, out.toByteArray());
                System.out.println("Sent: " + square);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }));
    }

    @Override
    protected void process() {

    }

    public static void main(String[] args) {
        Mocha.start(new MyBinaryServer("x"));
    }

}
