package com.raylabz.mocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class TCPClient extends Client {

    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
//    private final Thread receptionThread;

    public TCPClient(String name, String ipAddress, int port) throws IOException {
        super(name, ipAddress, port);
        this.socket = new Socket(getAddress(), getPort());
        writer = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        receptionThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String input;
//                        try {
//                            while ((input = reader.readLine()) != null) {
//                                onReceive(input);
//                            }
//                        }
//                        catch (IOException e) {
//                            System.err.println("Error receiving: " + e.getMessage());
//                        }
//                    }
//                }, getName());
//        receptionThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public void send(String data) {
        writer.println(data);
    }

    public abstract void onReceive(String data);

}
