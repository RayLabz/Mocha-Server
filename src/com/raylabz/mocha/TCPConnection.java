package com.raylabz.mocha;

import com.raylabz.mocha.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPConnection implements Runnable {

    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final TCPReceivable receivable;

    public TCPConnection(Socket socket, TCPReceivable receivable) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.receivable = receivable;
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

    public TCPReceivable getReceivable() {
        return receivable;
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = reader.readLine()) != null) {
                receivable.onReceive(socket.getInetAddress(), input);
            }
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            Logger.logError(e.getMessage());
        }
    }

}
