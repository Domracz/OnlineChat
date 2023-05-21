package net.domraczpvp.java.onlinechat.server;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BanManager {

    public static void banUser(String ip, String reason) {

        try {
            File file = new File("bans.txt");
            if (!file.exists()) {
                Boolean result = file.createNewFile();
                if (result) {
                    System.out.println("Success.");
                }else{
                    System.out.println("Failure");
                }
            }
            FileWriter writer = new FileWriter(file, true);
            writer.write(ip + "@" + reason + "\n"); // add the line to the file
            writer.close(); // close the writer to release resources
        } catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
    }

    public static void unbanUser(String ip) {
        try {
            File file = new File("bans.txt");
            if (!file.exists()) {
                Boolean result = file.createNewFile();
                if (result) {
                    System.out.println("Success.");
                }else{
                    System.out.println("Failure");
                }
            }
            List<String> banlines = new ArrayList<>();
            FileReader reader = new FileReader(file);

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

    public static boolean isBanned(String ip) {
        try {
            File file = new File("bans.txt");
            if (!file.exists()) {
                Boolean result = file.createNewFile();
                if (result) {
                    System.out.println("Success.");
                }else{
                    System.out.println("Failure");
                }
            }
            List<String> banlines = new ArrayList<>();
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);
            String contentLine = br.readLine();
            while (contentLine != null) {
                banlines.add(contentLine);
                contentLine = br.readLine();
            }

            for (String banline : banlines) {
                if (banline.startsWith(ip)) {
                    return true;
                }
            }

            reader.close();
        }catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
        return false;
    }

    public static String getReason(String ip) {
        try {
            File file = new File("bans.txt");
            if (!file.exists()) {
                Boolean result = file.createNewFile();
                if (result) {
                    System.out.println("Success.");
                }else{
                    System.out.println("Failure");
                }
            }
            List<String> banlines = new ArrayList<>();
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);
            String contentLine = br.readLine();
            while (contentLine != null) {
                banlines.add(contentLine);
                contentLine = br.readLine();
            }

            for (String banline : banlines) {
                if (banline.startsWith(ip)) {
                    String[] banSplit = banline.split("@");
                    String[] banReasons = new String[banSplit.length - 1];
                    System.arraycopy(banSplit, 0, banReasons, 0, banSplit.length - 1);
                    String banReason = String.join("@", banReasons);
                    return banReason;
                }
            }

            reader.close();
        }catch (IOException e) {
            System.out.println("An error occurred while adding the line to the file: " + e.getMessage());
        }
        return null;
    }
}
