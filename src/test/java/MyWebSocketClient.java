//import com.neovisionaries.ws.client.WebSocketException;
//import com.raylabz.mocha.client.TCPClient;
//import com.raylabz.mocha.client.WebSocketClient;
//import Mocha;
//
//import java.io.IOException;
//
//public class MyWebSocketClient extends WebSocketClient {
//
//    public MyWebSocketClient(String endpointURI) throws IOException, WebSocketException {
//        super("MyWebSocketClient", endpointURI);
//    }
//
//    @Override
//    public void initialize() {
//        //Code to initialize the client - ran once at the start.
//    }
//
//    @Override
//    public void process() {
//        //Code to run continuously for processing.
//    }
//
//    @Override
//    public void onReceive(String data) {
//        //Code that is executed once a message is received.
//    }
//
//    public static void main(String[] args) {
//        try {
//
//            MyTCPClient myTCPClient = new MyTCPClient("192.168.10.10", 1234);
//            Mocha.start(myTCPClient);
//
//            MyUDPClient myUDPClient = new MyUDPClient("192.168.10.10", 1234);
//            Mocha.start(myUDPClient);
//
//            MyWebSocketClient myWebSocketClient = new MyWebSocketClient("wss://echo.websocket.org");
//            myWebSocketClient.setExecutionDelay(1000);
//
//            myTCPClient.setListening(true); //Enable listening
//            myUDPClient.setListening(false); //Disable listening
//
//            Mocha.start(myWebSocketClient);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (WebSocketException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
