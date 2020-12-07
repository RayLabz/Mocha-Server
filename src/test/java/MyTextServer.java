import com.raylabz.mocha.server.*;
import com.raylabz.mocha.server.text.*;

import java.net.InetAddress;

public class MyTextServer extends TextServer {

    /**
     * Constructs a new server.
     *
     * @param name         The name of the server.
     * @param securityMode The security mode of the server.
     */
    public MyTextServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
        setExecutionDelay(500);
        addTCPHandler(new TCPTHandler(7080, new TCPTReceivable() {
            @Override
            public void onReceive(TCPTConnection tcpConnection, String data) {
                tcpConnection.send("--> " + data);
            }
        }));
        addUDPHandler(new UDPTConnection(8888) {
            @Override
            public void onReceive(UDPTConnection udpConnection, InetAddress address, int outPort, String data) {
                udpConnection.send(address, outPort, "--> " + data);
            }
        });
//        whitelistIP("127.0.0.1");
    }

    long startTime;

    @Override
    protected void initialize() {
        startTime = System.currentTimeMillis();
        runInBackground(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
//                    System.out.println(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "backgroundRunningProcess");
    }

    @Override
    protected void runIndefinitely() {
        long timePassed = System.currentTimeMillis() - startTime;
        System.out.println("timePassed = " + timePassed);
        if (System.currentTimeMillis() > startTime + (30 * 1000)) {
            stop();
        }
    }

    public static void main(String[] args) {
        MyTextServer myServer = new MyTextServer("myServer", SecurityMode.WHITELIST);
        myServer.start();
    }

}
