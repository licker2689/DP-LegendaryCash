package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
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

    // get data for offline Player
    public static double getOfflinePlayerCash(Player p, UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data.getDouble("Player.CASH");
        } catch (Exception e) {
            p.sendMessage(plugin.prefix + "해당 플레이어의 데이터가 존재하지 않습니다.");
            return 0;
        }
    }

    public static double getOfflinePlayerCash(UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data.getDouble("Player.CASH");
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getOfflinePlayerMileage(Player p, UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data.getDouble("Player.MILEAGE");
        } catch (Exception e) {
            p.sendMessage(plugin.prefix + "해당 플레이어의 데이터가 존재하지 않습니다.");
            return 0;
        }
    }

    public static double getOfflinePlayerMileage(UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data.getDouble("Player.MILEAGE");
        } catch (Exception e) {
            return 0;
        }
    }

    @Nullable
    public static YamlConfiguration getOfflinePlayerData(Player p, UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data;
        } catch (Exception e) {
            p.sendMessage(plugin.prefix + "해당 플레이어의 데이터가 존재하지 않습니다.");
            return null;
        }
    }

    @Nullable
    public static YamlConfiguration getOfflinePlayerData(UUID uuid) {
        try {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveOfflinePlayerData(UUID uuid, YamlConfiguration data) {
        try {
            data.save(new File(plugin.getDataFolder() + "/data", uuid + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
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
        plugin.lang = new DLang(plugin.config.getString("Lang") == null ? "Korean" : plugin.config.getString("Lang"), plugin);
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }

    public static void loadDefaultConfig() {
        plugin.config = ConfigUtils.loadDefaultPluginConfig(plugin);
        plugin.prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.prefix"));
    }

    public static void syncLegacyData() {
        plugin.shops.values().forEach(shop -> {
            if (shop.get("Shop.Items") != null) {
                shop.set("Shop.Pages.0.Items", shop.get("Shop.Items"));
                shop.set("Shop.Items", null);
                shop.set("Shop.Pages.0.Prices", shop.get("Shop.Prices"));
                shop.set("Shop.Prices", null);
            }
        });
    }

    public static String getColoredText(String[] args, int line) {
        StringBuilder s = new StringBuilder();
        args = Arrays.copyOfRange(args, line, args.length);
        Iterator<String> i = Arrays.stream(args).iterator();
        while (i.hasNext()) {
            s.append(i.next()).append(" ");
        }
        // delete last space
        if(s.charAt(s.length()-1) == ' ') {
            s.deleteCharAt(s.length()-1);
        }
        return ColorUtils.applyColor(s.toString());
    }

    public static void syncLegacyConfig() {
        if(plugin.config.get("Settings.Benta") == null) {
            plugin.config.set("Settings.Benta.useBenta", false);
            plugin.config.set("Settings.Benta.Token", "벤타 서비스에서 만든 어플리케이션의 토큰을 입력해주세요.");
            plugin.config.set("Settings.Benta.Title", "결제시 표시될 타이틀을 입력해주세요.");
            ConfigUtils.savePluginConfig(plugin, plugin.config);
        }
        if(plugin.config.get("Settings.Log") == null) {
            plugin.config.set("Settings.Log.useLog", false);
            plugin.config.set("Settings.Log.logLevel", -1);
            plugin.config.set("Settings.Log.useConsoleLog", false);
            plugin.config.set("Settings.Log.autoSaveInterval", 20*600);
            plugin.config.set("Settings.Log.savePath", "logs");
            ConfigUtils.savePluginConfig(plugin, plugin.config);
        }
    }


}
