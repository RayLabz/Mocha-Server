import com.raylabz.mocha.Mocha;
import com.raylabz.mocha.text.server.TextTCPConnection;
import com.raylabz.mocha.text.server.TextTCPHandler;
import com.raylabz.mocha.text.server.TextTCPReceivable;

public class TextServer extends com.raylabz.mocha.text.server.TextServer {
    /**
     * Constructs a new server.
     *
     * @param name The name of the server.
     */
    public TextServer(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        addTCPHandler(new TextTCPHandler(7080, new TextTCPReceivable() {
            @Override
            public void onReceive(TextTCPConnection tcpConnection, String data) {
                System.out.println(data);
                tcpConnection.send(data);
            }
        }));
    }

    @Override
    protected void process() {

    }

    public static void main(String[] args) {
        Mocha.start(new TextServer("x"));
    }

}
