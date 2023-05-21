package net.domraczpvp.java.onlinechat.server;

import java.util.Arrays;

public class CommandParser {
    public static String[] getArgs(String commandstring) {
        commandstring = commandstring.replace("/", "");
        String[] parts = commandstring.split(" ");
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        return args;
    }
    public static String getCommand(String commandstring) {
        commandstring = commandstring.replace("/", "");
        String[] parts = commandstring.split(" ");
        return parts[0];
    }
}
