package net.domraczpvp.java.onlinechat.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class ClientMain {
    static Scanner input;
    static Socket connection;
    public static void main(String[] args) {
        try {
            connection = new Socket("localhost", 1234);
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String escapeString(String str) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '@') {
                escaped.append("\\@");
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
        return unescaped.toString();
    }
}