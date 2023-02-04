package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.configuration.file.YamlConfiguration;

@SuppressWarnings("all")
public class ShopConfigUtil {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static boolean createShop(String name) {
        if (plugin.shops.containsKey(name)) {
            return false;
        }
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("Shop.Name", name);
        Utils.saveData(name, "shops", shop);
        plugin.shops.put(name, shop);
        return true;
    }

    public static boolean deleteShop(String name) {
        if (!plugin.shops.containsKey(name)) {
            return false;
        }
        Utils.deleteData(name, "shops");
        plugin.shops.remove(name);
        return true;
    }

    public static void loadAllShop() {
        plugin.shops.clear();
        if(Utils.getData("shops") != null) {
            Utils.getData("shops").forEach((shop) -> {
                plugin.shops.put(shop.getString("Shop.Name"), shop);
            });
        }
    }

    public static void saveAllShop() {
        plugin.shops.forEach((name, shop) -> {
            Utils.saveData(name, "shops", shop);
        });
    }
}
