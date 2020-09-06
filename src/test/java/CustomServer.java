import com.raylabz.mocha.server.Server;
import com.raylabz.mocha.server.UDPConnection;

import java.net.InetAddress;

public class CustomServer extends Server {
    /**
     * Constructs a new server.
     *
     * @param name The name of the server.
     */
    public CustomServer(String name) {
        super(name);
    }

    @Override
    public void process() {
        try {
            broadcastUDP(7080, "This is a broadcast message");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CustomServer customServer = new CustomServer("Custom server");
        customServer.addUDPListener(new UDPConnection(7080) {
            @Override
            public void onReceive(UDPConnection udpConnection, InetAddress address, int port, String data) {
                System.out.println("Received: " + data);
            }
        });
        Thread t = new Thread(customServer);
        t.start();
    }

}
