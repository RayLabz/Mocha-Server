import com.raylabz.mocha.server.Server;

public class MyCustomServer extends Server {

    public MyCustomServer(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        setExecutionDelay(1000);
    }

    @Override
    public void process() {
        sendTCP("192.168.10.10", 1234, "Hi!");
        sendTCP(tcpConnection, "Hi!");

        sendUDP("192.168.10.10", 1234, "Hi!");
        sendUDP(udpConnection, 1234, "Hi!");
        sendUDP(udpPeer, "Hi!");
    }

    @Override
    public void onStop() {
        System.err.println("Server is about to stop...");
    }

}
