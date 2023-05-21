package net.domraczpvp.java.onlinechat.server;

public class ModCommand {
    public String name;
    public String[][] args;
    public boolean permissionrequired;
    public String permission;

    public String description;
    public ModCommand(String cmdname, String[][] cmdargs, String cmdpermission, String desc) {
        name = cmdname;
        args = cmdargs;
        permission = cmdpermission;
        permissionrequired = true;
        description = desc;
    }

    public ModCommand(String cmdname, String[][] cmdargs, String desc) {
        name = cmdname;
        args = cmdargs;
        permissionrequired = false;
        description = desc;
    }
}
