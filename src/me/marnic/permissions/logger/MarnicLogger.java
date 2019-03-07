package me.marnic.permissions.logger;

import me.marnic.permissions.handler.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * Copyright (c) 02.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class MarnicLogger {
    public static void info(String msg) {
        Bukkit.getLogger().log(Level.INFO,"[MarnicPermissions] " + msg);
    }

    public static void warn(String msg) {
        Bukkit.getLogger().log(Level.WARNING,"[MarnicPermissions] " + msg);
    }

    public static void log(Message msg) {
        if(msg.isError()) {
            warn(msg.getMsg());
        }else{
            info(msg.getMsg());
        }
    }

    public static void info(String msg, Player player) {
        player.sendMessage("[MarnicPermissions/INFO] " + msg);
    }

    public static void warn(String msg,Player player) {
        player.sendMessage(ChatColor.RED+"[MarnicPermissions/WARN] " + msg);
    }

    public static void log(Message msg,Player player) {
        if(msg.isError()) {
            warn(msg.getMsg(),player);
        }else{
            info(msg.getMsg(),player);
        }
    }
}
