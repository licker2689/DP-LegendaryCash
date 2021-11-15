package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.utils.NBT;
import org.bukkit.inventory.ItemStack;

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
        return Double.parseDouble(NBT.getStringTag(item, "cash"));
    }

    public static double getMileagePrice(ItemStack item) {
        return Double.parseDouble(NBT.getStringTag(item, "mileage"));
    }

    public static boolean isDLC(ItemStack item) {
        return NBT.hasTagKey(item, "cash");
    }



}
