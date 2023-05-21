package net.domraczpvp.java.onlinechat.server;

import java.util.ArrayList;
import java.util.List;

public abstract class Mod {

    public abstract Mod getInstance();

    public abstract String getId();

    public abstract String getName();

    public abstract List<ModCommand> getCommands();


    public abstract boolean sendMessage(Client client, String message);

    public abstract boolean commandExecuted(Client client, String command, String[] args);

    public abstract void onLeave(Client client);

    public abstract void onJoin(Client client);

    public abstract void onConnect(Client client);

    public abstract UsernameResponse onUsername(Client client, String username);

    public abstract boolean onPacket(Client client, String packet, String data);

    public abstract void onLoad();
}
