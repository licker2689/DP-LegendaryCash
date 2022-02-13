package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dppc.lang.DLang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CashShopCommand implements CommandExecutor, TabCompleter {
    private final LegendaryCash plugin = LegendaryCash.getInstance();
    private final String prefix = plugin.prefix;
    private final DLang lang = plugin.lang;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§c플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(prefix + lang.get("shop_cmd_open"));
            p.sendMessage(prefix + lang.get("shop_cmd_list"));
            if (p.isOp()) {
                p.sendMessage(prefix + lang.get("shop_cmd_create"));
                p.sendMessage(prefix + lang.get("shop_cmd_display"));
                p.sendMessage(prefix + lang.get("shop_cmd_price"));
                p.sendMessage(prefix + lang.get("shop_cmd_price_info"));
                p.sendMessage(prefix + lang.get("shop_cmd_delete"));
                p.sendMessage(prefix + lang.get("shop_cmd_reload"));
                return false;
            }
        }

        if (args[0].equals("오픈")) {
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            CashShopFunction.openShop(p, args[1]);
            return false;
        }
        if (args[0].equals("목록")) {
            LegendaryCash.getInstance().shops.keySet().forEach(s -> p.sendMessage(prefix + s));
            return false;
        }
        if (p.isOp()) {
            if (args[0].equals("생성")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("shop_name_required"));
                    return false;
                }
                if (ShopConfigUtil.createShop(args[1])) {
                    p.sendMessage(prefix + lang.getWithArgs("shop_create_successful", args[1]));
                } else {
                    p.sendMessage(prefix + lang.get("shop_is_already_exists"));
                }
                return false;
            }
            if (args[0].equals("진열")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("shop_name_required"));
                    return false;
                }
                CashShopFunction.openShopShowCase(p, args[1]);
                return false;
            }
            if (args[0].equals("가격")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("shop_name_required"));
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + lang.get("shop_create_slot_required"));
                    return false;
                }
                if (args.length == 4) {
                    p.sendMessage(prefix + lang.get("amount_required"));
                    return false;
                }
                //p.sendMessage(prefix + "/캐시상점 가격 <상점이름> <C/M> <슬롯> <가격> - 해당 캐시상점의 해당 슬롯의 아이템의 가격을 설정합니다.");
                if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                    p.sendMessage(prefix + lang.get("shop_is_not_exists"));
                } else {
                    if (args[2].equalsIgnoreCase("c")) {
                        try {
                            int slot = Integer.parseInt(args[3]);
                            try {
                                double price = Double.parseDouble(args[4]);
                                CashShopFunction.setShopCashPrice(p, slot, price, args[1]);
                                return false;
                            } catch (Exception e) {
                                p.sendMessage(prefix + lang.get("amount_is_not_number"));
                                return false;
                            }
                        } catch (Exception e) {
                            p.sendMessage(prefix + lang.get("shop_slot_is_not_valid"));
                            return false;
                        }
                    }
                    if (args[2].equalsIgnoreCase("m")) {
                        try {
                            int slot = Integer.parseInt(args[3]);
                            try {
                                double price = Double.parseDouble(args[4]);
                                CashShopFunction.setShopMileagePrice(p, slot, price, args[1]);
                                return false;
                            } catch (Exception e) {
                                p.sendMessage(prefix + lang.get("amount_is_not_number"));
                                return false;
                            }
                        } catch (Exception e) {
                            p.sendMessage(prefix + lang.get("shop_slot_is_not_valid"));
                            return false;
                        }
                    }
                    p.sendMessage(prefix + lang.get("type_of_fund_is_not_valid"));
                    return false;
                }
            }
            if (args[0].equals("삭제")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("shop_name_required"));
                    return false;
                }
                if (ShopConfigUtil.deleteShop(args[1])) {
                    p.sendMessage(prefix + lang.getWithArgs("shop_delete_successful", args[1]));
                } else {
                    p.sendMessage(prefix + lang.get("shop_is_not_exists"));
                    return false;
                }
            }
            if (args[0].equals("리로드") || args[0].equals("rl")) {
                Utils.reloadConfig();
                ShopConfigUtil.loadAllShop();
                p.sendMessage(prefix + lang.get("reload_cmd_successful"));
                return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList("생성", "진열", "가격", "삭제", "목록", "오픈", "리로드");
            }
            return Arrays.asList("오픈", "목록");
        }
        if (args.length == 2) {
            if (!args[0].equals("목록") && !args[0].equals("리로드")) {
                return LegendaryCash.getInstance().shops.keySet().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            }
        }
        return null;
    }
}
