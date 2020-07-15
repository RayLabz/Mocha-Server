package com.raylabz.mocha.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Records data about server operations (such as errors etc.) into a log file.
 * @author Nicos Kasenides
 * @version 1.0.0
 */
public final class Logger {

    /**
     * Obtains an instance of the log file to write the log into.
     * @return Returns a File.
     */
    private static File obtainLogFile() {
        File file = new File("server.log");
        if (file.exists() && file.canWrite()) {
            return file;
        }
        else {
            return null;
        }
    }

    /**
     * Writes a log entry into the log file.
     * @param logType The type of the log entry. Either ERROR, WARNING or INFO.
     * @param text The log information.
     */
    public static void log(final LogType logType, final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                LogEntry logEntry = new LogEntry(logType, text);
                fileWriter.append(logEntry.toString());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    /**
     * Writes an info log entry to the log file.
     * @param text The log information.
     */
    public static void logInfo(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                LogEntry logEntry = new LogEntry(LogType.INFO, text);
                fileWriter.append(logEntry.toString());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    /**
     * Writes a warning log entry to the log file.
     * @param text The log information.
     */
    public static void logWarning(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                LogEntry logEntry = new LogEntry(LogType.WARNING, text);
                fileWriter.append(logEntry.toString());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    /**
     * Writes an errorW log entry to the log file.
     * @param text The log information.
     */
    public static void logError(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                LogEntry logEntry = new LogEntry(LogType.ERROR, text);
                fileWriter.append(logEntry.toString());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

}
