package com.darksoldier1404.dlc;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LegendaryCash extends JavaPlugin {
    private static LegendaryCash plugin;
    public String prefix;
    public YamlConfiguration config;
    public Map<UUID, YamlConfiguration> udata = new HashMap<>();

    public static LegendaryCash getInstance() {
        return plugin;
    }


    public void onEnable() {
        plugin = this;
        getLogger().info("LegendaryCash has been enabled!");
    }

    public void onDisable() {
        getLogger().info("LegendaryCash has been disabled!");
    }
}
