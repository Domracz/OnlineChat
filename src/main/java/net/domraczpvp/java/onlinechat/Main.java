package net.domraczpvp.java.onlinechat;

import net.domraczpvp.java.onlinechat.client.ClientMain;
import net.domraczpvp.java.onlinechat.server.Server;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int choice = JOptionPane.showOptionDialog(null, "Start Server or Join Server", "Server", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Start Server", "Join Server"}, 0);
        if (choice == 0) {
            Server.main(args);
        }else{
            ClientMain.main(args);
        }
    }


}
