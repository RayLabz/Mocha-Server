import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class CustomTCPServer extends Server {
    /**
     * Constructs a new server.
     *
     * @param name The name of the server.
     */
    public CustomTCPServer(String name) {
        super(name);
    }

    @Override
    public void process() {
        try {
            System.out.println("Broadcasting...");
            broadcastTCP(7080, "This is a broadcast message");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CustomTCPServer customTCPServer = new CustomTCPServer("Custom server");
        customTCPServer.addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                System.out.println("Received: " + data);
            }
        }));
        Thread t = new Thread(customTCPServer);
        t.start();
    }

}
