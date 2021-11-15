package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.configuration.file.YamlConfiguration;

public class ShopConfigUtil {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static void createShop(String name) {
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("Shop.Name", name);
        ConfigUtils.
    }

    public static void loadAllShop() {

    }
}
