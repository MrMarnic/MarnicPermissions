package me.marnic.permissions.yml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public interface IWriteable {
    void write(FileConfiguration configuration, String path, JavaPlugin plugin);
}
