package com.raylabz.mocha;

/**
 * Models the initialize-process style of execution.
 */
public interface BackgroundProcessor {

    /**
     * Executes code to initialize the client.
     */
    void initialize();

    /**
     * Defines the processing instructions for this client
     */
    void process();

}
