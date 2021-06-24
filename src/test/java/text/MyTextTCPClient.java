package text;

import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.client.TextTCPClient;

import java.io.IOException;

public class MyTextTCPClient extends TextTCPClient {

    public MyTextTCPClient(String ipAddress, int port) throws IOException {
        super(ipAddress, port);
        setExecutionDelay(1000);
    }

    @Override
    public void onReceive(String data) {
        System.out.println(data);
    }

    @Override
    public void process() {
        send("TCP!");
    }

    public static void main(String[] args) throws IOException {
        Mocha.start(new MyTextTCPClient("localhost", 1234));
    }

}
