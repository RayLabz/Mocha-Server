/*
 * Copyright (C) 2015 Neo Visionaries Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
import java.io.*;
import com.neovisionaries.ws.client.*;
import com.raylabz.mocha.client.WebSocketClient;


/**
 * A sample WebSocket client application using nv-websocket-client
 * library.
 *
 * <p>
 * This application connects to the echo server on websocket.org
 * ({@code ws://echo.websocket.org}) and repeats to (1) read a
 * line from the standard input, (2) send the read line to the
 * server and (3) print the response from the server, until
 * {@code exit} is entered.
 * </p>
 *
 * @see <a href="https://github.com/TakahikoKawasaki/nv-websocket-client"
 *      >nv-websocket-client</a>
 *
 * @author Takahiko Kawasaki
 */
public class WebSocketClientExample extends WebSocketClient {

    private long sendTime;
    private long receiveTime;

    public WebSocketClientExample(String endpointURI) throws IOException, WebSocketException {
        super(endpointURI);
    }

    @Override
    public void onReceive(String data) {
        receiveTime = System.currentTimeMillis();
        System.out.println("Received -> " + data);
        System.out.println("Latency: " + (receiveTime - sendTime) + "ms");
    }

    @Override
    public void run() {
        while (true) {
            sendTime = System.currentTimeMillis();
            send("Hello!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, WebSocketException, InterruptedException {
        WebSocketClientExample client = new WebSocketClientExample("ws://dev-sphere-283507.oa.r.appspot.com/echo");
        Thread t = new Thread(client);
        t.start();
    }

}