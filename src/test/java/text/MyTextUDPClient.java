package text;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.client.TextUDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MyTextUDPClient extends TextUDPClient {

    public MyTextUDPClient(String ipAddress, int port) throws UnknownHostException, SocketException {
        super(ipAddress, port);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    @Override
    public void process() {
        send("UDP!");
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        Mocha.start(new MyTextUDPClient("localhost", 4321));
    }

}
