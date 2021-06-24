package text;

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
            tcpConnection.send(data + " FTW");
        }));
        addUDPHandler(new TextUDPConnection(4321) {
            @Override
            public void onReceive(TextUDPConnection udpConnection, InetAddress address, int outPort, String data) {
                System.out.println("Received message: " + data + " from " + address + ":" + udpConnection.getPort());
                udpConnection.send(address, outPort, data + " FTW");
            }
        });
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void process() {

    }

    @Override
    public void onStop() {
        System.out.println("Stopping server...");
    }

    public static void main(String[] args) {
        Mocha.start(new MyTextServer("Server 1"));
    }

}
