package com.darksoldier1404.dlc;

import com.darksoldier1404.dlc.commands.CashCommand;
import com.darksoldier1404.dlc.commands.CashShopCommand;
import com.darksoldier1404.dlc.events.DLCEvent;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.duc.UniversalCore;
import com.darksoldier1404.duc.api.placeholder.DPHManager;
import com.darksoldier1404.duc.api.placeholder.DPlaceHolder;
import com.darksoldier1404.duc.lang.DLang;
import com.darksoldier1404.duc.utils.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class LegendaryCash extends JavaPlugin {
    private UniversalCore core;
    private static LegendaryCash plugin;
    public String prefix;
    public YamlConfiguration config;
    public final Map<UUID, YamlConfiguration> udata = new HashMap<>();
    public final Map<UUID, Double> ucash = new HashMap<>();
    public final Map<UUID, Double> umileage = new HashMap<>();
    public final Map<String, YamlConfiguration> shops = new HashMap<>();
    public final Map<UUID, String> currentEditShop = new HashMap<>();
    public Map<String, YamlConfiguration> langFiles = new HashMap<>();
    public DLang lang;
    public DPHManager dphm;

    public static LegendaryCash getInstance() {
        return plugin;
    }


    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("DP-UniversalCore");
        if (pl == null) {
            getLogger().warning("DP-UniversalCore 플러그인이 설치되어있지 않습니다.");
            getLogger().warning("DP-LegendaryCash 플러그인을 비활성화 합니다.");
            plugin.setEnabled(false);
            return;
        }
        core = (UniversalCore) pl;
        dphm = core.dphm;
        Utils.loadDefaultConfig();
        lang = new DLang(config.getString("Lang") == null ? "Korean" : config.getString("Lang"), plugin);
        ShopConfigUtil.loadAllShop();
        plugin.getServer().getPluginManager().registerEvents(new DLCEvent(), plugin);
        getCommand("캐시").setExecutor(new CashCommand());
        getCommand("캐시상점").setExecutor(new CashShopCommand());
        dphm.register(new DPlaceHolder(plugin.getServer().getConsoleSender(), "cash", ucash, true), "cash");
        dphm.register(new DPlaceHolder(plugin.getServer().getConsoleSender(), "mileage", umileage, true), "mileage");
        String r = lang.getWithArgs("cant_buy_storage_is_max", String.valueOf(10));

    }

    public void onDisable() {
        for (UUID uuid : udata.keySet()) {
            Utils.saveData(uuid);
        }
        ShopConfigUtil.saveAllShop();
    }
}
