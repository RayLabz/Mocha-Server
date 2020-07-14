package com.raylabz.mocha;

import java.net.InetAddress;

public interface TCPReceivable {

    void onReceive(InetAddress inetAddress, String data);

}
