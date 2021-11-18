package com.darksoldier1404.dlc;

import com.darksoldier1404.dlc.commands.CashCommand;
import com.darksoldier1404.dlc.commands.CashShopCommand;
import com.darksoldier1404.dlc.events.DLCEvent;
import com.darksoldier1404.dlc.utils.ConfigUtils;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
import com.darksoldier1404.dlc.utils.UpdateChecker;
import com.darksoldier1404.duc.UniversalCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LegendaryCash extends JavaPlugin {
    private UniversalCore core;
    private static LegendaryCash plugin;
    public String prefix;
    public YamlConfiguration config;
    public Map<UUID, YamlConfiguration> udata = new HashMap<>();
    public Map<String, YamlConfiguration> shops = new HashMap<>();
    public Map<UUID, String> currentEditShop = new HashMap<>();

    public static LegendaryCash getInstance() {
        return plugin;
    }


    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("UniversalCore");
        if(pl == null) {
            getLogger().warning("DP-UniversalCore 플러그인이 설치되어있지 않습니다.");
            plugin.setEnabled(false);
            return;
        }
        core = (UniversalCore) pl;
        getLogger().info("LegendaryCash has been enabled!");
        ConfigUtils.loadDefaultConfig();
        ShopConfigUtil.loadAllShop();
        plugin.getServer().getPluginManager().registerEvents(new DLCEvent(), plugin);
        getCommand("캐시").setExecutor(new CashCommand());
        getCommand("캐시상점").setExecutor(new CashShopCommand());
        UpdateChecker.check();
    }

    public void onDisable() {
        getLogger().info("LegendaryCash has been disabled!");
        for (UUID uuid : udata.keySet()) {
            ConfigUtils.saveData(uuid);
        }
        ShopConfigUtil.saveAllShop();
    }
}
