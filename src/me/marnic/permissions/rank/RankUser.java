package me.marnic.permissions.rank;

import me.marnic.permissions.main.MarnicPermissions;
import me.marnic.permissions.yml.ILoadable;
import me.marnic.permissions.yml.IWriteable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class RankUser implements ILoadable, IWriteable {
    private ArrayList<String> ranks;
    private String realName;
    private UUID uuid;
    private PermissionAttachment attachment;

    public RankUser(UUID uuid,String realName) {
        this.uuid = uuid;
        this.ranks = new ArrayList<>();
        this.realName = realName;
    }

    public void addToRealUser(Plugin plugin) {
        attachment = Bukkit.getPlayer(uuid).addAttachment(plugin);

        Rank rank = null;

        for(String r:ranks) {
            rank = MarnicPermissions.permissionHandler.getByName(r);

            for(String p:rank.getPermissions()) {
                attachment.setPermission(p,true);
            }
        }
    }

    public void addPermission(String permission,Plugin plugin) {
        attachment.setPermission(permission,true);
    }

    public void removePermission(String permission,Plugin plugin) {
        attachment.unsetPermission(permission);
    }

    public void removeAllPermissions() {
        for(String perm:attachment.getPermissions().keySet()) {
            attachment.unsetPermission(perm);
        }
    }

    public void removeAttachment() {
        Bukkit.getPlayer(uuid).removeAttachment(attachment);
    }

    public RankUser(FileConfiguration config,String path,JavaPlugin plugin) {
        load(config,path,plugin);
    }

    public UUID getUuid() {
        return uuid;
    }

    public ArrayList<String> getRanks() {
        return ranks;
    }

    public String getRealName() {
        return realName;
    }

    @Override
    public void load(FileConfiguration config, String path, JavaPlugin plugin) {
        this.uuid = UUID.fromString(config.getConfigurationSection(path).getName());
        this.realName = config.getString(path+".realName");
        this.ranks = (ArrayList<String>) config.getList(path+".ranks");

        if(ranks==null) {
            ranks = new ArrayList<>();
        }
    }

    @Override
    public void write(FileConfiguration configuration, String path, JavaPlugin plugin) {
        String p = path+"."+uuid.toString();
        configuration.createSection(p);
        configuration.set(p+".realName",realName);
        configuration.set(p+".ranks",ranks);

        plugin.saveConfig();
    }
}
