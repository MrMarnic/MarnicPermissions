package me.marnic.permissions.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Copyright (c) 02.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class CommandHandler {
    private ArrayList<MCommand> commands;

    public CommandHandler() {
        this.commands = new ArrayList<>();
    }

    public void onCommand(CommandSender sender,String name,String... args) {
        if(!commands.isEmpty()) {
            if (sender instanceof Player) {
                for (MCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(name)) {
                        if (cmd.argsEquals(args)) {
                            if(sender.hasPermission(cmd.getPermission())) {
                                cmd.onExecute((Player) sender, args);
                                return;
                            }else{
                                 onCmdPermissionError((Player)sender,name);
                            }
                        }
                    }
                }
                onCmdDoesNotExist((Player)sender,name,fromArgs(args));
            } else {
                for (MCommand cmd : commands) {
                    if (cmd.getName().equalsIgnoreCase(name)) {
                        if (cmd.argsEquals(args)) {
                            cmd.onExecute(sender,args);
                            return;
                        }
                    }
                }
                onCmdDoesNotExist(sender,name,fromArgs(args));
            }
        }
    }

    private String fromArgs(String[] args) {

        if(args.length==0)
            return "";

        String a = "";

        for(String s:args) {
            a=a+" "+s;
        }

        return a.substring(1);
    }

    public void onCmdDoesNotExist(Player player,String name,String args) {

    }

    public void onCmdDoesNotExist(CommandSender sender,String name,String args) {

    }

    public void onCmdPermissionError(Player player,String name) {

    }

    public void addCmd(MCommand cmd) {
        commands.add(cmd);
    }

    public void removeCmd(MCommand cmd) {
        commands.remove(cmd);
    }

    public void removeCmdWith(String name,String... args) {
        for (MCommand cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(name)) {
                if (cmd.argsEquals(args)) {
                    commands.remove(cmd);
                }
            }
        }
    }

    public ArrayList<MCommand> getCommands() {
        return commands;
    }
}
