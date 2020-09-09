import com.raylabz.mocha.server.*;

import java.net.InetAddress;

public class ServerExample {


    public static void main(String[] args) {
        BasicServer server = new BasicServer("My Basic Server");
        Mocha.start(server);

        server.removeTCPHandler(4321);
        server.removeTCPHandler(myTCPHandler);

    }

}
