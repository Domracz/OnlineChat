package net.domraczpvp.java.onlinechat;

import net.domraczpvp.java.onlinechat.client.ClientMain;
import net.domraczpvp.java.onlinechat.server.Server;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println(Server.escapeString("b@domracz"));
        System.out.println(Arrays.toString(Server.splitData("TST@b\\@domracz")));

        int choice = JOptionPane.showOptionDialog(null, "Start Server or Join Server", "Server", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Start Server", "Join Server"}, 0);
        if (choice == 0) {
            Server.main(args);
        }else{
            ClientMain.main(args);
        }
    }


}
