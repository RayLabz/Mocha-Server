package com.raylabz.mocha.client;

/**
 * Models the initialize-process style of execution.
 */
public interface BackgroundRunner {

    /**
     * Executes code to initialize the client.
     * Does nothing by default - should be implemented by extending classes.
     */
    void initialize();

    /**
     * Defines the processing instructions for this client.
     * Does nothing by default - should be implemented by extending classes.
     */
    void doContinuously();

}
