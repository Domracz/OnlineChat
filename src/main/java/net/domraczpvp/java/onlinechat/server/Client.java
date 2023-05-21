package net.domraczpvp.java.onlinechat.server;

import java.net.Socket;
import java.util.List;

public class Client {
    private final Socket clientSocket;
    private String name;

    private boolean isJoined;

    private boolean isAdmin;

    private boolean isMuted;

    private List<String> permissions;

    public Client(Socket client, String username) {
        clientSocket = client;
        name = username;
    }
    public Client(Socket client) {
        clientSocket = client;
        name = client.getRemoteSocketAddress().toString();
    }

    public String getUserName() {
        return name;
    }

    public Socket getConnection() {
        return clientSocket;
    }

    public void setName(String username) {
        name = username;
    }

    public boolean isJoined(){
        return isJoined;
    }

    public void setJoined(boolean value) {
        isJoined = value;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean value) {
        isAdmin = value;
    }

    public void setMuted(boolean value) {
        isMuted = value;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void addPermissionNode(String node) {
        permissions.add(node);
    }

    public void removePermissionNode(String node) {
        permissions.remove(node);
    }

    public boolean hasNode(String node) {
        return permissions.contains(node);
    }
}
