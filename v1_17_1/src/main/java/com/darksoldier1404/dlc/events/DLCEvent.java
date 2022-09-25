package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.functions.CurrencyType;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("all")
public class DLCEvent implements Listener {
    private final LegendaryCash plugin = LegendaryCash.getInstance();
    private final DLang lang = plugin.lang;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Utils.initData(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Utils.quitAndSaveData(p.getUniqueId());
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        String msg = e.getMessage();
        Player p = e.getPlayer();
        if (plugin.currentEditShopItem.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String name = plugin.currentEditShopItem.get(p.getUniqueId()).getA();
            int slot = plugin.currentEditShopItem.get(p.getUniqueId()).getB();
            CurrencyType type = plugin.currentEditShopItem.get(p.getUniqueId()).getC();
            try {
                double price = Double.parseDouble(msg);
                if (type == CurrencyType.CASH) {
                    CashShopFunction.setShopCashPrice(p, slot, price, name);
                } else {
                    CashShopFunction.setShopMileagePrice(p, slot, price, name);
                }
                plugin.currentEditShopItem.remove(p.getUniqueId());
                plugin.currentEditShop.remove(p.getUniqueId());
                CashShopFunction.openShopPriceSetting(p, name);
                return;
            } catch (Exception ex) {
                p.sendMessage(plugin.prefix + lang.get("amount_required"));
                plugin.currentEditShopItem.remove(p.getUniqueId());
                plugin.currentEditShop.remove(p.getUniqueId());
                CashShopFunction.openShopPriceSetting(p, name);
                return;
            }
        }
        if (msg.contains("{cash}")) {
            e.setMessage(e.getMessage().replace("{cash}", ((HashMap<UUID, Double>) plugin.dphm.getPlaceholder("cash").getValue()).get(e.getPlayer().getUniqueId()) + ""));
        }
        if (msg.contains("{mileage}")) {
            e.setMessage(e.getMessage().replace("{mileage}", ((HashMap<UUID, Double>) plugin.dphm.getPlaceholder("mileage").getValue()).get(e.getPlayer().getUniqueId()) + ""));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory() instanceof DInventory) {
            if (!plugin.currentEditShop.containsKey(p.getUniqueId())) return;
            DInventory di = (DInventory) e.getInventory();
            if(di.getObj() != null) return;
            if (di.isValidHandler(plugin)) {
                if (e.getView().getTitle().equals(lang.getWithArgs("shop_display_gui_title", plugin.currentEditShop.get(p.getUniqueId())))) {
                    CashShopFunction.saveShopShowCase(p, (Inventory) di);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory() instanceof DInventory) {
            DInventory di = (DInventory) e.getClickedInventory();
            if (di.isValidHandler(plugin)) {
                e.setCancelled(true);
                if (di.getObj() != null) {
                    if (di.getObj().equals("priceSetting")) {
                        if (e.getClick() == ClickType.LEFT) {
                            CashShopFunction.setCashPriceWithChat(p, e.getSlot());
                            return;
                        }
                        if (e.getClick() == ClickType.RIGHT) {
                            CashShopFunction.setMileagePriceWithChat(p, e.getSlot());
                        }
                        return;
                    }
                }
                if (plugin.currentEditShop.containsKey(e.getWhoClicked().getUniqueId())) return;
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

    @EventHandler
    public void onInteractCheck(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return; // 더블클릭 방지
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() == null) return;
            ItemStack item = e.getItem();
            if (e.getItem().getItemMeta() == null) return;
            if (NBT.hasTagKey(e.getItem(), "CASH")) {
                double cash = NBT.getDoubleTag(e.getItem(), "CASH");
                CashFunction.addCash(p, cash);
                item.setAmount(item.getAmount() - 1);
                p.sendMessage(plugin.prefix + "§e" + cash + "§a캐시를 획득했습니다.");
            } else if (NBT.hasTagKey(e.getItem(), "MILEAGE")) {
                double mileage = NBT.getDoubleTag(e.getItem(), "MILEAGE");
                CashFunction.addMileage(p, mileage);
                item.setAmount(item.getAmount() - 1);
                p.sendMessage(plugin.prefix + "§e" + mileage + "§a마일리지를 획득했습니다.");
            }
        }
    }
}
