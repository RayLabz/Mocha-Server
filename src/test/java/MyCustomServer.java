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
//        if (messagesSent < 10) {
//            broadcastTCP(1234, "Hello");
//            messagesSent++;
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            stop();
//        }
    }

    @Override
    public void onStop() {
        System.err.println("Server is about to stop...");
    }

    public static void main(String[] args) {

        MyCustomServer server = new MyCustomServer("Server");
        server.addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                tcpConnection.send(data);
            }
        }));
//        server.addUDPHandler(new UDPConnection(7080) {
//            @Override
//            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
//                udpConnection.send(address, outPort, "You said: " + data);
//            }
//        });
        Mocha.start(server);
    }

}
