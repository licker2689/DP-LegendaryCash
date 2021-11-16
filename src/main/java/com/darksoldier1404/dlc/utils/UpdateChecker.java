package com.darksoldier1404.dlc.utils;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static void check() {
        String currentVersion = plugin.getDescription().getVersion();
        String pluginName = plugin.getDescription().getName();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/darksoldier1404/" + pluginName + "/master/src/main/resources/plugin.yml").openConnection();
                connection.connect();
                String rr = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().toList().get(1).split(" ")[1];
                if (!currentVersion.equals(rr)) {
                    LegendaryCash.getInstance().getLogger().info("A new version of LegendaryCash is available! " + rr);
                    LegendaryCash.getInstance().getLogger().info("This plugin's version is " + currentVersion);
                    LegendaryCash.getInstance().getLogger().info("최신 버전이 존재합니다! " + rr);
                    LegendaryCash.getInstance().getLogger().info("이 플러그인의 버전은 " + currentVersion + " 입니다. 업데이트를 해주시기 바랍니다.");
                } else {
                    LegendaryCash.getInstance().getLogger().info("이 플러그인은 최신버전 입니다." + currentVersion);
                }
            } catch (IOException e) {
                LegendaryCash.getInstance().getLogger().info("업데이트 정보를 가져오는데 실패했습니다.");
            }
        });
    }

    public static void check(Player p) {
        String currentVersion = plugin.getDescription().getVersion();
        String pluginName = plugin.getDescription().getName();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://raw.githubusercontent.com/darksoldier1404/" + pluginName + "/master/src/main/resources/plugin.yml").openConnection();
                connection.connect();
                String rr = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().toList().get(1).split(" ")[1];
                if (!currentVersion.equals(rr)) {
                    p.sendMessage("[ " + pluginName + " Update Checker ] " + "최신 버전이 존재합니다! " + rr);
                    p.sendMessage("[ " + pluginName + " Update Checker ] " + "이 플러그인의 버전은 " + currentVersion + " 입니다. 업데이트를 해주시기 바랍니다.");
                    p.sendMessage("[ " + pluginName + " Update Checker ] " + "업데이트 다운로드 : https://github.com/darksoldier1404/" + pluginName + "/releases");
                } else {
                    p.sendMessage("[ " + pluginName + " Update Checker ] " + "이 플러그인은 최신버전 입니다." + currentVersion);
                }
            } catch (IOException e) {
                p.sendMessage("[ " + pluginName + " Update Checker ] " + "업데이트 정보를 가져오는데 실패했습니다.");
            }
        });
    }
}