import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryTCPClient;

import java.io.*;

public class MyBinaryTCPClient extends BinaryTCPClient {
    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public MyBinaryTCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
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
                System.out.println("Sent " + i);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionRefused(Throwable error) {
        System.err.println("Connection refused " + error.getMessage());
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

    public static void main(String[] args) throws IOException {
        Mocha.start(new MyBinaryTCPClient("cl", "localhost", 7080));
    }

}
