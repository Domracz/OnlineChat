package net.domraczpvp.java.onlinechat.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BanManager {

    public static void banUser(String ip, String reason) {

        try {
            FileWriter writer = new FileWriter("bans.txt", true);
            writer.write(ip + "@" + reason + "\n"); // add the line to the file
            writer.close(); // close the writer to release resources
        } catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
    }

    public static void unbanUser(String ip) {
        try {
            List<String> banlines = new ArrayList<>();
            FileReader reader = new FileReader("bans.txt");

            BufferedReader br = new BufferedReader(reader);
            String contentLine = br.readLine();
            while (contentLine != null) {
                banlines.add(contentLine);
                contentLine = br.readLine();
            }

            for (int i =0; i < banlines.size(); i++) {
                if (banlines.get(i).startsWith(ip)) {
                    banlines.remove(i);
                    break;
                }
            }

            reader.close();
        }catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
    }

    private static boolean isBanned() {
        try {
            List<String> banlines = new ArrayList<>();
            FileReader reader = new FileReader("bans.txt");

            BufferedReader br = new BufferedReader(reader);
            String contentLine = br.readLine();
            while (contentLine != null) {
                banlines.add(contentLine);
                contentLine = br.readLine();
            }

            for (int i =0; i < banlines.size(); i++) {
                if (banlines.get(i).startsWith(ip)) {
                    banlines.remove(i);
                    break;
                }
            }

            reader.close();
        }catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
    }
}
