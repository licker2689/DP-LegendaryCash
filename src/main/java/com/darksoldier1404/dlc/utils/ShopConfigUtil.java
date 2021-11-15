package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("all")
public class ShopConfigUtil {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static void createShop(String name) {
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("Shop.Name", name);
        ConfigUtils.saveData(name, "shops", shop);
        plugin.shops.put(name, shop);
    }

    public static void deleteShop(String name) {
        ConfigUtils.deleteData(name, "shops");
        plugin.shops.remove(name);
    }

    public static void loadAllShop() {
        plugin.shops.clear();
        ConfigUtils.getData("shops").forEach((shop) -> {
            plugin.shops.put(shop.getString("Shop.Name"), shop);
        });
    }

    public static void saveAllShop() {
        plugin.shops.forEach((name, shop) -> {
            ConfigUtils.saveData(name, "shops", shop);
        });
    }
}
