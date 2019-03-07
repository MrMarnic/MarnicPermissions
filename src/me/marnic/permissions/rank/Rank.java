package me.marnic.permissions.rank;

import me.marnic.permissions.yml.ILoadable;
import me.marnic.permissions.yml.IWriteable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class Rank implements ILoadable, IWriteable {
    private String name;
    private ArrayList<String> permissions;

    public Rank(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public Rank(FileConfiguration configuration,String path,JavaPlugin plugin) {
        load(configuration,path,plugin);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public void load(FileConfiguration config, String path, JavaPlugin plugin) {
        name = config.getConfigurationSection(path).getName();
        permissions = (ArrayList<String>) config.getList(path+".permissions");
        if(permissions==null) {
            permissions = new ArrayList<>();
        }
    }

    @Override
    public void write(FileConfiguration configuration, String path, JavaPlugin plugin) {
        String p = path+"."+name;
        configuration.createSection(p);
        configuration.set(p+".permissions",permissions);

        plugin.saveConfig();
    }
}
