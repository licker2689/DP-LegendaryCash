package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.utils.ConfigUtils;
import com.darksoldier1404.dlc.utils.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DLCEvent implements Listener {
    private final LegendaryCash plugin = LegendaryCash.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        ConfigUtils.initData(p.getUniqueId());
        if(p.isOp()) {
            UpdateChecker.check(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ConfigUtils.quitAndSaveData(p.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().contains("캐시상점 진열")) {
            CashShopFunction.saveShopShowCase(p, e.getView().getTopInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().contains("캐시상점 아이템 목록")) {
            e.setCancelled(true);
            if (e.getClick() == ClickType.LEFT) {
                CashShopFunction.buyWithCash(p, e.getCurrentItem());
                return;
            }
            if (e.getClick() == ClickType.RIGHT) {
                CashShopFunction.buyWithMileage(p, e.getCurrentItem());
            }
        }
    }


}
