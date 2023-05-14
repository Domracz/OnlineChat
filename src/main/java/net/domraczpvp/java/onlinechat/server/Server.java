package net.domraczpvp.java.onlinechat.server;


import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static ServerSocket socket;
    static final List<Client> clients = new ArrayList<>();
    public static void main(String[] args) {
        try {
            socket = new ServerSocket(49254, 0, InetAddress.getLocalHost());
            Thread connectionListener = new Thread(() -> {
                try {
                    while (true) {
                        // Wait for a new connection
                        Socket client = socket.accept();

                        // Add the new client to the list
                        synchronized (clients) {
                            clients.add(new Client(client));
                        }

                        // Log the new connection
                        System.out.println("New Connection: " + client.getInetAddress().getHostAddress());
                    }
                } catch (Exception e) {
                    System.err.println("Something went wrong while accepting a connection. Here's a rundown of the error:");
                    e.printStackTrace();
                }
            });
            connectionListener.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Something went wrong while shutting down the server. Here's a rundown of the error:");
                        e.printStackTrace();
                    }
                }
            }));
            System.out.println("Started listening on \\" + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

        }catch (Exception e) {
            System.err.println("Something went wrong while creating ConsoleChat server. Here's a rundown of the error:");
            e.printStackTrace();
        }

    }

    public static void startConnection() {
        Thread handleconnections = new Thread(() -> {
            while (true) {
                for (int i = 0; i < clients.size(); i++) {
                    Client client = clients.get(i);
                    try {
                        InputStream in = client.getConnection().getInputStream();
                        OutputStream out = client.getConnection().getOutputStream();
                        if (in.available() > 0) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getConnection().getInputStream()));
                            String data = reader.readLine();
                            if (data != null) {
                                String[] parseddata = data.split("(\\\\\\\\.|[^@\\\\\\\\])*@");
                                parseddata[1] = unescapeString(parseddata[1]);
                                //Start the input command list.
                                switch (parseddata[0]) {
                                    case "SND":
                                        //Send to everyone.
                                        if (clients.get(i).isJoined()) {
                                            if (parseddata[1].startsWith("/")) {
                                                String[] args = CommandParser.getArgs(parseddata[1]);
                                                String command = CommandParser.getCommand(parseddata[1]);
                                                switch (command) {
                                                    case "reqadmin":
                                                        sendPrivateMessage("You have requested admin from the server. You will be given admin once the server accepts.", i);
                                                        requestAdmin(i);
                                                    case "msg":
                                                        if (args.length == 2) {
                                                            if (getClientsByName(args[1]).size() > 0) {
                                                                Client reciver = getClientsByName(args[1]).get(0);
                                                                sendPrivateMessage(clients.get(i).getUserName() + " -> " + "me", reciver);
                                                                sendPrivateMessage("me" + " -> " + reciver.getUserName(), i);
                                                            }else{
                                                                sendPrivateMessage(args[0] + " is not online!", i);
                                                            }
                                                        }else {
                                                            sendPrivateMessage("Command Usage: /msg <user> <message>", i);
                                                        }
                                                    case "kick":
                                                        sendPrivateMessage("Please use kick dialog", i);
                                                        showKickDialog("Enter the user you want to kick: ", i);
                                                    case "ban":
                                                        sendPrivateMessage("Please use ban dialog", i);
                                                        showBanDialog("Enter the user you want to ban: ", i);
                                                    case "getip":
                                                        if (args.length == 1) {
                                                            if (getClientsByName(args[0]).size() > 0) {
                                                                Client getip = getClientsByName(args[0]).get(0);
                                                                sendPrivateMessage("Ip address of " + getip.getUserName() + "is " + getip.getConnection().getInetAddress().getHostAddress(), i);
                                                            }else{
                                                                sendPrivateMessage(args[0] + " is not online!", i);
                                                            }
                                                        }else{
                                                            sendPrivateMessage("Command Usage: /getip <username>", i);
                                                        }
                                                }
                                            }
                                            sendMessage(parseddata[1]);
                                        }else{
                                            System.err.println("A user tried to speak in the server without joining!");
                                        }
                                        break;
                                    case "CHUN":
                                        if (getClientsByName(parseddata[1]).size() == 0) {
                                            if (parseddata[1].contains("@") | parseddata[1].contains("\\") | parseddata[1].contains("$") | parseddata[1].contains(")") | parseddata[1].contains("(") | parseddata[1].contains("*") | parseddata[1].contains("%") | parseddata[1].contains("!") | parseddata[1].contains("#") | parseddata[1].contains("$")) {
                                                sendNameChangeRedo(i, "Username cannot contain special characters.");
                                            }else {
                                                client.setName(parseddata[1]);
                                                sendNameChangeSuccess(i);
                                            }
                                        }else{
                                            sendNameChangeRedo(i, "That username is already in the server!");
                                        }
                                        break;
                                    case "JN":
                                        if (getClientsByName(parseddata[1]).size() == 0) {
                                            if (parseddata[1].contains("@") | parseddata[1].contains("\\") | parseddata[1].contains("$") | parseddata[1].contains(")") | parseddata[1].contains("(") | parseddata[1].contains("*") | parseddata[1].contains("%") | parseddata[1].contains("!") | parseddata[1].contains("#") | parseddata[1].contains("$")) {
                                                sendPrivateMessage("You tried to join with a blocked username! Please remove your modification.", i);
                                                client.getConnection().close();
                                            }else {
                                                client.setJoined(true);
                                            }
                                        }else{
                                            sendPrivateMessage("You tried to join with a username already in the server!", i);
                                            client.getConnection().close();
                                        }
                                        break;
                                    case "KK":
                                        if (getClientsByName(parseddata[1]).size() > 0) {
                                            String[] args = data.split("(\\\\\\\\.|[^&\\\\\\\\])*&");
                                            if (args.length == 2) {
                                                Client target = getClientsByName(args[0]).get(0);
                                                sendPrivateMessage(args[1], target);
                                                target.getConnection().close();
                                            }
                                        }
                                    case "BN":
                                        if (getClientsByName(parseddata[1]).size() > 0) {
                                            String[] args = data.split("(\\\\\\\\.|[^&\\\\\\\\])*&");
                                            if (args.length == 2) {
                                                Client target = getClientsByName(args[0]).get(0);
                                                sendPrivateMessage("You have been banned from this server!", target);
                                                sendPrivateMessage(args[1], target);
                                                BanManager.banUser(target.getConnection().getInetAddress().getHostAddress(), args[1]);
                                                target.getConnection().close();
                                            }
                                        }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Something went wrong while reading user packets. Here's a rundown of the error:");
                        e.printStackTrace();
                    }
                }
            }
        });
        handleconnections.start();
        System.out.println("Started listening for communication.");
    }

    public static void requestAdmin(int clientid) {
        Thread adminreq = new Thread(() -> {
           int result = JOptionPane.showOptionDialog(null, (clients.get(clientid).getUserName() + "wants to get admin."), "Admin Prompt", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Give Admin", "Kick User", "Cancel"}, 2);
           if (result == 0) {
               clients.get(clientid).setAdmin(true);
           }else if (result == 1){
               String reason = JOptionPane.showInputDialog("Please enter kick reason:");
               sendPrivateMessage(reason, clientid);
               try {
                   clients.get(clientid).getConnection().close();
               }catch (Exception ignored) {

               }
           }
        });
        adminreq.start();
    }
    public static void sendMessage(String message) {
        for (Client client : clients) {
            try {
                PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                writer.println("MSG@" + escapeString(message));
            }catch (Exception e) {
                System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
            }
        }
    }
    public static void sendPrivateMessage(String message, int clientid) {
        try {
            PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
            writer.println("MSG@" + escapeString(message));
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void sendPrivateMessage(String message, Client client) {
        try {
            PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
            writer.println("MSG@" + escapeString(message));
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void showKickDialog(String message, int clientid) {
        try {
            PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
            writer.println("KKD@" + escapeString(message));
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static void showBanDialog(String message, int clientid) {
        try {
            PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
            writer.println("BND@" + escapeString(message));
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static void sendNameChangeSuccess(int clientid) {
        try {
            PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
            writer.println("UNSC@");
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static List<Client> getClientsByName(String username) {
        List<Client> returnlist = new ArrayList<>();
        for (Client client : clients) {
            if (client.getUserName().toLowerCase().matches(username.toLowerCase())) {
                returnlist.add(client);
            }
        }
        return returnlist;
    }

    public static void sendNameChangeRedo(int clientid, String redomessage) {
        try {
            PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
            writer.println("UNRE@" + escapeString(redomessage));
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static String escapeString(String str) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '@') {
                escaped.append("\\@");
            } else if (c == '&') {
                escaped.append("\\&");
            } else if (c == '\\') {
                escaped.append("\\\\");
            } else {
                escaped.append(c);
            }
        }
        return escaped.toString();
    }

    public static String unescapeString(String str) {
        StringBuilder unescaped = new StringBuilder();
        boolean isEscaped = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isEscaped) {
                if (c == '@') {
                    unescaped.append('@');
                }else if (c == '&') {
                    unescaped.append('&');
                } else if (c == '\\') {
                    unescaped.append('\\');
                } else {
                    // This should never happen if the string was correctly escaped
                    throw new IllegalArgumentException("Invalid escape sequence: \\" + c);
                }
                isEscaped = false;
            } else if (c == '\\') {
                isEscaped = true;
            } else {
                unescaped.append(c);
            }
        }
        if (isEscaped) {
            // This should never happen if the string was correctly escaped
            throw new IllegalArgumentException("Invalid escape sequence: \\");
        }
        return unescaped.toString().replace("\\\\", "\\");
    }
}
