package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("all")
public class CashFunction {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();
    private static final String prefix = plugin.prefix;
    private static final DLang lang = plugin.lang;

    public static boolean canUseCashCheck() {
        return plugin.config.getBoolean("Settings.useCashCheck");
    }

    public static boolean canUseMileageCheck() {
        return plugin.config.getBoolean("Settings.useMileageCheck");
    }

    public static float getMinCashCheck() {
        return (float) plugin.config.getDouble("Settings.minCashCheck");
    }

    public static float getMinMileageCheck() {
        return (float) plugin.config.getDouble("Settings.minMileageCheck");
    }

    @Nullable
    public static Material getCashCheckMaterial() {
        return Material.getMaterial(plugin.config.getString("Settings.cashCheckItem.ItemType"));
    }

    @Nullable
    public static Material getMileageCheckMaterial() {
        return Material.getMaterial(plugin.config.getString("Settings.mileageCheckItem.ItemType"));
    }

    public static int getCashCheckCMI() {
        return plugin.config.getInt("Settings.cashCheckItem.CustomModelData");
    }

    public static int getMileageCheckCMI() {
        return plugin.config.getInt("Settings.mileageCheckItem.CustomModelData");
    }

    public static String getCashCheckDisplayName() {
        return plugin.config.getString("Settings.cashCheckItem.DisplayName");
    }

    public static String getMileageCheckDisplayName() {
        return plugin.config.getString("Settings.mileageCheckItem.DisplayName");
    }

    public static List<String> getCashCheckLore() {
        return plugin.config.getStringList("Settings.cashCheckItem.Lores");
    }

    public static List<String> getMileageCheckLore() {
        return plugin.config.getStringList("Settings.mileageCheckItem.Lores");
    }

