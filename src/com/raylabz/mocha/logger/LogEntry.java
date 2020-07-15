package com.raylabz.mocha.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Models an entry written into the server log files.
 */
public class LogEntry {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(" yyyy-MM-dd HH:mm ");

    /**
     * The type of entry (either INFO, WARNING, or ERROR).
     */
    private final LogType type;

    /**
     * The time this entry was created.
     */
    private final long time;

    /**
     * The entry's information text.
     */
    private final String info;

    /**
     * Constructs a new LogEntry.
     * @param type The type of the entry.
     * @param info The info text of the entry.
     */
    public LogEntry(LogType type, String info) {
        this.type = type;
        this.time = System.currentTimeMillis();
        this.info = info;
    }

    /**
     * Retrieves the type of the entry.
     * @return Returns a LogType.
     */
    public final LogType getType() {
        return type;
    }

    /**
     * Retrieves the time this entry was created on.
     * @return Returns a long (timestamp)
     */
    public final long getTime() {
        return time;
    }

    /**
     * Retrieves the information text for this entry.
     * @return Returns a String.
     */
    public final String getInfo() {
        return info;
    }

    /**
     * Converts a LogEntry into a text-based format for printing or writing into files.
     * @return Returns a String.
     */
    @Override
    public final String toString() {
        return type + "\t" + DATE_FORMAT.format(new Date(time)) + "\t" + info + System.lineSeparator();
    }

}
