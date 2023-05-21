package net.domraczpvp.java.onlinechat.server.mods;

import net.domraczpvp.java.onlinechat.server.*;

import java.util.ArrayList;
import java.util.List;

public class AntiSwear extends Mod {

    private List<String> blockedwords;

    @Override
    public Mod getInstance() {
        return this;
    }

    @Override
    public String getId() {
        return "antiswear-v0.0.1-stable";
    }

    @Override
    public String getName() {
        return "AntiSwear";
    }

    @Override
    public List<ModCommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public boolean sendMessage(Client client, String message) {
        String[] words = message.split(" ");
        for (String word : words) {
            if (blockedwords.contains(word)) {
                Server.sendMessage("You may not use the word: " + word + " in your messages or username!");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean commandExecuted(Client client, String command, String[] args) {
        return false;
    }

    @Override
    public void onLeave(Client client) {

    }

    @Override
    public void onJoin(Client client) {

    }

    @Override
    public void onConnect(Client client) {

    }

    @Override
    public UsernameResponse onUsername(Client client, String username) {
        String[] words = username.split(" ");
        for (String word : words) {
            if (blockedwords.contains(word)) {
                return new UsernameResponse(true, "The word " + word + " is blocked!");
            }
        }
        return new UsernameResponse(false, null);
    }

    @Override
    public boolean onPacket(Client client, String packet, String data) {
        return false;
    }

    @Override
    public void onLoad() {
        blockedwords.add("fuck");
        blockedwords.add("bitch");
        blockedwords.add("shit");
    }
}