    public static void getCashCheck(Player p, double cash, int amount) {
        if(isEnoughCash(p, cash*amount)){
            if(getMinCashCheck() > cash){
                p.sendMessage(prefix + lang.getWithArgs("check_cmd_cash_minimum_amount", String.valueOf(getMinCashCheck())));
                return;
            }
            if(getCashCheckMaterial() == null){
                p.sendMessage(prefix + lang.get("check_cmd_check_item_is_not_set"));
                return;
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(prefix + lang.get("inventory_is_full"));
                return;
            }
            ItemStack item = new ItemStack(getCashCheckMaterial());
            item.setAmount(amount);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', getCashCheckDisplayName().replace("<cash>", String.valueOf(cash))));
            List<String> lore = getCashCheckLore();
            for(int i = 0; i < lore.size(); i++){
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i).replace("<cash>", String.valueOf(cash))));
            }
            im.setLore(lore);
            item.setItemMeta(im);
            item = NBT.setDoubleTag(item, "CASH", cash);
            p.getInventory().addItem(item);
            p.sendMessage(prefix + lang.getWithArgs("check_cmd_cash_successfully_printed", String.valueOf(amount)));
            takeCash(p, cash*amount);
        }else{
            p.sendMessage(prefix + lang.get("not_enough_cash"));
        }
    }

    public static void getMileageCheck(Player p, double mileage, int amount) {
        if(isEnoughMileage(p, mileage*amount)) {
            if (getMinMileageCheck() > mileage) {
                p.sendMessage(prefix + lang.getWithArgs("check_cmd_mileage_minimum_amount", String.valueOf(getMinMileageCheck())));
                return;
            }
            if (getMileageCheckMaterial() == null) {
                p.sendMessage(prefix + lang.get("check_cmd_check_item_is_not_set"));
                return;
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(prefix + lang.get("inventory_is_full"));
                return;
            }
            ItemStack item = new ItemStack(getMileageCheckMaterial());
            item.setAmount(amount);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', getMileageCheckDisplayName().replace("<mileage>", String.valueOf(mileage))));
            List<String> lore = getMileageCheckLore();
            for(int i = 0; i < lore.size(); i++){
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i).replace("<mileage>", String.valueOf(mileage))));
            }
            im.setLore(lore);
            item.setItemMeta(im);
            item = NBT.setDoubleTag(item, "MILEAGE", mileage);
            p.getInventory().addItem(item);
            p.sendMessage(prefix + lang.getWithArgs("check_cmd_mileage_successfully_printed", String.valueOf(amount)));
            takeMileage(p, mileage);
        }else{
            p.sendMessage(prefix + lang.get("not_enough_mileage"));
        }
    }

    public static double getCash(Player p) {
        return plugin.ucash.get(p.getUniqueId());
    }

    public static double getMileage(Player p) {
        return plugin.umileage.get(p.getUniqueId());
    }

    public static void addCash(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return;
        }
        double cash = getCash(p);
        cash += amount;
        plugin.ucash.put(p.getUniqueId(), cash);
    }

    public static void addMileage(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return;
        }
        double mileage = getMileage(p);
        mileage += amount;
        plugin.umileage.put(p.getUniqueId(), mileage);
    }

    public static boolean takeCash(Player p, double amount) {
        if(amount <= 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return false;
        }
        double cash = getCash(p);
        if (cash - amount >= 0) {
            cash -= amount;
            plugin.ucash.put(p.getUniqueId(), cash);
            return true;
        }else{
            p.sendMessage(prefix + lang.get("not_enough_cash"));
            return false;
        }
    }

    public static boolean takeMileage(Player p, double amount) {
        if(amount <= 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return false;
        }
        double mileage = getMileage(p);
        if (mileage - amount >= 0) {
            mileage -= amount;
            plugin.umileage.put(p.getUniqueId(), mileage);
            return true;
        }else{
            p.sendMessage(prefix + lang.get("not_enough_mileage"));
            return false;
        }
    }

    public static void setCash(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return;
        }
        plugin.ucash.put(p.getUniqueId(), amount);
    }

    public static void setMileage(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(prefix + lang.get("value_is_negative"));
            return;
        }
        plugin.umileage.put(p.getUniqueId(), amount);
    }

    public static boolean isEnoughCash(Player p, double amount) {
        double cash = getCash(p);
        return cash - amount >= 0;
    }

    public static boolean isEnoughMileage(Player p, double amount) {
        double mileage = getMileage(p);
        return mileage - amount >= 0;
    }

    public static void sendCash(Player sender, Player receiver, double amount) {
        if(sender.getUniqueId().equals(receiver.getUniqueId())){
            sender.sendMessage(prefix + lang.get("transfer_cmd_cant_send_to_me"));
            return;
        }
        if(amount <= 0){
            sender.sendMessage(prefix + lang.get("transfer_cmd_amount_too_small"));
            return;
        }
        if (isEnoughCash(sender, amount)) {
            addCash(receiver, amount);
            takeCash(sender, amount);
            sender.sendMessage(prefix + lang.getWithArgs("transfer_cmd_cash_successfully_transferred", String.valueOf(amount), receiver.getName()));
        }else{
            sender.sendMessage(prefix + lang.get("not_enough_cash"));
        }
    }

    public static void sendMileage(Player sender, Player receiver, double amount) {
        if(sender.getUniqueId().equals(receiver.getUniqueId())){
            sender.sendMessage(prefix + lang.get("transfer_cmd_cant_send_to_me"));
            return;
        }
        if (isEnoughMileage(sender, amount)) {
            addMileage(receiver, amount);
            takeMileage(sender, amount);
            sender.sendMessage(prefix + lang.getWithArgs("transfer_cmd_mileage_successfully_transferred", String.valueOf(amount), receiver.getName()));
        }else{
            sender.sendMessage(prefix + lang.get("not_enough_mileage"));
        }
    }

    public static void checkCash(Player p) {
        double cash = getCash(p);
        p.sendMessage(prefix + "보유 캐시 : " + cash);
    }

    public static void checkMileage(Player p) {
        double mileage = getMileage(p);
        p.sendMessage(prefix + "보유 마일리지 : " + mileage);
    }

    public static boolean isOpen(Player p) {
        return plugin.udata.get(p.getUniqueId()).getBoolean("Player.SHOW");
    }

    public static void setOpen(Player p, boolean b) {
        plugin.udata.get(p.getUniqueId()).set("Player.SHOW", b);
    }
}
