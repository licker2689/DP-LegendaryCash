package com.darksoldier1404.dlc.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class NBT {
    public static ItemStack setTag(ItemStack objitem, String key, Object value) {
        final net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        final NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        ntc.set(key, NBTTagString.a(value.toString()));
        item.setTag(ntc);
        return CraftItemStack.asBukkitCopy(item);
    }

    public static ItemStack removeTag(ItemStack objitem, String key) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        item.setTag(ntc);
        item.removeTag(key);
        return CraftItemStack.asBukkitCopy(item);
    }

    public static ItemStack removeTagAll(ItemStack objitem) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        for (String key : ntc.getKeys()) {
            item.removeTag(key);
        }
        item.setTag(ntc);
        return CraftItemStack.asBukkitCopy(item);
    }

    public static String getStringTag(ItemStack objitem, String key) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        if (ntc.get(key) == null) {
            return null;
        }
        return ntc.get(key).toString();
    }

    public static boolean hasTagKey(ItemStack objitem, String key) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        return ntc.hasKey(key);
    }

    public static Map<String, String> getAllStringTag(ItemStack objitem) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(objitem);
        NBTTagCompound ntc = item.hasTag() ? item.getTag() : new NBTTagCompound();
        Map<String, String> tags = new HashMap<>();
        for (String key : ntc.getKeys()) {
            tags.put(key, ntc.get(key).toString());
        }
        return tags;
    }
}
