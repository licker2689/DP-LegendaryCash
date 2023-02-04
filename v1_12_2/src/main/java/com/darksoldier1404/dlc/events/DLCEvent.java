package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.functions.CurrencyType;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
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
            int page = plugin.currentEditShopItem.get(p.getUniqueId()).getD();
            try {
                double price = Double.parseDouble(msg);
                if (type == CurrencyType.CASH) {
                    CashShopFunction.setShopCashPrice(p, slot, price, name, page);
                } else {
                    CashShopFunction.setShopMileagePrice(p, slot, price, name, page);
                }
                plugin.currentEditShopItem.remove(p.getUniqueId());
                plugin.currentEditShop.remove(p.getUniqueId());
                CashShopFunction.openShopPriceSetting(p, name, page);
                return;
            } catch (Exception ex) {
                p.sendMessage(plugin.prefix + lang.get("amount_required"));
                plugin.currentEditShopItem.remove(p.getUniqueId());
                plugin.currentEditShop.remove(p.getUniqueId());
                CashShopFunction.openShopPriceSetting(p, name, page);
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
        if (CashShopFunction.currentInv.containsKey(p.getUniqueId())) {
            if (!plugin.currentEditShop.containsKey(p.getUniqueId())) return;
            DInventory di = CashShopFunction.currentInv.get(p.getUniqueId());
            if (di.isValidHandler(plugin)) {
                if (di.getObj() != null) {
                    if(di.getObj().equals("priceSetting")) {
                        if(!plugin.currentEditShopItem.containsKey(p.getUniqueId())) {
                            plugin.currentEditShop.remove(p.getUniqueId());
                            plugin.currentEditShopItem.remove(p.getUniqueId());
                            CashShopFunction.currentInv.remove(p.getUniqueId());
                            return;
                        }
                    }
                    if (di.getObj() instanceof Tuple<?, ?>) {
                        Tuple<String, Integer> tpl = (Tuple<String, Integer>) di.getObj();
                        if (tpl.getA().equals(plugin.currentEditShop.get(p.getUniqueId()))) {
                            CashShopFunction.saveShopShowCase(p, di, tpl.getB());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() == null) return;
        if (CashShopFunction.currentInv.containsKey(p.getUniqueId())) {
            DInventory di = CashShopFunction.currentInv.get(p.getUniqueId());
            if (di.isValidHandler(plugin)) {
                if (e.getCurrentItem() == null) return;
                if (di.getObj() != null) {
                    if (di.getObj().equals("priceSetting")) {
                        e.setCancelled(true);
                        if (NBT.hasTagKey(e.getCurrentItem(), "dlc_display")) {
                            return;
                        }
                        if (NBT.hasTagKey(e.getCurrentItem(), "dlc_page")) return;
                        if (NBT.hasTagKey(e.getCurrentItem(), "dlc_prev")) {
                            di.prevPage();
                            return;
                        }
                        if (NBT.hasTagKey(e.getCurrentItem(), "dlc_next")) {
                            di.nextPage();
                            return;
                        }
                        if (e.getClick() == ClickType.LEFT) {
                            CashShopFunction.setCashPriceWithChat(p, e.getSlot(), di.getCurrentPage());
                            return;
                        }
                        if (e.getClick() == ClickType.RIGHT) {
                            CashShopFunction.setMileagePriceWithChat(p, e.getSlot(), di.getCurrentPage());
                        }
                        return;
                    }
                }
                if (plugin.currentEditShop.containsKey(e.getWhoClicked().getUniqueId())) return;
                if (di.getObj() != null && di.getObj().equals("confirmBuyWithCash")) {
                    e.setCancelled(true);
                    if (NBT.hasTagKey(e.getCurrentItem(), "dlc_yes")) {
                        ItemStack item = di.getItem(13);
                        CashShopFunction.buyWithCash(p, item);
                        p.openInventory(CashShopFunction.prevInvs.get(p.getUniqueId()));
                        CashShopFunction.prevInvs.remove(p.getUniqueId());
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "dlc_cancel")) {
                        p.openInventory(CashShopFunction.prevInvs.get(p.getUniqueId()));
                        CashShopFunction.prevInvs.remove(p.getUniqueId());
                        return;
                    }
                    return;
                }
                if (di.getObj() != null && di.getObj().equals("confirmBuyWithMileage")) {
                    e.setCancelled(true);
                    if (NBT.hasTagKey(e.getCurrentItem(), "dlc_yes")) {
                        ItemStack item = di.getItem(13);
                        CashShopFunction.buyWithMileage(p, item);
                        p.openInventory(CashShopFunction.prevInvs.get(p.getUniqueId()));
                        CashShopFunction.prevInvs.remove(p.getUniqueId());
                        return;
                    }
                    if (NBT.hasTagKey(e.getCurrentItem(), "dlc_cancel")) {
                        p.openInventory(CashShopFunction.prevInvs.get(p.getUniqueId()));
                        CashShopFunction.prevInvs.remove(p.getUniqueId());
                        return;
                    }
                    return;
                }
                e.setCancelled(true);
                if (NBT.hasTagKey(e.getCurrentItem(), "dlc_display")) {
                    return;
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "dlc_page")) return;
                if (NBT.hasTagKey(e.getCurrentItem(), "dlc_prev")) {
                    di.prevPage();
                    return;
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "dlc_next")) {
                    di.nextPage();
                    return;
                }
                if (e.getClick() == ClickType.LEFT) {
                    CashShopFunction.confirmBuyWithCash(p, e.getCurrentItem(), di);
                    return;
                }
                if (e.getClick() == ClickType.RIGHT) {
                    CashShopFunction.confirmBuyWithMileage(p, e.getCurrentItem(), di);
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
