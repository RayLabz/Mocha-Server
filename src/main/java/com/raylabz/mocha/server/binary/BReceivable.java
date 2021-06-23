package com.raylabz.mocha.server.binary;

import com.raylabz.mocha.message.Message;

/**
 * Models a Receivable, used to determine what is executed when a response is send back from the server.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public interface BReceivable {

    /**
     * Executes code when data is received.
     * @param message The message received.
     */
    void onReceive(Message message);

}
