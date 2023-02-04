package com.darksoldier1404.dlc.functions;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Quadruple;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("all")
public class CashShopFunction {
    private static final LegendaryCash plugin = LegendaryCash.getInstance();
    private static final String prefix = plugin.prefix;
    private static final DLang lang = plugin.lang;
    public static final Map<UUID, DInventory> prevInvs = new HashMap<>();

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

    public static void openShopShowCase(Player p, String name, String spage) {
        // check spage is integer and grate than -1
        int page;
        try {
            page = Integer.parseInt(spage);
            if (page < 0) {
                p.sendMessage(prefix + "페이지는 0 이상의 정수여야 합니다.");
                return;
            }
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + "페이지는 0 이상의 정수여야 합니다.");
            return;
        }
        plugin.currentEditShop.put(p.getUniqueId(), name);
        DInventory inv = new DInventory(null, lang.getWithArgs("shop_display_gui_title", name), 54, plugin);
        inv.setObj(Tuple.of(name, page));
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getConfigurationSection("Shop.Pages." + page + ".Items") != null) {
            for (String key : shop.getConfigurationSection("Shop.Pages." + page + ".Items").getKeys(false)) {
                inv.setItem(Integer.parseInt(key), shop.getItemStack("Shop.Pages." + page + ".Items." + key));
            }
        }
        p.openInventory(inv);
    }

    public static void openShopPriceSetting(Player p, String name, int prevPage) {
        plugin.currentEditShop.put(p.getUniqueId(), name);
        DInventory inv = new DInventory(null, getShopTitle(name), 54, true, plugin);
        inv.setObj("priceSetting");
        Map<Integer/*page*/, Map<Integer/*slot*/, ItemStack>> items = new HashMap<>();
        YamlConfiguration shop = plugin.shops.get(name);
        int ic = 0;
        if (shop.getConfigurationSection("Shop.Pages") != null) {
            for (String page : shop.getConfigurationSection("Shop.Pages").getKeys(false)) {
                Map<Integer, ItemStack> pageItems = new HashMap<>();
                for (int i = 0; i < 54; i++) {
                    ItemStack item = shop.getItemStack("Shop.Pages." + page + ".Items." + i);
                    if (item == null) {
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_display")) {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("§f");
                        item.setItemMeta(im);
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_page")) {
                        String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "§a" + page + " 페이지";
                        displayName = displayName.replace("<currentPage>", String.valueOf(Integer.parseInt(page) + 1));
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(displayName);
                        item.setItemMeta(im);
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_prev")) {
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_next")) {
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    ItemMeta im = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    if (im.hasLore()) {
                        lore = im.getLore();
                    }
                    double cash = shop.getDouble("Shop.Pages." + page + ".Prices." + i + ".cash");
                    double mileage = shop.getDouble("Shop.Pages." + page + ".Prices." + i + ".mileage");
                    if (cash <= 0) {
                        item = NBT.setDoubleTag(item, "cash", -1);
                        lore.add(lang.get("shop_item_lore_cant_buy_with_cash"));
                    } else {
                        item = NBT.setDoubleTag(item, "cash", cash);
                        lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_cash", String.valueOf(cash)));
                    }
                    if (mileage <= 0) {
                        item = NBT.setDoubleTag(item, "mileage", -1);
                        lore.add(lang.get("shop_item_lore_cant_buy_with_mileage"));
                    } else {
                        item = NBT.setDoubleTag(item, "mileage", mileage);
                        lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_mileage", String.valueOf(mileage)));
                    }
                    ItemStack r = item.clone();
                    ItemMeta rm = r.getItemMeta();
                    rm.setLore(lore);
                    r.setItemMeta(rm);
                    pageItems.put(ic, r.clone());
                    ic++;
                }
                items.put(Integer.parseInt(page), pageItems);
            }
        }
        int maxPages = shop.getInt("Shop.MaxPage");
        inv.setPages(maxPages);
        items.forEach((page, pageItems) -> {
            ItemStack[] contents = new ItemStack[54];
            for (int i = 0; i < 54; i++) {
                ItemStack item = pageItems.get(i);
                if (item != null) {
                    contents[i] = item;
                }
            }
            inv.setPageContent(page, contents);
            contents = new ItemStack[54];
        });
        inv.setUsePageTools(false);
        inv.update();
        inv.setCurrentPage(prevPage);
        p.openInventory(inv);
    }

    public static void saveShopShowCase(Player p, DInventory inv, int page) {
        String name = plugin.currentEditShop.get(p.getUniqueId());
        YamlConfiguration shop = plugin.shops.get(name);
        for (int i = 0; i < inv.getSize(); i++) {
            shop.set("Shop.Pages." + page + ".Items." + i, inv.getItem(i));
        }
        p.sendMessage(prefix + lang.getWithArgs("shop_display_gui_save_successful", name));
        Utils.saveData(name, "shops", shop);
        plugin.currentEditShop.remove(p.getUniqueId());
    }

    public static void setCashPriceWithChat(Player p, int slot, int page) {
        plugin.currentEditShopItem.put(p.getUniqueId(), Quadruple.of(plugin.currentEditShop.get(p.getUniqueId()), slot, CurrencyType.CASH, page));
        p.sendMessage(plugin.prefix + lang.get("amount_required"));
        p.closeInventory();
    }


    public static void setMileagePriceWithChat(Player p, int slot, int page) {
        plugin.currentEditShopItem.put(p.getUniqueId(), Quadruple.of(plugin.currentEditShop.get(p.getUniqueId()), slot, CurrencyType.MILEAGE, page));
        p.sendMessage(plugin.prefix + lang.get("amount_required"));
        p.closeInventory();
    }

    public static void setShopCashPrice(Player p, int slot, double cash, String name, int page) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Pages." + page + ".Items." + slot) != null) {
            shop.set("Shop.Pages." + page + ".Prices." + slot + ".cash", cash);
            Utils.saveData(name, "shops", shop);
            p.sendMessage(prefix + lang.getWithArgs("shop_cmd_cash_price_set_successful", String.valueOf(cash)));
        } else {
            p.sendMessage(prefix + lang.get("shop_display_no_item_in_slot"));
        }
    }

    public static void setShopMileagePrice(Player p, int slot, double mileage, String name, int page) {
        YamlConfiguration shop = plugin.shops.get(name);
        if (shop.getItemStack("Shop.Pages." + page + ".Items." + slot) != null) {
            shop.set("Shop.Pages." + page + ".Prices." + slot + ".mileage", mileage);
            Utils.saveData(name, "shops", shop);
            p.sendMessage(prefix + lang.getWithArgs("shop_cmd_mileage_price_set_successful", String.valueOf(mileage)));
        } else {
            p.sendMessage(prefix + lang.get("shop_display_no_item_in_slot"));
        }
    }

    public static void confirmBuyWithCash(Player p, ItemStack item, DInventory prevInv) {
        prevInvs.put(p.getUniqueId(), prevInv);
        DInventory inv = new DInventory(null, "§1구매 확인", 45, plugin);
        inv.setObj("confirmBuyWithCash");
        // fill inventory with glasspane with new itemStack
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paneMeta.setDisplayName("§f");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
        // make info paper itemstack
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paperMeta.setDisplayName("§f");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§f구매하려는 아이템의 가격은");
        lore.add("§e" + NBT.getDoubleTag(item, "cash") + "캐시 입니다.");
        lore.add("§c구매 금액이 계좌에 충분하지 않으면 구매에 실패합니다..");
        lore.add("§a구매 하시겠습니까?");
        lore.add("§6§l현재 보유한 캐시 : " + CashFunction.getCash(p));
        paperMeta.setLore(lore);
        paper.setItemMeta(paperMeta);
        inv.setItem(31, paper);
        // make yes itemstack
        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        yesMeta.setLore(Arrays.asList("§6구매하려면 클릭해주세요"));
        yesMeta.setDisplayName("§f§l구매하기");
        yes.setItemMeta(yesMeta);
        inv.setItem(19, NBT.setStringTag(yes, "dlc_yes", "true"));
        // make cancel itemstack
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        cancelMeta.setLore(Arrays.asList("§6취소하려면 클릭해주세요"));
        cancelMeta.setDisplayName("§f§l취소하기");
        cancel.setItemMeta(cancelMeta);
        inv.setItem(25, NBT.setStringTag(cancel, "dlc_cancel", "true"));
        inv.setItem(13, item);
        p.openInventory(inv);
    }

    // confirmBuyWithMileage
    public static void confirmBuyWithMileage(Player p, ItemStack item, DInventory prevInv) {
        prevInvs.put(p.getUniqueId(), prevInv);
        DInventory inv = new DInventory(null, "§1구매 확인", 45, plugin);
        inv.setObj("confirmBuyWithMileage");
        // fill inventory with glasspane with new itemStack
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paneMeta.setDisplayName("§f");
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
        // make info paper itemStack
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paperMeta.setDisplayName("§f");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§f구매하려는 아이템의 가격은");
        lore.add("§e" + NBT.getDoubleTag(item, "mileage") + "마일리지 입니다.");
        lore.add("§c구매 금액이 계좌에 충분하지 않으면 구매에 실패합니다..");
        lore.add("§a구매 하시겠습니까?");
        lore.add("§6§l현재 보유한 마일리지 : " + CashFunction.getMileage(p));
        paperMeta.setLore(lore);
        paper.setItemMeta(paperMeta);
        inv.setItem(31, paper);
        // make yes itemstack
        ItemStack yes = new ItemStack(Material.LIME_WOOL);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        yesMeta.setLore(Arrays.asList("§6구매하려면 클릭해주세요"));
        yesMeta.setDisplayName("§f§l구매하기");
        yes.setItemMeta(yesMeta);
        inv.setItem(19, NBT.setStringTag(yes, "dlc_yes", "true"));
        // make cancel itemstack
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        cancelMeta.setLore(Arrays.asList("§6취소하려면 클릭해주세요"));
        cancelMeta.setDisplayName("§f§l취소하기");
        cancel.setItemMeta(cancelMeta);
        inv.setItem(25, NBT.setStringTag(cancel, "dlc_cancel", "true"));
        inv.setItem(13, item);
        p.openInventory(inv);
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
        if (plugin.logger != null) {
            plugin.logger.log("아이템 구매 완료, 구매자 : " + p.getName() + ", 구매 가격 : " + cash + "캐시");
            plugin.logger.log(item);
        }
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
        if (plugin.logger != null) {
            plugin.logger.log("아이템 구매 완료, 구매자 : " + p.getName() + ", 구매 가격 : " + mileage + "마일리지");
            plugin.logger.log(item);
        }
    }

    public static void openShop(Player p, String name) {
        if (!plugin.shops.containsKey(name)) {
            p.sendMessage(prefix + lang.get("shop_is_not_exists"));
            return;
        }
        DInventory inv = new DInventory(null, getShopTitle(name), 54, true, plugin);
        Map<Integer/*page*/, Map<Integer/*slot*/, ItemStack>> items = new HashMap<>();
        YamlConfiguration shop = plugin.shops.get(name);
        int ic = 0;
        if (shop.getConfigurationSection("Shop.Pages") != null) {
            for (String page : shop.getConfigurationSection("Shop.Pages").getKeys(false)) {
                Map<Integer, ItemStack> pageItems = new HashMap<>();
                for (int i = 0; i < 54; i++) {
                    ItemStack item = shop.getItemStack("Shop.Pages." + page + ".Items." + i);
                    if (item == null) {
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_display")) {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("§f");
                        item.setItemMeta(im);
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_page")) {
                        String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "§a" + page + " 페이지";
                        displayName = displayName.replace("<currentPage>", String.valueOf(Integer.parseInt(page) + 1));
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(displayName);
                        item.setItemMeta(im);
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_prev")) {
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    if (NBT.hasTagKey(item, "dlc_next")) {
                        pageItems.put(ic, item.clone());
                        ic++;
                        continue;
                    }
                    ItemMeta im = item.getItemMeta();
                    List<String> lore = new ArrayList<>();
                    if (im.hasLore()) {
                        lore = im.getLore();
                    }
                    double cash = shop.getDouble("Shop.Pages." + page + ".Prices." + i + ".cash");
                    double mileage = shop.getDouble("Shop.Pages." + page + ".Prices." + i + ".mileage");
                    if (cash <= 0) {
                        item = NBT.setDoubleTag(item, "cash", -1);
                        lore.add(lang.get("shop_item_lore_cant_buy_with_cash"));
                    } else {
                        item = NBT.setDoubleTag(item, "cash", cash);
                        lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_cash", String.valueOf(cash)));
                    }
                    if (mileage <= 0) {
                        item = NBT.setDoubleTag(item, "mileage", -1);
                        lore.add(lang.get("shop_item_lore_cant_buy_with_mileage"));
                    } else {
                        item = NBT.setDoubleTag(item, "mileage", mileage);
                        lore.add(lang.getWithArgs("shop_item_lore_can_buy_with_mileage", String.valueOf(mileage)));
                    }
                    ItemStack r = item.clone();
                    ItemMeta rm = r.getItemMeta();
                    rm.setLore(lore);
                    r.setItemMeta(rm);
                    pageItems.put(ic, r.clone());
                    ic++;
                }
            }
        }
        int maxPages = shop.getInt("Shop.MaxPage");
        int count = 0;
        inv.setPages(maxPages);
        items.forEach((page, pageItems) -> {
            ItemStack[] contents = new ItemStack[54];
            for (int i = 0; i < 54; i++) {
                ItemStack item = pageItems.get(i);
                if (item != null) {
                    contents[i] = item;
                }
            }
            inv.setPageContent(page, contents);
            contents = new ItemStack[54];
        });
        inv.setUsePageTools(false);
        inv.update();
        p.openInventory(inv);
    }

    @Nullable
    public static String getShopTitle(String name) {
        if (!plugin.shops.containsKey(name)) {
            System.out.println("Shop is not exists");
            return null;
        }
        YamlConfiguration shop = plugin.shops.get(name);
        String title = shop.getString("Shop.Title");
        if (title == null) {
            return name + " - 타이틀 미설정";
        }
        return title;
    }

    public static void setShopTitle(Player p, String name, String... rawTitle) {
        // dont use lang
        if (!plugin.shops.containsKey(name)) {
            p.sendMessage(prefix + "§c해당 샵이 존재하지 않습니다.");
            return;
        }
        YamlConfiguration shop = plugin.shops.get(name);
        String title = Utils.getColoredText(rawTitle, 2);
        shop.set("Shop.Title", title);
        ShopConfigUtil.saveAllShop();
        p.sendMessage(prefix + name + "캐시상점의 타이틀을 " + title + "§f(으)로 설정하였습니다.");
    }

    public static void givePageTools(Player p) {
        ItemStack prev = new ItemStack(Material.PAPER);
        ItemMeta prevm = prev.getItemMeta();
        prevm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        prevm.setDisplayName("§a이전 페이지");
        prev.setItemMeta(prevm);
        p.getInventory().addItem(NBT.setStringTag(prev, "dlc_prev", "true"));
        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nextm = next.getItemMeta();
        nextm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nextm.setDisplayName("§b다음 페이지");
        next.setItemMeta(nextm);
        p.getInventory().addItem(NBT.setStringTag(next, "dlc_next", "true"));
        ItemStack page = new ItemStack(Material.PAPER);
        ItemMeta pagem = page.getItemMeta();
        pagem.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pagem.setDisplayName("§e현재 페이지: §a<currentPage>");
        page.setItemMeta(pagem);
        p.getInventory().addItem(NBT.setStringTag(page, "dlc_page", "true"));
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassm = glass.getItemMeta();
        glassm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        glassm.setDisplayName("§f");
        glass.setItemMeta(glassm);
        glass = NBT.setStringTag(glass, "dlc_display", "true");
        p.getInventory().addItem(glass);
    }

    public static void setMaxPages(Player p, String name, String spage) {
        if (!plugin.shops.containsKey(name)) {
            p.sendMessage(prefix + lang.get("shop_is_not_exists"));
            return;
        }
        // check page is integer
        try {
            int page = Integer.parseInt(spage);
            if (page < 0) {
                p.sendMessage(prefix + "페이지는 0 이상의 정수여야 합니다.");
                return;
            }
            YamlConfiguration shop = plugin.shops.get(name);
            shop.set("Shop.MaxPage", page);
            ShopConfigUtil.saveAllShop();
            p.sendMessage(prefix + name + "캐시상점의 최대 페이지를 " + page + "페이지로 설정하였습니다.");
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + "페이지 값이 정수가 아닙니다.");
            return;
        }
    }
}