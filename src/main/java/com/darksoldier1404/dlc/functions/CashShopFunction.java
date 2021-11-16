package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.utils.ConfigUtils;
import com.darksoldier1404.dlc.utils.NBT;
import org.bukkit.Bukkit;
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

    public static ItemStack addDLCNBT(ItemStack item, double cash, double mileage) {
        item = NBT.setTag(item, "cash", cash);
        item = NBT.setTag(item, "mileage", mileage);
        return item;
    }

    public static ItemStack removeDLCNBT(ItemStack item) {
        item = NBT.removeTag(item, "cash");
        item = NBT.removeTag(item, "mileage");
        return item;
    }

    public static double getCashPrice(ItemStack item) {
        return Double.parseDouble(NBT.getStringTag(item, "cash").replace('"', ' ').trim());
    }

    public static double getMileagePrice(ItemStack item) {
        return Double.parseDouble(NBT.getStringTag(item, "mileage").replace('"', ' ').trim());
    }

    public static boolean isDLC(ItemStack item) {
        return NBT.hasTagKey(item, "cash");
    }


    public static void openShopShowCase(Player p, String name) {
        plugin.currentEditShop.put(p.getUniqueId(), name);
        Inventory inv = Bukkit.createInventory(null, 54, "§1" + name + " 캐시상점 진열");
        YamlConfiguration shop = plugin.shops.get(name);
        if(shop.getConfigurationSection("Shop.Items") != null) {
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
            if (inv.getItem(i) != null) {
                shop.set("Shop.Items." + i, inv.getItem(i));
            }
        }

        ConfigUtils.saveData(name, "shops", shop);
        plugin.currentEditShop.remove(p.getUniqueId());
    }

    public static void setShopCashPrice(Player p, int slot, double cash, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".cash", cash);
            ConfigUtils.saveData(name, "shops", shop);
            p.sendMessage(plugin.prefix + "§a캐시 가격이 설정되었습니다. : " + cash + " 캐시");
        } else {
            p.sendMessage(plugin.prefix + "§c해당 슬롯에는 진열된 아이템이 없습니다.");
        }
    }

    public static void setShopMileagePrice(Player p, int slot, double mileage, String name) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Items." + slot) != null) {
            shop.set("Shop.Prices." + slot + ".mileage", mileage);
            ConfigUtils.saveData(name, "shops", shop);
            p.sendMessage(plugin.prefix + "§a마일리지 가격이 설정되었습니다. : " + mileage + " 마일리지");
        } else {
            p.sendMessage(plugin.prefix + "§c해당 슬롯에는 진열된 아이템이 없습니다.");
        }
    }

    public static void buyWithCash(Player p, ItemStack item) {
        if(item == null) return;
        if (NBT.getStringTag(item, "cash") == null || NBT.getStringTag(item, "cash").contains("-1")) {
            p.sendMessage(plugin.prefix + "§c캐시로 구매할 수 없습니다.");
            return;
        }
        double cash = getCashPrice(item);
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(plugin.prefix + "§c인벤토리 공간이 부족합니다.");
            return;
        }
        if (!CashFunction.isEnoughCash(p, cash)) {
            p.sendMessage(plugin.prefix + "§c캐시가 부족합니다.");
            return;
        }
        CashFunction.takeCash(p, cash);
        ItemStack r = item.clone();
        r = NBT.removeTag(r, "cash");
        r = NBT.removeTag(r, "mileage");
        ItemMeta im = r.getItemMeta();
        List<String> lore =  im.getLore();
        lore.remove(lore.size()-1);
        lore.remove(lore.size()-1);
        im.setLore(lore);
        r.setItemMeta(im);
        p.getInventory().addItem(r);
        p.sendMessage(plugin.prefix + "§a구매에 성공하였습니다.");
    }

    public static void buyWithMileage(Player p, ItemStack item) {
        if(item == null) return;
        if (NBT.getStringTag(item, "mileage") == null || NBT.getStringTag(item, "mileage").contains("-1")) {
            p.sendMessage(plugin.prefix + "§c마일리지로 구매할 수 없습니다.");
            return;
        }
        double mileage = getMileagePrice(item);
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(plugin.prefix + "§c인벤토리 공간이 부족합니다.");
            return;
        }
        if (!CashFunction.isEnoughMileage(p, mileage)) {
            p.sendMessage(plugin.prefix + "§c마일리지가 부족합니다.");
            return;
        }
        CashFunction.takeMileage(p, mileage);
        ItemStack r = item.clone();
        r = NBT.removeTag(r, "cash");
        r = NBT.removeTag(r, "mileage");
        ItemMeta im = r.getItemMeta();
        List<String> lore =  im.getLore();
        lore.remove(lore.size()-1);
        lore.remove(lore.size()-1);
        im.setLore(lore);
        r.setItemMeta(im);
        p.getInventory().addItem(r);
        p.sendMessage(plugin.prefix + "§a구매에 성공하였습니다.");
    }

    public static void openShop(Player p, String name) {
        if(!plugin.shops.containsKey(name)) {
            p.sendMessage(plugin.prefix + "§c존재하지 않는 캐시상점 입니다.");
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 54, "§1" + name + " 캐시상점 아이템 목록");
        YamlConfiguration shop = plugin.shops.get(name);
        if(shop.getConfigurationSection("Shop.Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
                ItemStack item = shop.getItemStack("Shop.Items." + key);
                ItemMeta im = item.getItemMeta();
                List<String> lore = im.getLore() == null ? new ArrayList<>() : im.getLore();
                if(shop.getString("Shop.Prices." + key + ".cash") == null || shop.getInt("Shop.Prices." + key + ".cash") == -1) {
                    item = NBT.setTag(item, "cash", -1);
                    lore.add("§b좌클릭 구매 : §c캐시 구매 불가.");
                }else{
                    String price = shop.getString("Shop.Prices." + key + ".cash");
                    item = NBT.setTag(item, "cash", price);
                    lore.add("§b좌클릭 구매 : §e" + price + " 캐시");
                }
                if(shop.getString("Shop.Prices." + key + ".mileage") == null || shop.getInt("Shop.Prices." + key + ".mileage") == -1) {
                    item = NBT.setTag(item, "mileage", -1);
                    lore.add("§b우클릭 구매 : §c마일리지 구매 불가.");
                }else{
                    String price = shop.getString("Shop.Prices." + key + ".mileage");
                    item = NBT.setTag(item, "mileage", price);
                    lore.add("§b우클릭 구매 : §e" + price + " 마일리지");
                }
                im.setLore(lore);
                item.setItemMeta(im);
                inv.setItem(Integer.parseInt(key), item);
            }
        }
        p.openInventory(inv);
    }
}
