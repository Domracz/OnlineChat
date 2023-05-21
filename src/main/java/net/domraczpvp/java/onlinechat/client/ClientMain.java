package net.domraczpvp.java.onlinechat.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientMain {
    static Scanner input;
    static Socket connection;
    static InputStream in;
    static OutputStream out;
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        System.out.println("OnlineChat - Console Chat");
        System.out.println("-------------------------");
        System.out.println("Please use login dialog to join a server.");
        String ip = JOptionPane.showInputDialog("Please enter server ip: ");
        String username = JOptionPane.showInputDialog("Please enter username.");
        connect(ip, username);
    }
    public static String[] splitData(String message) {
        List<String> matches = new ArrayList<>();

        // Match non-escaped characters or escaped backslashes
        Pattern pattern = Pattern.compile("(?:[^\\\\@]|\\\\.)*");
        Matcher matcher = pattern.matcher(message);

        // Find all matches in the message
        while (matcher.find()) {
            String match = matcher.group();

            // Remove the escaped backslashes from each match
            match = match.replaceAll("\\\\(.)", "$1");

            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        String[] splitarray = new String[matches.size()];
        splitarray = matches.toArray(splitarray);
        return splitarray;
    }

    public static String[] splitParts(String message) {
        List<String> matches = new ArrayList<>();

        // Match non-escaped characters or escaped backslashes
        Pattern pattern = Pattern.compile("(?:[^\\\\&]|\\\\.)*");
        Matcher matcher = pattern.matcher(message);

        // Find all matches in the message
        while (matcher.find()) {
            String match = matcher.group();

            // Remove the escaped backslashes from each match
            match = match.replaceAll("\\\\(.)", "$1");

            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        String[] splitarray = new String[matches.size()];
        splitarray = matches.toArray(splitarray);
        return splitarray;
    }

    public static void connect(String serverip, String username) {
        try {
            System.out.println("Connecting...");
            System.out.println("Joining server: " + serverip);
            connection = new Socket(serverip, 1234);
            in = connection.getInputStream();
            out = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("CHUN@" + escapeString(username));
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (true) {
                if (in.available() > 0) {
                    String data = reader.readLine();
                    String[] datasplit = splitData(data);
                    if (datasplit[0].equals("UNRE")) {
                        username = JOptionPane.showInputDialog(datasplit[1] + " Please re-enter your username.");
                        writer.println("CHUN@" + escapeString(username));
                        writer.flush();
                    } else if (datasplit[0].equals("UNSC")) {
                        break;
                    }
                }
            }
            System.out.println("Name change was successful!");
            System.out.println("Starting packet listener...");
            AtomicBoolean isRunning = new AtomicBoolean(true);
            final String usernamefinal = username;
            Thread listener = new Thread(() -> {
                while (isRunning.get()) {
                    try {
                        if (in.available() > 0) {
                            String data = reader.readLine();
                            String[] datasplit = data.split("@");
                            switch (datasplit[0]) {
                                case "MSG":
                                    System.out.println("\n" + unescapeString(datasplit[1]));
                                    System.out.print(usernamefinal + ": ");
                                    break;
                                case "KKD":
                                    showKickDialog(unescapeString(datasplit[1]));
                                    break;
                                case "BND":
                                    showBanDialog(unescapeString(datasplit[1]));
                                    break;
                                case "MSGB":
                                    String[] parts = splitParts(datasplit[1]);
                                    for (String line : parts) {
                                        System.out.println(line);
                                    }
                                    System.out.println(usernamefinal + ": ");
                                    break;
                            }

                        }
                    } catch (Exception e) {
                        System.out.println("Something went wrong while receiving a message!");
                        e.printStackTrace();
                    }

                }
                System.out.println("Listener was shut down.");
            });
            listener.start();
            System.out.println("Started listening for messages!");
            writer.println("JN@");
            writer.flush();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    connection.close();
                } catch (IOException ignored) {

                }
            }));
            System.out.println("Joined server!");
            input = new Scanner(System.in);
            while (true) {
                if (input.hasNextLine()) {
                    writer.println("SND@" + input.nextLine());
                    writer.flush();
                    System.out.print(usernamefinal + ": ");
                }
                if (connection.isClosed()) {
                    break;
                }
            }
            isRunning.set(false);
            for (int i = 0; i < 1000; i++) {
                System.out.println("\n");
            }
            System.err.println("The connection was closed by the remote host.");
            int input = JOptionPane.showOptionDialog(null, "The connection was closed by the remote host.", "Disconnected", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Reconnect", "Back to login", "Close"}, 2);
            if (input == 0) {
                System.out.println("Reconnecting.");
                connect(serverip, username);
            }else if (input == 1) {
                System.out.println("Returning to login.");
                run();
            }else if (input == 2) {
                System.out.println("Shutting down...");
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Something went wrong in the system. Please try again later.");
            e.printStackTrace();
        }
    }


    public static void showKickDialog(String message) {
        Thread dialog = new Thread(() -> {
           String kickusername = JOptionPane.showInputDialog(message);
           String reason = JOptionPane.showInputDialog("Enter reason: ");
           PrintWriter writer = new PrintWriter(out, true);
           writer.println("KK@" + kickusername + "&" + reason);
           writer.flush();
        });
        dialog.start();
    }

    public static void showBanDialog(String message) {
        Thread dialog = new Thread(() -> {
            String banusername = JOptionPane.showInputDialog(message);
            String reason = JOptionPane.showInputDialog("Enter reason: ");
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("BN@" + banusername + "&" + reason);
            writer.flush();
        });
        dialog.start();
    }

    public static String unescapeString(String str) {
        return str.replaceAll("\\\\\\\\", "\\").replaceAll("\\\\@", "@");
    }
    public static String escapeString(String str) {
        return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("@", "\\\\@");
    }
}