package com.raylabz.mocha.logger;


import com.raylabz.mocha.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(" yyyy-MM-dd HH:mm ");

    private static File obtainLogFile() {
        File file = new File(Server.SERVER_NAME + ".log");
        if (file.exists() && file.canWrite()) {
            return file;
        }
        else {
            return null;
        }
    }

    public static void log(final LogType logType, final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                Date date = new Date(System.currentTimeMillis());
                fileWriter.append(logType.toString()).append(DATE_FORMAT.format(date)).append(text).append(System.lineSeparator());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    public static void logInfo(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                Date date = new Date(System.currentTimeMillis());
                fileWriter.append(LogType.INFO.toString()).append(DATE_FORMAT.format(date)).append(text).append(System.lineSeparator());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    public static void logWarning(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                Date date = new Date(System.currentTimeMillis());
                fileWriter.append(LogType.WARNING.toString()).append(DATE_FORMAT.format(date)).append(text).append(System.lineSeparator());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

    public static void logError(final String text) {
        File file = obtainLogFile();
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                Date date = new Date(System.currentTimeMillis());
                fileWriter.append(LogType.ERROR.toString()).append(DATE_FORMAT.format(date)).append(text).append(System.lineSeparator());
                fileWriter.close();
            }
            catch (IOException e) {
                System.err.println("Logger cannot write to log file.");
            }
        }
    }

}
