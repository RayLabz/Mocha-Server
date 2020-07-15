package test;

import com.raylabz.mocha.server.Server;
import com.raylabz.mocha.server.TCPConnection;
import com.raylabz.mocha.server.TCPHandler;
import com.raylabz.mocha.server.TCPReceivable;

public class TestServer {

    public static void main(String[] args) {

        Server server = new Server("My server");
        server.addTCPHandler(new TCPHandler(1234, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                System.out.println("Received from TCP[2222]: "  + data);
                tcpConnection.send("Server response!");
            }
        }));

        Thread thread = new Thread(server);
        thread.start();
    }

}
