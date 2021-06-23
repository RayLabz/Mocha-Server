import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.binary.client.BinaryUDPClient;

import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MyBinaryUDPClient extends BinaryUDPClient {
    /**
     * Constructs a new UDPClient.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address that this client will connect to.
     * @param port      The port that this client will connect through.
     * @throws UnknownHostException Thrown when the IP address is invalid.
     * @throws SocketException      Thrown when the client's socket cannot be instantiated.
     */
    public MyBinaryUDPClient(String name, String ipAddress, int port) throws UnknownHostException, SocketException {
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
    public void onConnectionRefused(Throwable error) {
        System.err.println(error.getMessage());
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

    public static void main(String[] args) throws SocketException, UnknownHostException {
        Mocha.start(new MyBinaryUDPClient("udp", "localhost", 7080));
    }

}
