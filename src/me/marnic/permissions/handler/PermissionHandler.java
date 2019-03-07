package me.marnic.permissions.handler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import me.marnic.permissions.logger.MarnicLogger;
import me.marnic.permissions.main.MarnicPermissions;
import me.marnic.permissions.rank.Rank;
import me.marnic.permissions.rank.RankUser;
import me.marnic.permissions.yml.ILoadable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Copyright (c) 02.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class PermissionHandler implements ILoadable, Listener {

    private HashMap<String,Rank> ranks;
    private HashMap<UUID,RankUser> users;
    private HashMap<UUID,RankUser> onlinePlayers;
    private JavaPlugin plugin;
    private String rankPath;
    private String usersPath;
    private boolean hasDefaultRank;


    public PermissionHandler() {
        ranks = new HashMap<>();
        users = new HashMap<>();
        onlinePlayers = new HashMap<>();
    }

    public void init(FileConfiguration configuration,String path,JavaPlugin plugin) {
        load(configuration,path,plugin);
        this.plugin = plugin;

        RankUser user = null;

        for(Player player:Bukkit.getOnlinePlayers()) {
            if(users.containsKey(player.getUniqueId())) {
                user = getUserByName(player.getUniqueId());
                user.addToRealUser(plugin);
                onlinePlayers.put(user.getUuid(),user);
            }else{
                if(hasDefaultRank) {
                    addRankToUser(new UUIDNameBundle(player),"default");
                }
            }
        }
    }

    public Message addRank(String name) {
        if(!ranks.containsKey(name)) {
            if(!name.equalsIgnoreCase("default")) {
                Rank rank = new Rank(name);
                rank.write(plugin.getConfig(), rankPath, plugin);
                ranks.put(name, rank);
                plugin.saveConfig();
            }else {
                Rank rank = new Rank(name);
                rank.write(plugin.getConfig(), rankPath, plugin);
                ranks.put(name, rank);
                plugin.saveConfig();
                hasDefaultRank = true;
                RankUser user = null;
                for(Player player:Bukkit.getOnlinePlayers()) {
                    if(users.containsKey(player.getUniqueId())) {
                        user = getUserByName(player.getUniqueId());
                        if(user.getRanks().isEmpty()) {
                            addRankToUser(new UUIDNameBundle(player),"default");
                        }
                    }else{
                        if(hasDefaultRank) {
                            addRankToUser(new UUIDNameBundle(player),"default");
                        }
                    }
                }
            }
            return new Message("Rank " + name + " was successfully created!",false);
        }
        return new Message("Rank " + name + " does already exist!");
    }

    public Message removeRank(String name) {
        if(ranks.containsKey(name)) {
            plugin.getConfig().set(rankPath+"."+name,null);
            plugin.saveConfig();

            for(RankUser user:onlinePlayers.values()) {
                if(user.getRanks().contains(name)) {
                    removeRankFromUser(new UUIDNameBundle(user),name);
                }
            }

            if(name.equalsIgnoreCase("default")) {
                hasDefaultRank = false;
            }

            ranks.remove(name);

            return new Message("Rank " + name + " was successfully removed!",false);
        }
        return new Message("Rank " + name + " does not exist!");
    }

    public Message setRankOfUser(UUIDNameBundle user, String rank) {
        if(ranks.containsKey(rank)) {
            RankUser user1 = null;
            if(!users.containsKey(user)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(user.getUniqueId());
                if(player!=null) {
                    user1 = new RankUser(player.getUniqueId(), player.getName());
                    if(user.isOnline()) {
                        user1.addToRealUser(plugin);
                    }
                    users.put(player.getUniqueId(),user1);
                }else{
                    return new Message("Player " + user.getName() + " does not exist!");
                }
            }else {
                user1 = getUserByName(user.getUniqueId());
            }

            user1.getRanks().clear();
            user1.getRanks().add(rank);

            user1.write(plugin.getConfig(),usersPath,plugin);
            return new Message("Rank " + rank + " is now the only rank of " + user1.getRealName(),false);
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    public Message addRankToUser(UUIDNameBundle user, String rank) {
        if(ranks.containsKey(rank)) {
            RankUser user1 = null;
            if(!users.containsKey(user.getUniqueId())) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(user.getUniqueId());
                if(player!=null) {
                    user1 = new RankUser(player.getUniqueId(), player.getName());
                    users.put(player.getUniqueId(),user1);
                    onlinePlayers.put(player.getUniqueId(),user1);
                }else{
                    return new Message("Player " + user.getName() + " does not exist!");
                }
            }else {
                user1 = getUserByName(user.getUniqueId());
            }

            user1.getRanks().add(rank);
            user1.write(plugin.getConfig(),usersPath,plugin);
            if(user.isOnline()) {
                user1.addToRealUser(plugin);
            }
            return new Message("Rank " + rank + " was added to " + user1.getRealName(),false);
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    public Message removeRankFromUser(UUIDNameBundle user, String rank) {
        if(ranks.containsKey(rank)) {
            if(users.containsKey(user.getUniqueId())) {
                Rank rank1 = getByName(rank);
                RankUser user1 = getUserByName(user.getUniqueId());

                user1.getRanks().remove(rank1.getName());
                if(user.isOnline()) {
                    user1.addToRealUser(plugin);
                }
                user1.write(plugin.getConfig(),usersPath,plugin);
                return new Message("Rank " + rank + " was successfully removed from user " + user.getName(),false);
            }
            return new Message("User " + user.getName() + " has no rank!");
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    public Message clearRanksFromUser(UUIDNameBundle user) {
        if(users.containsKey(user.getUniqueId())) {
            RankUser user1 = getUserByName(user.getUniqueId());
            user1.getRanks().clear();
            if(user.isOnline()) {
                user1.removeAllPermissions();
            }
            user1.write(plugin.getConfig(),usersPath,plugin);
            return new Message("All ranks were cleared from user " + user.getName(),false);
        }
        return new Message("User " + user.getName() + " has no rank!");
    }

    public Message addPermissionToRank(String rank, String permission) {
        if(ranks.containsKey(rank)) {
            Rank rank1 = getByName(rank);
            rank1.getPermissions().add(permission);
            rank1.write(plugin.getConfig(),rankPath,plugin);
            plugin.saveConfig();
            for(RankUser user:onlinePlayers.values()) {
                if(user.getRanks().contains(rank)) {
                    user.addPermission(permission,plugin);
                }
            }
            return new Message("Permission " + permission + " was added to rank " + rank,false);
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    public Message removePermissionFromRank(String rank, String permission) {
        if(ranks.containsKey(rank)) {
            Rank rank1 = getByName(rank);
            rank1.getPermissions().remove(permission);
            rank1.write(plugin.getConfig(),rankPath,plugin);
            plugin.saveConfig();
            for(RankUser user:onlinePlayers.values()) {
                if(user.getRanks().contains(rank)) {
                    user.removePermission(permission,plugin);
                }
            }
            return new Message("Permission " + permission + " was removed from rank " + rank,false);
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    public Message clearPermissions(String rank) {
        if(ranks.containsKey(rank)) {
            Rank rank1 = getByName(rank);
            rank1.getPermissions().clear();
            rank1.write(plugin.getConfig(),rankPath,plugin);
            plugin.saveConfig();
            for(RankUser user:onlinePlayers.values()) {
                if(user.getRanks().contains(rank)) {
                    user.removeAllPermissions();
                }
            }
            return new Message("All permissions were removed from rank " + rank,false);
        }
        return new Message("Rank " + rank + " does not exist!");
    }

    @Override
    public void load(FileConfiguration config, String path, JavaPlugin plugin) {

        if(config.getConfigurationSection(path)==null) {
            config.createSection(path);
            MarnicLogger.warn("Config path " + path + " not existing: Creating...");
        }
        if(config.getConfigurationSection(path+".ranks")==null) {
            config.createSection(path+".ranks");
            MarnicLogger.warn("Config path " + path+".ranks" + " not existing: Creating...");
        }
        if(config.getConfigurationSection(path+".users")==null) {
            config.createSection(path+".users");
            MarnicLogger.warn("Config path " + path+".users" + " not existing: Creating...");
        }

        plugin.saveConfig();

        rankPath = path + ".ranks";

        Set<String> ranks = config.getConfigurationSection(rankPath).getKeys(false);

        MarnicLogger.info("Loading ranks...");
        Rank rank = null;
        for(String r:ranks) {
            rank = new Rank(config,rankPath+"."+r,plugin);
            this.ranks.put(rank.getName(),rank);
            MarnicLogger.info("Loaded rank " + rank.getName());
        }
        MarnicLogger.info("Ranks loaded");

        usersPath = path + ".users";

        RankUser user = null;

        Set<String> users = config.getConfigurationSection(usersPath).getKeys(false);

        for(String r:users) {
            user = new RankUser(config,usersPath+"."+r,plugin);
            this.users.put(user.getUuid(),user);
        }

        if(config.getConfigurationSection(rankPath+".default")!=null) {
            hasDefaultRank = true;
        }
    }

    public Rank getByName(String name) {
        return ranks.get(name);
    }

    public RankUser getUserByName(UUID name) {
        return users.get(name);
    }

    public Message listPermissions(UUIDNameBundle player) {
        for(PermissionAttachmentInfo s :Bukkit.getPlayer(player.getUniqueId()).getEffectivePermissions()) {
            MarnicLogger.info(s.getPermission());
        }
        return new Message("Permissions were listed",false);
    }

    public Message listPermissions(UUIDNameBundle player,Player p) {
        for(PermissionAttachmentInfo s :Bukkit.getPlayer(player.getUniqueId()).getEffectivePermissions()) {
            MarnicLogger.info(s.getPermission(),p);
        }
        return new Message("Permissions were listed",false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(users.containsKey(e.getPlayer().getUniqueId())) {
            RankUser user = getUserByName(e.getPlayer().getUniqueId());
            onlinePlayers.put(user.getUuid(),user);
            user.addToRealUser(plugin);
        }else{
            if(hasDefaultRank) {
                addRankToUser(new UUIDNameBundle(e.getPlayer()),"default");
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(users.containsKey(e.getPlayer().getUniqueId())) {
            RankUser user = getUserByName(e.getPlayer().getUniqueId());
            onlinePlayers.remove(user.getUuid());
            user.removeAttachment();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {

    }

    public HashMap<UUID, RankUser> getOnlinePlayers() {
        return onlinePlayers;
    }
}
