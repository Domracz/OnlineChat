package net.domraczpvp.java.onlinechat.server;

import java.net.Socket;

public class Client {
    private final Socket clientSocket;
    private String name;

    private boolean isJoined;

    private boolean isAdmin;

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
}
