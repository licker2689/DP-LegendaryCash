package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.duc.utils.NBT;
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
                p.sendMessage(plugin.prefix + "수표로 뽑을 수 있는 최소 금액은 " + getMinCashCheck() + "캐시 입니다.");
                return;
            }
            if(getCashCheckMaterial() == null){
                p.sendMessage(plugin.prefix + "수표로 뽑을 수 있는 아이템이 설정되지 않았습니다.");
                return;
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(plugin.prefix + "§c인벤토리 공간이 부족합니다.");
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
            p.sendMessage(plugin.prefix + "수표로 " + amount + "개의 수표를 뽑았습니다.");
            takeCash(p, cash*amount);
        }else{
            p.sendMessage(plugin.prefix + "§c캐시가 부족합니다.");
        }
    }

    public static void getMileageCheck(Player p, double mileage, int amount) {
        if(isEnoughMileage(p, mileage*amount)) {
            if (getMinMileageCheck() > mileage) {
                p.sendMessage(plugin.prefix + "마일리지로 뽑을 수 있는 최소 마일리지는 " + getMinMileageCheck() + "마일리지 입니다.");
                return;
            }
            if (getMileageCheckMaterial() == null) {
                p.sendMessage(plugin.prefix + "마일리지로 뽑을 수 있는 아이템이 설정되지 않았습니다.");
                return;
            }
            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(plugin.prefix + "§c인벤토리 공간이 부족합니다.");
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
            p.sendMessage(plugin.prefix + "마일리지로 " + amount + "개의 수표를 뽑았습니다.");
            takeMileage(p, mileage);
        }else{
            p.sendMessage(plugin.prefix + "§c마일리지가 부족합니다.");
        }
    }

    public static double getCash(Player p) {
        return plugin.udata.get(p.getUniqueId()).getInt("Player.CASH");
    }

    public static double getMileage(Player p) {
        return plugin.udata.get(p.getUniqueId()).getInt("Player.MILEAGE");
    }

    public static void addCash(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return;
        }
        double cash = getCash(p);
        cash += amount;
        plugin.udata.get(p.getUniqueId()).set("Player.CASH", cash);
    }

    public static void addMileage(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return;
        }
        double mileage = getMileage(p);
        mileage += amount;
        plugin.udata.get(p.getUniqueId()).set("Player.MILEAGE", mileage);
    }

    public static boolean takeCash(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return false;
        }
        double cash = getCash(p);
        if (cash - amount >= 0) {
            cash -= amount;
            plugin.udata.get(p.getUniqueId()).set("Player.CASH", cash);
            return true;
        }else{
            p.sendMessage(plugin.prefix + "캐시가 부족합니다.");
            return false;
        }
    }

    public static boolean takeMileage(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return false;
        }
        double mileage = getMileage(p);
        if (mileage - amount >= 0) {
            mileage -= amount;
            plugin.udata.get(p.getUniqueId()).set("Player.MILEAGE", mileage);
            return true;
        }else{
            p.sendMessage(plugin.prefix + "마일리지가 부족합니다.");
            return false;
        }
    }

    public static void setCash(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return;
        }
        plugin.udata.get(p.getUniqueId()).set("Player.CASH", amount);
    }

    public static void setMileage(Player p, double amount) {
        if(amount < 0){
            p.sendMessage(plugin.prefix + "§c음수는 입력이 불가능합니다.");
            return;
        }
        plugin.udata.get(p.getUniqueId()).set("Player.MILEAGE", amount);
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
            sender.sendMessage(plugin.prefix + "자기 자신에게 송금할 수 없습니다.");
            return;
        }
        if(amount <= 0){
            sender.sendMessage(plugin.prefix + "송금할 금액이 0원 이하입니다.");
            return;
        }
        if (isEnoughCash(sender, amount)) {
            addCash(receiver, amount);
            takeCash(sender, amount);
            sender.sendMessage(plugin.prefix + amount + "캐시를 " + receiver.getName() + "에게 송금했습니다.");
        }else{
            sender.sendMessage(plugin.prefix + "캐시가 부족합니다.");
        }
    }

    public static void sendMileage(Player sender, Player receiver, double amount) {
        if(sender.getUniqueId().equals(receiver.getUniqueId())){
            sender.sendMessage(plugin.prefix + "자기 자신에게 송금할 수 없습니다.");
            return;
        }
        if (isEnoughMileage(sender, amount)) {
            addMileage(receiver, amount);
            takeMileage(sender, amount);
            sender.sendMessage(plugin.prefix + amount + "마일리지를 " + receiver.getName() + "에게 송금했습니다.");
        }else{
            sender.sendMessage(plugin.prefix + "마일리지가 부족합니다.");
        }
    }

    public static void checkCash(Player p) {
        double cash = getCash(p);
        p.sendMessage(plugin.prefix + "보유 캐시 : " + cash);
    }

    public static void checkMileage(Player p) {
        double mileage = getMileage(p);
        p.sendMessage(plugin.prefix + "보유 마일리지 : " + mileage);
    }

    public static boolean isOpen(Player p) {
        return plugin.udata.get(p.getUniqueId()).getBoolean("Player.SHOW");
    }

    public static void setOpen(Player p, boolean b) {
        plugin.udata.get(p.getUniqueId()).set("Player.SHOW", b);
    }
}
