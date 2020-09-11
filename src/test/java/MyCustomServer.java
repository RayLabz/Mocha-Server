import com.raylabz.mocha.server.*;

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
        if (messagesSent < 10) {
            multicastTCP("Hello " + messagesSent, 7080, "localhost", "192.168.10.11");
            messagesSent++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            stop();
        }
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
                System.out.println(tcpConnection.getInetAddress() + ": " + data);
            }
        }));
        Mocha.start(server);
    }

}
