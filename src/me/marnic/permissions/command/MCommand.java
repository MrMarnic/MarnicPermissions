package me.marnic.permissions.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 02.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class MCommand {
    private String name;
    private final String[] args;
    private String description;
    private final String permission;

    public MCommand(String name, String arg,String description,String permission) {
        this.name = name;
        if(arg.length()>0) {
            this.args = arg.split(" ");
        }else{
            this.args = new String[0];
        }
        this.description = description;
        this.permission = permission;
    }

    public void onExecute(Player player,String... args) {

    }

    public void onExecute(CommandSender sender,String... args) {

    }

    public boolean argsEquals(String[] arg) {
        if(args.length==arg.length) {
            for(int i = 0;i<arg.length;i++) {
                if(!args[i].equalsIgnoreCase("p")) {
                    if(!args[i].equalsIgnoreCase(arg[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }
}
