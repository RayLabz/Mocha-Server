import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.server.TextServer;
import com.raylabz.mocha.text.server.TextTCPHandler;
import com.raylabz.mocha.text.server.TextUDPConnection;

import java.net.InetAddress;

public class MyTextServer extends TextServer {

    public MyTextServer(String name) {
        super(name);
        addTCPHandler(new TextTCPHandler(1234, (tcpConnection, data) -> {
            System.out.println("Received message: " + data + " from " + tcpConnection.getInetAddress() + ":" + tcpConnection.getPort());
        }));
        addUDPHandler(new TextUDPConnection(4321) {
            @Override
            public void onReceive(TextUDPConnection udpConnection, InetAddress address, int outPort, String data) {
                System.out.println("Received message: " + data + " from " + udpConnection.getInetAddress() + ":" + udpConnection.getPort());
            }
        });
        setExecutionDelay(1000);
    }

    @Override
    protected void initialize() {
        //This method runs code once, BEFORE the server is started.
    }

    @Override
    protected void process() {
        //This method runs continuously every interval
    }

    @Override
    public void onStop() {
        System.out.println("Stopping server...");
    }

    public static void main(String[] args) {
        Mocha.start(new MyTextServer("Server 1"));
    }

}
