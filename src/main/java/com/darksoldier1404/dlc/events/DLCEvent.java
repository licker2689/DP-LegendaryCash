package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DLCEvent implements Listener {
    private final LegendaryCash plugin = LegendaryCash.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

    }
}
