package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.duc.api.inventory.DInventory;
import com.darksoldier1404.duc.lang.DLang;
import com.darksoldier1404.duc.utils.NBT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CashShopFunction {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();
    private static final String prefix = plugin.prefix;
    private static final DLang lang = plugin.lang;

    public static ItemStack addDLCNBT(ItemStack item, double cash, double mileage) {
        item = NBT.setDoubleTag(item, "cash", cash);
        item = NBT.setDoubleTag(item, "mileage", mileage);
        return item;
    }

    public static ItemStack removeDLCNBT(ItemStack item) {
        item = NBT.removeTag(item, "cash");
        item = NBT.removeTag(item, "mileage");
        return item;
    }

    public static double getCashPrice(ItemStack item) {
        return NBT.getDoubleTag(item, "cash");
    }

    public static double getMileagePrice(ItemStack item) {
        return NBT.getDoubleTag(item, "mileage");
    }

    public static boolean isDLC(ItemStack item) {
        return NBT.hasTagKey(item, "cash");
    }


    public static void openShopShowCase(Player p, String name) {
        plugin.currentEditShop.put(p.getUniqueId(), name);
        Inventory inv = new DInventory(null, lang.getWithArgs("shop_display_gui_title", name), 54, plugin);

        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getConfigurationSection("Shop.Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), shop.getItemStack("Shop.Items." + key));
            }
        }
        p.openInventory(inv);
    }

    public static void saveShopShowCase(Player p, Inventory inv) {
        String name = plugin.currentEditShop.get(p.getUniqueId());
        YamlConfiguration shop = plugin.shops.get(name);
        for (int i = 0; i < inv.getSize(); i++) {
                shop.set("Shop.Items." + i, inv.getItem(i));
        }
        p.sendMessage(prefix + lang.getWithArgs("shop_display_gui_save_successful", name));
        Utils.saveData(name, "shops", shop);
        plugin.currentEditShop.remove(p.getUniqueId());
    }

    public static void setShopCashPrice(Player p, int slot, double cash, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".cash", cash);
            Utils.saveData(name, "shops", shop);
            p.sendMessage(prefix + lang.getWithArgs("shop_cmd_cash_price_set_successful", String.valueOf(cash)));
        } else {
            p.sendMessage(prefix + lang.get("shop_display_no_item_in_slot"));
        }
    }

    public static void setShopMileagePrice(Player p, int slot, double mileage, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".mileage", mileage);
            Utils.saveData(name, "shops", shop);
            p.sendMessage(prefix + lang.getWithArgs("shop_cmd_mileage_price_set_successful", String.valueOf(mileage)));
        } else {
            p.sendMessage(prefix + lang.get("shop_display_no_item_in_slot"));
        }
    }

    public static void buyWithCash(Player p, ItemStack item) {
        if (item == null) return;
        if (NBT.getDoubleTag(item, "cash") == 0 || NBT.getDoubleTag(item, "cash") == -1) {
            p.sendMessage(prefix + lang.get("shop_cant_buy_with_cash"));
            return;
        }
        double cash = getCashPrice(item);
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(prefix + lang.get("inventory_is_full"));
            return;
        }
        if (!CashFunction.isEnoughCash(p, cash)) {
            p.sendMessage(prefix + lang.get("not_enough_cash"));
            return;
        }
        CashFunction.takeCash(p, cash);
        ItemStack r = item.clone();
        r = NBT.removeTag(r, "cash");
        r = NBT.removeTag(r, "mileage");
        ItemMeta im = r.getItemMeta();
        List<String> lore = im.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        im.setLore(lore);
        r.setItemMeta(im);
        p.getInventory().addItem(r);
        p.sendMessage(prefix + lang.get("shop_buy_successful"));
    }

    public static void buyWithMileage(Player p, ItemStack item) {
        if (item == null) return;
        if (NBT.getDoubleTag(item, "mileage") == 0 || NBT.getDoubleTag(item, "mileage") == -1) {
            p.sendMessage(prefix + lang.get("shop_cant_buy_with_mileage"));
            return;
        }
        double mileage = getMileagePrice(item);
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(prefix + lang.get("inventory_is_full"));
            return;
        }
        if (!CashFunction.isEnoughMileage(p, mileage)) {
            p.sendMessage(prefix + lang.get("not_enough_mileage"));
            return;
        }
        CashFunction.takeMileage(p, mileage);
        ItemStack r = item.clone();
        r = NBT.removeTag(r, "cash");
        r = NBT.removeTag(r, "mileage");
        ItemMeta im = r.getItemMeta();
        List<String> lore = im.getLore();
        lore.remove(lore.size() - 1);
        lore.remove(lore.size() - 1);
        im.setLore(lore);
        r.setItemMeta(im);
        p.getInventory().addItem(r);
        p.sendMessage(prefix + lang.get("shop_buy_successful"));
    }

    public static void openShop(Player p, String name) {
        if (!plugin.shops.containsKey(name)) {
            p.sendMessage(prefix + lang.get("shop_is_not_exists"));
            return;
        }
        Inventory inv = new DInventory(null, lang.getWithArgs("shop_open_gui_title", name), 54, plugin);
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getConfigurationSection("Shop.Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
                ItemStack item = shop.getItemStack("Shop.Items." + key);
                ItemMeta im = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im.hasLore()) {
                    lore = im.getLore();
                }
                if (shop.getString("Shop.Prices." + key + ".cash") == null || shop.getInt("Shop.Prices." + key + ".cash") == -1) {
                    item = NBT.setDoubleTag(item, "cash", -1);
                    lore.add(lang.get("shop_item_lore_cant_buy_with_cash"));
                } else {
                    double price = shop.getDouble("Shop.Prices." + key + ".cash");
                    item = NBT.setDoubleTag(item, "cash", price);
                    lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_cash", String.valueOf(price)));
                }
                if (shop.getString("Shop.Prices." + key + ".mileage") == null || shop.getInt("Shop.Prices." + key + ".mileage") == -1) {
                    item = NBT.setDoubleTag(item, "mileage", -1);
                    lore.add(lang.get("shop_item_lore_cant_buy_with_mileage"));
                } else {
                    double price = shop.getDouble("Shop.Prices." + key + ".mileage");
                    item = NBT.setDoubleTag(item, "mileage", price);
                    lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_mileage", String.valueOf(price)));
                }
                ItemStack r = item.clone();
                ItemMeta rm = r.getItemMeta();
                rm.setLore(lore);
                r.setItemMeta(rm);
                inv.setItem(Integer.parseInt(key), r);
            }
        }
        p.openInventory(inv);
    }
}
