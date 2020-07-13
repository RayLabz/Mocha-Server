import com.raylabz.mocha.Server;
import com.raylabz.mocha.UDPPortListener;

import java.net.DatagramPacket;

public class Test {

    public static void main(String[] args) {
        Server server = new Server.Builder("My Server")
                .addUDPListener(new UDPPortListener(1234) {
                    @Override
                    public void onReceive(DatagramPacket packet) {
                        String data = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        System.out.println("Received -> " + data);
                    }
                })
                .build();

        server.start();
    }

}
