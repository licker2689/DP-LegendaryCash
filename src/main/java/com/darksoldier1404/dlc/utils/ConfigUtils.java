package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ConfigUtils {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static void initData(UUID uuid) {
        final File file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        if (!file.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            data.set("Player.CASH", 0);
            data.set("Player.MILEAGE", 0);
            data.set("Player.SHOW", false);
            try {
                data.save(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
                plugin.udata.put(uuid, data);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            plugin.udata.put(uuid, data);
        }
    }

    public static void saveData(UUID uuid) {
        YamlConfiguration data = plugin.udata.get(uuid);
        try {
            data.save(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData(String key, String path, YamlConfiguration data) {
        try {
            data.save(new File(plugin.getDataFolder() + "/" + path, key + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteData(String key, String path) {
        File file = new File(plugin.getDataFolder() + "/" + path, key + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    public static YamlConfiguration getData(String key, String path) {
        File file = new File(plugin.getDataFolder() + "/" + path, key + ".yml");
        if (!file.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    @Nullable
    public static List<YamlConfiguration> getData(String path) {
        File file = new File(plugin.getDataFolder() + "/" + path);
        if (!file.exists()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        List<YamlConfiguration> list = Lists.newArrayList();
        for (File f : files) {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder() + "/" + path, f.getName()));
            list.add(data);
        }
        return list;
    }

    public static void quitAndSaveData(UUID uuid) {
        saveData(uuid);
        plugin.udata.remove(uuid);
    }

    // reload config
    public static void reloadConfig() {
        plugin.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }

    public static void loadDefaultConfig() {
        File fconfig = new File(plugin.getDataFolder(), "config.yml");
        if (!fconfig.exists()) {
            plugin.saveResource("config.yml", false);
        }
        plugin.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }
}
