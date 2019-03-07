package me.marnic.permissions.handler;

import me.marnic.permissions.rank.RankUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class UUIDNameBundle {
    private UUID uuid;
    private String name;
    private boolean isOnline;

    public UUIDNameBundle(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.isOnline = true;
    }

    public UUIDNameBundle(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.isOnline = false;
    }

    public UUIDNameBundle(RankUser player) {
        this.uuid = player.getUuid();
        this.name = player.getRealName();
        this.isOnline = true;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
