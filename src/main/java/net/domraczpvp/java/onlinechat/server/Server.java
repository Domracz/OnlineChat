package net.domraczpvp.java.onlinechat.server;


import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {
    static ServerSocket socket;
    static final List<Client> clients = new ArrayList<>();
    public static void main(String[] args) {
        try {
            socket = new ServerSocket(1234);
            Thread connectionListener = new Thread(() -> {
                try {
                    while (true) {
                        // Wait for a new connection
                        Socket client = socket.accept();

                        // Add the new client to the list
                        clients.add(new Client(client));

                        // Start listener
                        System.out.println("New Connection: " + client.getInetAddress().getHostAddress());
                        System.out.println("Starting the listener for client " + client.getInetAddress().getHostAddress());
                        Thread userthread = new Thread(() -> clientConnection(clients.size() - 1));
                        API.connect(clients.get(clients.size() - 1));
                        userthread.start();
                        System.out.println("Started listener.");
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
                        System.out.println("Server was shutdown.");
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Something went wrong while shutting down the server. Here's a rundown of the error:");
                        e.printStackTrace();
                    }
                }
            }));
            System.out.println("Started listening on \\" + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
            System.out.println("Loading mods...");
            API.registerMods();
            System.out.println("Mods loaded. Here's a list of all of them: ");
            for (Mod mod : API.mods) {
                System.out.println(mod.getId());
            }
            System.out.println("Server successfully loaded.");

        }catch (Exception e) {
            System.err.println("Something went wrong while creating ConsoleChat server. Here's a rundown of the error:");
            e.printStackTrace();
        }

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




    public static void clientConnection(int i) {
        Client client = clients.get(i);
        do {
            try {
                InputStream in = client.getConnection().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                if (in.available() > 0) {
                    String data = reader.readLine();
                    if (data != null) {
                        String[] parseddata = splitData(data);
                        System.out.println(Arrays.toString(parseddata));
                        if (parseddata.length > 1) {
                            parseddata[1] = unescapeString(parseddata[1]);
                        }
                        //Start the input command list.
                        System.out.println("New message: " + data);
                        switch (parseddata[0]) {
                            case "SND":
                                //Send to everyone.
                                if (clients.get(i).isJoined()) {
                                    if (parseddata[1].startsWith("/")) {
                                        String[] args = CommandParser.getArgs(parseddata[1]);
                                        String command = CommandParser.getCommand(parseddata[1]);
                                        switch (command) {
                                            case "reqadmin" -> {
                                                if (!client.isAdmin()) {
                                                    sendPrivateMessage("You have requested admin from the server. You will be given admin once the server accepts.", i);
                                                    requestAdmin(i);
                                                } else {
                                                    sendPrivateMessage("You are already admin!", client);
                                                }
                                            }
                                            case "msg" -> {
                                                if (args.length == 2) {
                                                    if (getClientsByName(args[1]).size() > 0) {
                                                        Client receiver = getClientsByName(args[1]).get(0);
                                                        sendPrivateMessage(clients.get(i).getUserName() + " -> " + "me", receiver);
                                                        sendPrivateMessage("me" + " -> " + receiver.getUserName(), i);
                                                    } else {
                                                        sendPrivateMessage(args[0] + " is not online!", i);
                                                    }
                                                } else {
                                                    sendPrivateMessage("Command Usage: /msg <user> <message>", i);
                                                }
                                            }
                                            case "kick" -> {
                                                if (client.isAdmin() | client.hasNode("mod.kick")) {
                                                    sendPrivateMessage("Please use kick dialog", i);
                                                    showKickDialog("Enter the user you want to kick: ", i);
                                                } else {
                                                    sendPrivateMessage("You are not allowed to do this!", client);
                                                }
                                            }
                                            case "ban" -> {
                                                if (client.isAdmin() | client.hasNode("mod.ban")) {
                                                    sendPrivateMessage("Please use ban dialog", i);
                                                    showBanDialog("Enter the user you want to ban: ", i);
                                                } else {
                                                    sendPrivateMessage("You are not allowed to do this!", client);
                                                }
                                            }
                                            case "getip" -> {
                                                System.out.println(Arrays.toString(args));
                                                if (client.isAdmin()) {
                                                    if (args.length == 1) {
                                                        if (getClientsByName(args[0]).size() > 0) {
                                                            Client getip = getClientsByName(args[0]).get(0);
                                                            sendPrivateMessage("Ip address of " + getip.getUserName() + " is " + getip.getConnection().getInetAddress().getHostAddress(), i);
                                                        } else {
                                                            sendPrivateMessage(args[0] + " is not online!", i);
                                                        }
                                                    } else {
                                                        sendPrivateMessage("Command Usage: /getip <username>", i);
                                                    }
                                                } else {
                                                    sendPrivateMessage("You are not allowed to do this!", client);
                                                }
                                            }
                                            case "list" -> {
                                                List<String> lines = new ArrayList<>();
                                                lines.add("List of all users:");
                                                lines.add("Users suffixed by an \"*\" are admins.");
                                                lines.add("---------------------------------------");
                                                for (Client iuser : clients) {
                                                    if (iuser.isAdmin()) {
                                                        lines.add(iuser.getUserName() + "*");
                                                    } else {
                                                        lines.add(iuser.getUserName());
                                                    }
                                                }
                                                lines.add("----------------------------------");
                                                sendPrivateMessageBurst(lines, client);
                                            }
                                            case "mute" -> {
                                                if (client.isAdmin() | client.hasNode("mod.mute")) {
                                                    showMuteDialog("Please enter the username of the user to mute.", i);
                                                } else {
                                                    sendPrivateMessage("You are not allowed to do this!", client);
                                                }
                                            }
                                            case "help" -> {
                                                List<String> lines = new ArrayList<>();
                                                if (args.length == 0) {
                                                    lines.add("Commands list - Default");
                                                    lines.add("/reqadmin - Request admin privileges from the server.");
                                                    lines.add("/msg <user> <message> - Sends a private message to a user.");
                                                    lines.add("/kick - Opens the kick dialog.");
                                                    lines.add("/ban - Opens the ban dialog.");
                                                    lines.add("/getip <user> - Gets a user's ip address.");
                                                    lines.add("/list - Shows all the users in the server.");
                                                    lines.add("/mute - Prevents user's from talking.");
                                                    lines.add("/help [mod] - Shows the help menu.");
                                                }else{
                                                    int findindex = -1;
                                                    for (int i2 = 0; i2 < API.mods.size(); i2++) {
                                                        if (API.mods.get(i2).getName().equalsIgnoreCase(args[0])) {
                                                            lines.add("Commands list - " + API.mods.get(i2).getName());
                                                            if (API.mods.get(i2).getCommands() != null) {
                                                                for (int i3 = 0; i3 < API.mods.get(i2).getCommands().size(); i3++) {
                                                                    if (API.mods.get(i2).getCommands().get(i3).args.length > 0) {
                                                                        StringBuilder builder = new StringBuilder();
                                                                        for (String[] arg : API.mods.get(i2).getCommands().get(i3).args) {
                                                                            if (Objects.equals(arg[1], "notreq")) {
                                                                                builder.append("[").append(arg[0]).append("]");
                                                                            } else {
                                                                                builder.append("<").append(arg[0]).append(">");
                                                                            }
                                                                        }
                                                                        if (API.mods.get(i2).getCommands().get(i3).args.length > 0) {
                                                                            lines.add(API.mods.get(i2).getCommands().get(i3).name + " " + builder + " - " + API.mods.get(i2).getCommands().get(i3).description);
                                                                        } else {
                                                                            lines.add(API.mods.get(i2).getCommands().get(i3).name + " - " + API.mods.get(i2).getCommands().get(i3).description);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            findindex = i2;
                                                            break;
                                                        }
                                                    }
                                                    if (findindex == -1) {
                                                        lines.add("Unknown mod: " + args[0]);
                                                    }
                                                }
                                                sendPrivateMessageBurst(lines, client);
                                            }
                                            default -> {
                                                if (!API.command(client, parseddata[0], args)) {
                                                    sendPrivateMessage("Unknown command. Use /help for a list of commands", client);
                                                }
                                            }
                                        }
                                    } else {
                                        if (API.message(client, parseddata[1])) {
                                            sendMessage(client.getUserName() + ": " + parseddata[1], i);
                                        }
                                    }

                                } else {
                                    System.err.println("A user tried to speak in the server without joining!");
                                }
                                break;
                            case "CHUN":
                                System.out.println(getClientsByName(parseddata[1]).size());
                                if (getClientsByName(parseddata[1]).size() == 0) {
                                    if (parseddata[1].contains("@") | parseddata[1].contains("\\") | parseddata[1].contains("$") | parseddata[1].contains(")") | parseddata[1].contains("(") | parseddata[1].contains("*") | parseddata[1].contains("%") | parseddata[1].contains("!") | parseddata[1].contains("#") | parseddata[1].contains("$")) {
                                        sendNameChangeRedo(i, "Username cannot contain special characters.");
                                    } else {
                                        if (parseddata[1].length() > 3 && parseddata[1].length() < 14) {
                                            System.out.println("done.");
                                            client.setName(parseddata[1]);
                                            sendNameChangeSuccess(i);
                                        } else {
                                            sendNameChangeRedo(i, "Username has to be at least 4 characters and at most 13 characters.");
                                        }
                                    }
                                } else {
                                    sendNameChangeRedo(i, "That username is already in the server!");
                                }
                                break;
                            case "JN":
                                if (getClientsByName(client.getUserName()).size() == 1) {
                                    if (client.getUserName().contains("@") | client.getUserName().contains("\\") | client.getUserName().contains("$") | client.getUserName().contains(")") | client.getUserName().contains("(") | client.getUserName().contains("*") | client.getUserName().contains("%") | client.getUserName().contains("!") | client.getUserName().contains("#") | client.getUserName().contains("$")) {
                                        sendPrivateMessage("You tried to join with a blocked username! Please remove your modification.", i);
                                        setDisconnectReason(client, "You tried to join with a blocked username! Please remove your modification.");
                                        closeConnection(client);
                                    } else {
                                        if (BanManager.isBanned(client.getConnection().getInetAddress().getHostAddress())) {
                                            System.out.println("Banned");
                                            sendPrivateMessage("You have been banned from this server!", i);
                                            sendPrivateMessage(BanManager.getReason(client.getConnection().getInetAddress().getHostAddress()), i);
                                            setDisconnectReason(client, "You have been banned for: " + BanManager.getReason(client.getConnection().getInetAddress().getHostAddress()));
                                            closeConnection(client);

                                        } else {
                                            if (client.getUserName().length() > 3 && client.getUserName().length() < 14) {
                                                UsernameResponse api = API.username(client, client.getUserName());
                                                if (api.redo) {
                                                    sendPrivateMessage(api.redomessage, client);
                                                    setDisconnectReason(client, api.redomessage);
                                                    closeConnection(client);
                                                }else{
                                                    client.setJoined(true);
                                                    API.join(client);
                                                    sendMessage(client.getUserName() + " joined!");
                                                }
                                            } else {
                                                sendPrivateMessage("You tried to join with an invalid username!", client);
                                                setDisconnectReason(client, "You tried to join with an invalid username!");
                                                closeConnection(client);
                                            }
                                        }
                                    }
                                } else {
                                    sendPrivateMessage("You tried to join with a username already in the server!", i);
                                    setDisconnectReason(client, "You tried to join with a username already in the server!");
                                    closeConnection(client);
                                }
                                break;
                            case "KK":
                                if (client.isAdmin() | client.hasNode("mod.kick")) {
                                    String[] args = splitParts(parseddata[1]);
                                    if (args.length == 2) {
                                        if (getClientsByName(args[0]).size() > 0) {
                                            Client target = getClientsByName(args[0]).get(0);
                                            sendPrivateMessage(args[1], target);
                                            setDisconnectReason(target, args[1]);
                                            closeConnection(target);
                                            System.out.println(target.getUserName() + " was kicked.");
                                            sendPrivateMessage("Kicked " + target.getUserName() + " for " + args[1], client);
                                        } else {
                                            sendPrivateMessage("That user is offline!", client);
                                        }
                                    }
                                } else {
                                    sendPrivateMessage("You are not allowed to do this!", client);
                                }
                            case "BN":
                                if (client.isAdmin() | client.hasNode("mod.ban")) {
                                    String[] args = splitParts(parseddata[1]);
                                    if (args.length == 2) {
                                        if (getClientsByName(args[0]).size() > 0) {
                                            Client target = getClientsByName(args[0]).get(0);
                                            sendPrivateMessage("You have been banned from this server!", target);
                                            sendPrivateMessage(args[1], target);
                                            BanManager.banUser(target.getConnection().getInetAddress().getHostAddress(), args[1]);
                                            setDisconnectReason(target, args[1]);
                                            closeConnection(target);
                                            sendPrivateMessage("Banned " + target.getUserName() + " for " + args[1], client);
                                        } else {
                                            sendPrivateMessage("That user is offline!", client);
                                        }
                                    }
                                } else {
                                    sendPrivateMessage("You are not allowed to do this!", client);
                                }
                                break;
                            case "MT":
                                if (client.isAdmin() | client.hasNode("mod.mute")) {
                                    String[] args = splitParts(parseddata[1]);
                                    if (args.length == 2) {
                                        if (getClientsByName(args[0]).size() > 0) {
                                            Client target = getClientsByName(args[0]).get(0);
                                            sendPrivateMessage("You have been muted.", target);
                                            sendPrivateMessage(args[1], target);
                                            target.setMuted(true);
                                            closeConnection(target);
                                            sendPrivateMessage("Muted " + target.getUserName() + " for " + args[1], client);
                                        } else {
                                            sendPrivateMessage("That user is offline!", client);
                                        }
                                    }
                                } else {
                                    sendPrivateMessage("You are not allowed to do this!", client);
                                }
                                break;
                            case "exit":
                                client.setRunning(false);
                            default:
                                if (!API.packet(client, parseddata[0], parseddata[1])) {
                                    System.out.println("Invalid packet detected.");
                                }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        } while (client.getRunning());
        System.out.println("Client left: " + client.getUserName());
        API.leave(client);
        sendMessage(client.getUserName() + " left!");
        clients.remove(client);
    }


    public static void requestAdmin(int clientid) {
        Thread adminreq = new Thread(() -> {
           int result = JOptionPane.showOptionDialog(null, (clients.get(clientid).getUserName() + " wants to get admin."), "Admin Prompt", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Give Admin", "Kick User", "Cancel"}, 2);
           if (result == 0) {
               clients.get(clientid).setAdmin(true);
               sendPrivateMessage("You are now a server operator.", clientid);
           }else if (result == 1){
               String reason = JOptionPane.showInputDialog("Please enter kick reason:");
               sendPrivateMessage(reason, clientid);
               try {
                   setDisconnectReason(clientid, reason);
                   closeConnection(clients.get(clientid));
               }catch (Exception ignored) {

               }
           }
        });
        adminreq.start();
    }

    public static void setDisconnectReason(int clientid, String reason) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream());
                writer.println("STKR@" + escapeString(reason));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet.");
        }
    }

    public static void setDisconnectReason(Client client, String reason) {
        try {
            if (!client.getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream());
                writer.println("STKR@" + escapeString(reason));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet.");
        }
    }
    public static void sendMessage(String message) {
        for (Client client : clients) {
            try {
                if (!client.getConnection().isClosed()) {
                    PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                    writer.println("MSG@" + escapeString(message));
                    writer.flush();
                }
            }catch (Exception e) {
                System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
                e.printStackTrace();
            }
        }
    }

    public static void sendMessageBurst(List<String> messages) {
        for (Client client : clients) {
            try {
                if (!client.getConnection().isClosed()) {
                    PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                    StringBuilder builder = new StringBuilder();
                    for (String line : messages) {
                        builder.append(escapeString(line)).append("&");
                    }
                    writer.println("MSGB@" + builder);
                    writer.flush();
                }
            }catch (Exception e) {
                System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
            }
        }
    }

    public static void sendPrivateMessageBurst(List<String> messages, int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                StringBuilder builder = new StringBuilder();
                for (String line : messages) {
                    builder.append(escapeString(line)).append("&");
                }
                writer.println("MSGB@" + builder);
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void sendPrivateMessageBurst(List<String> messages, Client client) {
        try {
            if (!client.getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                StringBuilder builder = new StringBuilder();
                for (String line : messages) {
                    builder.append(escapeString(line)).append("&");
                }
                writer.println("MSGB@" + builder);
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void sendMessage(String message, int excluded) {
        for (Client client : clients) {
            try {
                if (!client.getConnection().isClosed()) {
                    if (!clients.get(excluded).equals(client)) {
                        PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                        writer.println("MSG@" + escapeString(message));
                        writer.flush();
                    }
                }
            }catch (Exception e) {
                System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
            }
        }
    }
    public static void sendPrivateMessage(String message, int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("MSG@" + escapeString(message));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void sendPrivateMessage(String message, Client client) {
        try {
            if (!client.getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
                writer.println("MSG@" + escapeString(message));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a message. Here's a rundown of the error:");
        }
    }

    public static void showKickDialog(String message, int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("KKD@" + escapeString(message));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static void showBanDialog(String message, int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("BND@" + escapeString(message));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static void showMuteDialog(String message, int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("MED@" + escapeString(message));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }

    public static void sendNameChangeSuccess(int clientid) {
        try {
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("UNSC@");
                writer.flush();
            }
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
            if (!clients.get(clientid).getConnection().isClosed()) {
                PrintWriter writer = new PrintWriter(clients.get(clientid).getConnection().getOutputStream(), true);
                writer.println("UNRE@" + escapeString(redomessage));
                writer.flush();
            }
        }catch (Exception e) {
            System.err.println("Something went wrong while sending a packet. Here's a rundown of the error:");
        }
    }


    public static String unescapeString(String str) {
        return str.replaceAll("\\\\\\\\", "\\").replaceAll("\\\\@", "@");
    }
    public static String escapeString(String str) {
        return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("@", "\\\\@");
    }

    public static void closeConnection(Client client) throws IOException{
        PrintWriter writer = new PrintWriter(client.getConnection().getOutputStream(), true);
        writer.println("exit");
        writer.close();
        client.getConnection().close();
    }
}
