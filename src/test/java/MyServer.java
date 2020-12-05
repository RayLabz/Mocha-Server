import com.raylabz.mocha.server.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyServer extends Server {

    /**
     * Constructs a new server.
     *
     * @param name         The name of the server.
     * @param securityMode The security mode of the server.
     */
    public MyServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
        setExecutionDelay(500);
        addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                tcpConnection.send("--> " + data);
            }
        }));
        addUDPHandler(new UDPConnection(8888) {
            @Override
            public void onReceive(UDPConnection udpConnection, InetAddress address, int outPort, String data) {
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
        MyServer myServer = new MyServer("myServer", SecurityMode.WHITELIST);
        myServer.start();
    }

}
