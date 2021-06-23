import com.raylabz.bytesurge.schema.IntSchema;
import com.raylabz.bytesurge.stream.StreamReader;
import com.raylabz.bytesurge.stream.StreamWriter;
import com.raylabz.mocha.client.TCPBClient;
import com.raylabz.mocha.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyTCPBinaryClient extends TCPBClient {
    /**
     * Constructs a TCP Client.
     *
     * @param name      The client's name.
     * @param ipAddress The IP address of that this TCP client will connect to.
     * @param port      The port that this TCP client will connect to.
     * @throws IOException Thrown when the socket of this client cannot be instantiated.
     */
    public MyTCPBinaryClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
    }

    @Override
    public void onReceive(Message message) {
        //TODO
    }

    int x = 0;

    @Override
    public void process() {
        try {
            if (x < 20) {
                StreamWriter writer = new StreamWriter(new IntSchema());
                writer.writeInt(x);
                writer.close();
                System.out.println("sent: " + x);
                send(new Message(writer.getBytes()));
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        MyTCPBinaryClient myTCPBinaryClient = new MyTCPBinaryClient("client", "localhost", 7080);
        myTCPBinaryClient.setExecutionDelay(1000);
        myTCPBinaryClient.start();
    }

}
