package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dppc.api.placeholder.DPlaceHolder;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Utils {
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
                plugin.ucash.put(uuid, data.getDouble("Player.CASH"));
                plugin.umileage.put(uuid, data.getDouble("Player.MILEAGE"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            YamlConfiguration data = YamlConfiguration
                    .loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            plugin.udata.put(uuid, data);
            plugin.ucash.put(uuid, data.getDouble("Player.CASH"));
            plugin.umileage.put(uuid, data.getDouble("Player.MILEAGE"));
        }
    }

    public static void saveData(UUID uuid) { // for user data
        YamlConfiguration data = plugin.udata.get(uuid);
        try {
            data.set("Player.CASH", plugin.ucash.get(uuid));
            data.set("Player.MILEAGE", plugin.umileage.get(uuid));
            data.save(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData(String key, String path, YamlConfiguration data) { // for shop
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
        plugin.ucash.remove(uuid);
        plugin.umileage.remove(uuid);
        plugin.dphm.unregister(uuid + "_cash");
        plugin.dphm.unregister(uuid + "_mileage");
    }

    // reload config
    public static void reloadConfig() {
        plugin.config = ConfigUtils.reloadPluginConfig(plugin, plugin.config);
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }

    public static void loadDefaultConfig() {
        plugin.config = ConfigUtils.loadDefaultPluginConfig(plugin);
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }

}
