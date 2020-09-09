import com.raylabz.mocha.server.Server;

public class MyCustomServer extends Server {

    public MyCustomServer(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        setExecutionDelay(1000);
    }

    @Override
    public void process() {
        try {
            //...
        }
        catch (Exception e) {
            stop();
        }
    }

    @Override
    public void onStop() {
        System.err.println("Server is about to stop...");
    }

}
