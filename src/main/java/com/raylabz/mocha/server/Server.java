package com.raylabz.mocha.server;

import com.raylabz.mocha.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server implements Runnable {

    public static final String WHITELIST_FILENAME_POSTFIX =  "-whitelist";
    public static final String BLACKLIST_FILENAME_POSTFIX =  "-blacklist";

    /**
     * The server's security mode.
     */
    protected final SecurityMode securityMode;

    /**
     * The server's whitelist - a list of IP addresses for which to <b>accept</b> communication when running in SecurityMode.WHITELIST.
     */
    protected final HashSet<InetAddress> whitelist = new HashSet<>();

    /**
     * The server's blacklist - a list of IP addresses for which to <b>reject</b> communication when running in SecurityMode.BLACKLIST.
     */
    protected final HashSet<InetAddress> blacklist = new HashSet<>();

    /**
     * The name of the server.
     */
    protected final String name;

    /**
     * Whether the server is running or not.
     */
    protected final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Execution delay between calls to the process() method - In <b>MILLISECONDS</b>.
     */
    protected int executionDelay = 0;

    /**
     * A list of threads running the UDP listeners of this server.
     */
    protected final Vector<Thread> udpHandlerThreads = new Vector<>();

    /**
     * A list of threads running the TCP handlers of this server.
     */
    protected final Vector<Thread> tcpHandlerThreads = new Vector<>();

    /**
     * Constructs a new server.
     * @param name The name of the server.
     * @param securityMode The security mode of the server.
     */
    public Server(final String name, final SecurityMode securityMode) {
        this.name = name;

        switch (securityMode) {
            case WHITELIST:

                //Create if not there:
                final File whiteListFile = new File(name + WHITELIST_FILENAME_POSTFIX);
                try {
                    whiteListFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Whitelist error: could not create whitelist file. Will default to SecurityMode.NONE.");
                    Logger.logError("Whitelist error: could not create whitelist file. Will default to SecurityMode.NONE.");
                    this.securityMode = SecurityMode.NONE;
                    break;
                }

                this.securityMode = securityMode;

                //Load:
                try {
                    loadWhitelist();
                } catch (FileNotFoundException e) {
                    System.err.println("Whitelist loading error: file '" + name + WHITELIST_FILENAME_POSTFIX + "' not found.");
                    Logger.logError("Whitelist loading error: file '" + name + WHITELIST_FILENAME_POSTFIX + "' not found.");
                }

                break;

            case BLACKLIST:

                //Create if not there:
                final File blackListFile = new File(name + BLACKLIST_FILENAME_POSTFIX);
                try {
                    blackListFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("Whitelist error: could not create blacklist file. Will default to SecurityMode.NONE.");
                    Logger.logError("Whitelist error: could not create blacklist file. Will default to SecurityMode.NONE.");
                    this.securityMode = SecurityMode.NONE;
                    break;
                }

                this.securityMode = securityMode;

                //Load:
                try {
                    loadBlacklist();
                } catch (FileNotFoundException e) {
                    System.err.println("Blacklist loading error: file '" + name + BLACKLIST_FILENAME_POSTFIX + "' not found.");
                    Logger.logError("Blacklist loading error: file '" + name + BLACKLIST_FILENAME_POSTFIX + "' not found.");
                }

                break;

            case NONE:
            default:
                this.securityMode = SecurityMode.NONE;
                System.out.println("Warning: '" + name + "' started without a security mode - consider using SecurityMode.BLACKLIST or SecurityMode.WHITELIST.");
                Logger.logWarning("Warning: '" + name + "' started without a security mode - consider using SecurityMode.BLACKLIST or SecurityMode.WHITELIST.");
        }
    }

    /**
     * Checks if the server is running.
     * @return Returns true if the server is running, false otherwise.
     */
    public final boolean isRunning() {
        return running.get();
    }

    /**
     * Retrieves the execution delay in milliseconds.
     * @return Returns an integer.
     */
    public final int getExecutionDelay() {
        return executionDelay;
    }

    /**
     * Sets the execution delay.
     * @param executionDelay The delay in milliseconds.
     */
    public final void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    /**
     * Sets the server to be running (true) or not running (false).
     * This method further disables the server's UDP listeners and TCP handlers if the server is set to stop running.
     * @param running Set to true for running, false for not running.
     */
    public final void setRunning(boolean running) {
        this.running.set(running);
    }

    /**
     * Retrieves the list of threads running the server's UDP listeners.
     * @return Returns a Vector of Thread.
     */
    public final Vector<Thread> getUdpHandlerThreads() {
        return udpHandlerThreads;
    }

    /**
     * Retrieves the list of threads running the server's TCP handlers.
     * @return Returns a Vector of Threads.
     */
    public final Vector<Thread> getTcpHandlerThreads() {
        return tcpHandlerThreads;
    }

    /**
     * Retrieves the server's name.
     * @return Returns a string.
     */
    public final String getName() {
        return name;
    }

    /**
     * Retrieves the server's blacklist.
     * @return Returns a HashSet of InetAddress.
     */
    public HashSet<InetAddress> getBlacklist() {
        return blacklist;
    }

    /**
     * Retrieves the server's whitelist.
     * @return Returns a HashSet of InetAddress.
     */
    public HashSet<InetAddress> getWhitelist() {
        return whitelist;
    }

    /**
     * Retrieves the server's SecurityMode.
     * @return Returns a SecurityMode.
     */
    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    /**
     * Initializes the server.
     * Does nothing - must be implemented by extending classes if needed.
     */
    protected void initialize() { }

    /**
     * Executes a piece of code indefinitely in the background, every <i>executionDelay</i> milliseconds
     * Does nothing - must be implemented by extending classes if needed.
     * <b>Important note</b>: This method is executed CONTINUOUSLY during the server's runtime.
     * Make sure to properly optimize this method to avoid performance degradation.
     */
    protected void runIndefinitely() { }

    /**
     * Executes a piece of code in the background.
     * This method is not meant for use with code that must run in a loop indefinitely.
     * For that, consider implementing <i>runIndefinitely()</i> instead.
     * @param runnable A runnable to run.
     * @param tag The runnable's tag, used for identification.
     * @return Returns the thread running the runnable.
     */
    public final Thread runInBackground(final Runnable runnable, final String tag) {
        Thread thread = new Thread(runnable, tag);
        thread.start();
        return thread;
    }

    /**
     * Loads the blacklist from the external file.
     * @throws FileNotFoundException thrown when a malformed IP address is found.
     */
    private void loadBlacklist() throws FileNotFoundException {
        final Scanner fileScanner = new Scanner(new File(name + BLACKLIST_FILENAME_POSTFIX));
        while (fileScanner.hasNextLine()) {
            final String ipAddressStr = fileScanner.nextLine();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddressStr);
                blacklist.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Blacklist loading error: invalid IP '" + ipAddressStr + "'.");
                Logger.logError("Blacklist loading error: invalid IP '" + ipAddressStr + "'.");
                fileScanner.close();
                return;
            }
        }
        System.out.println("Blacklist loaded: '" + name + BLACKLIST_FILENAME_POSTFIX + "' - " + blacklist.size() + " entries");
        Logger.logInfo("Blacklist loaded: '" + name + BLACKLIST_FILENAME_POSTFIX + "' - " + blacklist.size() + " entries");
        fileScanner.close();
    }

    /**
     * Loads the whitelist from the external file.
     * @throws FileNotFoundException thrown when a malformed IP address is found.
     */
    private void loadWhitelist() throws FileNotFoundException {
        final Scanner fileScanner = new Scanner(new File(name + WHITELIST_FILENAME_POSTFIX));
        while (fileScanner.hasNextLine()) {
            final String ipAddressStr = fileScanner.nextLine();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddressStr);
                whitelist.add(inetAddress);
            } catch (UnknownHostException e) {
                System.err.println("Whitelist loading error: invalid IP '" + ipAddressStr + "'.");
                Logger.logError("Whitelist loading error: invalid IP '" + ipAddressStr + "'.");
                fileScanner.close();
                return;
            }
        }
        System.out.println("Whitelist loaded: '" + name + WHITELIST_FILENAME_POSTFIX + "' - " + whitelist.size() + " entries");
        Logger.logInfo("Whitelist loaded: '" + name + WHITELIST_FILENAME_POSTFIX + "' - " + whitelist.size() + " entries");
        fileScanner.close();
    }

    /**
     * Saves the blacklist into a file.
     */
    private void saveBlacklistToFile() {
        try {
            FileWriter fileWriter = new FileWriter(new File(name + BLACKLIST_FILENAME_POSTFIX));
            final StringBuilder stringBuilder = new StringBuilder();
            for (final InetAddress ip : blacklist) {
                stringBuilder.append(ip.toString().split(":")[0].replace("/", "")).append(System.lineSeparator());
            }
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Blacklist save error: could not write list to file '" + name + BLACKLIST_FILENAME_POSTFIX);
            Logger.logError("Blacklist save error: could not write list to file '" + name + BLACKLIST_FILENAME_POSTFIX);
        }
    }

    /**
     * Saves the whitelist into a file.
     */
    private void saveWhitelistToFile() {
        try {
            FileWriter fileWriter = new FileWriter(new File(name + WHITELIST_FILENAME_POSTFIX));
            final StringBuilder stringBuilder = new StringBuilder();
            for (final InetAddress ip : whitelist) {
                stringBuilder.append(ip.toString().split(":")[0].replace("/", "")).append(System.lineSeparator());
            }
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Whitelist save error: could not write list to file '" + name + WHITELIST_FILENAME_POSTFIX);
            Logger.logError("Whitelist save error: could not write list to file '" + name + WHITELIST_FILENAME_POSTFIX);
        }
    }

    /**
     * Bans an IP address.
     * @param inetAddress The IP address to ban.
     */
    public final void banIP(InetAddress inetAddress) {
        if (securityMode == SecurityMode.BLACKLIST) {
            blacklist.add(inetAddress);
            saveBlacklistToFile();
            System.out.println("'" + name + "' Banned IP: " + inetAddress.toString());
            Logger.logInfo("'" + name + "' Banned IP: " + inetAddress.toString());
        }
        else {
            System.err.println("Cannot ban IP '" + inetAddress.toString() + "' while not on blacklist mode.");
            Logger.logWarning("Cannot ban IP while not on blacklist mode.");
        }
    }

    /**
     * Bans an IP address.
     * @param ipAddress The IP address to ban.
     */
    public final void banIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            banIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Unban error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Un-bans an IP address.
     * @param inetAddress The IP address to unban.
     */
    public final void unbanIP(InetAddress inetAddress) {
        if (securityMode == SecurityMode.BLACKLIST) {
            blacklist.remove(inetAddress);
            saveBlacklistToFile();
            System.out.println("'" + name + "' Un-banned IP: " + inetAddress.toString());
            Logger.logInfo("'" + name + "' Un-banned IP: " + inetAddress.toString());
        }
        else {
            System.err.println("Cannot unban IP '" + inetAddress.toString() + "' while not on blacklist mode.");
            Logger.logWarning("Cannot unban IP while not on blacklist mode.");
        }
    }

    /**
     * Un-bans an IP address.
     * @param ipAddress The IP address to unban.
     */
    public final void unbanIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            unbanIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Unban error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Whitelists an IP address.
     * @param address The address to whitelist.
     */
    public final void whitelistIP(InetAddress address) {
        if (securityMode == SecurityMode.WHITELIST) {
            whitelist.add(address);
            saveWhitelistToFile();
            System.out.println("'" + name + "' Whitelisted IP: " + address.toString());
            Logger.logInfo("'" + name + "' Whitelisted IP: " + address.toString());
        }
        else {
            System.err.println("Cannot whitelist IP '" + address.toString() + "' while not on whitelist mode.");
            Logger.logWarning("Cannot whitelist IP '" + address.toString() + "' while not on whitelist mode.");
        }
    }

    /**
     * Whitelists an IP address.
     * @param ipAddress The IP address to whitelist.
     */
    public final void whitelistIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            whitelistIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Whitelist error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    public final void unWhitelistIP(InetAddress address) {
        if (securityMode == SecurityMode.WHITELIST) {
            whitelist.remove(address);
            saveWhitelistToFile();
            System.out.println("'" + name + "' IP: " + address.toString() + " removed from whitelist.");
            Logger.logInfo("'" + name + "' IP: " + address.toString() + " removed from whitelist.");
        }
        else {
            System.err.println("Cannot un-whitelist IP '" + address.toString() + "' while not on whitelist mode.");
            Logger.logWarning("Cannot un-whitelist IP '" + address.toString() + "' while not on whitelist mode.");
        }
    }

    public final void unWhitelistIP(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            unWhitelistIP(address);
        } catch (UnknownHostException e) {
            System.err.println("Un-whitelist error: " + e.getMessage());
            Logger.logError(e.getMessage());
        }
    }

    /**
     * Executes code before shutting down the server.
     * Does nothing by default - must be implemented (if needed) by the extending class.
     */
    public void onStop() { }

    /**
     * Starts the server.
     * @return Returns the thread running this client.
     */
    public final Thread start() {
        Thread thread = new Thread(this, name);
        thread.start();
        return thread;
    }

}
