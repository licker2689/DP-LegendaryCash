package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.utils.ConfigUtils;
import com.darksoldier1404.dlc.utils.NBT;
import com.darksoldier1404.dlc.utils.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("all")
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

    @EventHandler
    public void onInteractCheck(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getHand() == EquipmentSlot.OFF_HAND) return; // 더블클릭 방지
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getItem() == null) return;
            ItemStack item = e.getItem();
            if(e.getItem().getItemMeta() == null) return;
            if(NBT.hasTagKey(e.getItem(), "CASH")) {
                double cash = Double.parseDouble(NBT.getStringTag(e.getItem(), "CASH").replace('"', ' ').trim());
                CashFunction.addCash(p, cash);
                item.setAmount(item.getAmount() - 1);
                p.sendMessage(plugin.prefix + "§e" + cash + "§a캐시를 획득했습니다.");
            }
            else if(NBT.hasTagKey(e.getItem(), "MILEAGE")) {
                double mileage = Double.parseDouble(NBT.getStringTag(e.getItem(), "MILEAGE").replace('"', ' ').trim());
                CashFunction.addMileage(p, mileage);
                item.setAmount(item.getAmount() - 1);
                p.sendMessage(plugin.prefix + "§e" + mileage + "§a마일리지를 획득했습니다.");
            }
        }
    }
}
