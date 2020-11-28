import com.raylabz.mocha.server.*;

public class MyServer extends Server {

    /**
     * Constructs a new server.
     *
     * @param name         The name of the server.
     * @param securityMode The security mode of the server.
     */
    public MyServer(String name, SecurityMode securityMode) {
        super(name, securityMode);
        addTCPHandler(new TCPHandler(7080, new TCPReceivable() {
            @Override
            public void onReceive(TCPConnection tcpConnection, String data) {
                tcpConnection.send("--> " + data);
            }
        }));
    }

    long startTime;

    @Override
    protected void initialize() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void runIndefinitely() {
        if (System.currentTimeMillis() > startTime + (15 * 1000)) {
            stop();
        }
    }

    public static void main(String[] args) {
        MyServer myServer = new MyServer("myServer", SecurityMode.BLACKLIST);
        myServer.start();
    }

}
