package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import org.bukkit.entity.Player;

public class CashFunction {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();

    public static double getCash(Player p) {
        return plugin.udata.get(p.getUniqueId()).getInt("Player.CASH");
    }

    public static double getMileage(Player p) {
        return plugin.udata.get(p.getUniqueId()).getInt("Player.MILEAGE");
    }

    public static void addCash(Player p, double amount) {
        double cash = getCash(p);
        cash += amount;
        plugin.udata.get(p.getUniqueId()).set("Player.CASH", cash);
    }

    public static void addMileage(Player p, double amount) {
        double mileage = getMileage(p);
        mileage += amount;
        plugin.udata.get(p.getUniqueId()).set("Player.MILEAGE", mileage);
    }

    public static boolean removeCash(Player p, double amount) {
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

    public static boolean removeMileage(Player p, double amount) {
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
        plugin.udata.get(p.getUniqueId()).set("Player.CASH", amount);
    }

    public static void setMileage(Player p, double amount) {
        plugin.udata.get(p.getUniqueId()).set("Player.MILEAGE", amount);
    }

    public static boolean isEnoughCash(Player p, double amount) {
        double cash = getCash(p);
        if (cash - amount >= 0) {
            return true;
        }else{
            p.sendMessage(plugin.prefix + "캐시가 부족합니다.");
            return false;
        }
    }

    public static boolean isEnoughMileage(Player p, double amount) {
        double mileage = getMileage(p);
        if (mileage - amount >= 0) {
            return true;
        }else{
            p.sendMessage(plugin.prefix + "마일리지가 부족합니다.");
            return false;
        }
    }

    public static void sendCash(Player sender, Player receiver, double amount) {
        if (isEnoughCash(sender, amount)) {
            addCash(receiver, amount);
            removeCash(sender, amount);
            sender.sendMessage(plugin.prefix + amount + "캐시를 " + receiver.getName() + "에게 송금했습니다.");
        }else{
            sender.sendMessage(plugin.prefix + "캐시가 부족합니다.");
        }
    }

    public static void sendMileage(Player sender, Player receiver, double amount) {
        if (isEnoughMileage(sender, amount)) {
            addMileage(receiver, amount);
            removeMileage(sender, amount);
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
