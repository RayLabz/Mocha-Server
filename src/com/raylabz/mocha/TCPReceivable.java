package com.raylabz.mocha;

import java.net.InetAddress;

public interface TCPReceivable {

    void onReceive(TCPConnection tcpConnection, String data);

}
