import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class MyCustomServer extends Server {

    public MyCustomServer(String name) {
        super(name);
    }

    private int messagesSent = 0;

    @Override
    public void initialize() {
        setExecutionDelay(1000);
    }

    @Override
    public void process() {
        broadcastUDP(7080, "Hello");
        messagesSent++;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        System.out.println("Server is about to stop...");
    }

    public static void main(String[] args) {

        MyCustomServer server = new MyCustomServer("Server");
        server.addUDPHandler(new UDPConnection(7080) {
            @Override
            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {

            }
        });
        server.banIP("192.168.10.11");
        Mocha.start(server);
    }

}
