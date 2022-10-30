package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
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
                p.sendMessage(prefix + "/캐시상점 진열 <상점이름> <페이지> - 캐시 상점에 아이템을 진열하기 위한 창을 엽니다.");
                p.sendMessage(prefix + "/캐시상점 가격 <상점이름> - 해당 캐시상점의 가격 설정창을 오픈합니다. (좌클 = 캐시, 우클 = 마일리지)");
                p.sendMessage(prefix + lang.get("shop_cmd_price_info"));
                p.sendMessage(prefix + lang.get("shop_cmd_delete"));
                p.sendMessage(prefix + lang.get("shop_cmd_reload"));
                p.sendMessage(prefix + "/캐시상점 타이틀 <상점이름> <커스텀 타이틀> - 해당 상점의 타이틀을 설정합니다.");
                p.sendMessage(prefix + "/캐시상점 최대페이지 <상점이름> <0~?> - 해당 상점의 최대 페이지를 설정합니다.");
                p.sendMessage(prefix + "페이지는 0부터 시작합니다. (미설정시 0페이지)");
                p.sendMessage(prefix + "/캐시상점 페이지툴 - 페이지툴 아이템을 받습니다.");
                p.sendMessage(prefix + "해당 아이템들은 진열 명령어를 통하여 원하는 위치에 진열하셔서 디자인 하시면 됩니다.");
                p.sendMessage(prefix + "아이템 편집 플러그인으로 페이지툴을 원하시는데로 디자인 하시면 됩니다.");
                p.sendMessage(prefix + "플레이스 홀더 : <currentPage>, <maxPage>, <currentCash>, <currentMileage>");
                p.sendMessage(prefix + "제공되는 유리판은 클릭해도 아무런 반응을 안하는 아이템입니다.");
            }
            return false;
        }
        if (args[0].equals("오픈")) {
            if (!p.hasPermission("dlc.shop.open")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            CashShopFunction.openShop(p, args[1]);
            return false;
        }
        if (args[0].equals("목록")) {
            if (!p.hasPermission("dlc.shop.list")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (LegendaryCash.getInstance().shops.isEmpty()) {
                p.sendMessage(prefix + lang.get("상점이 존재하지 않습니다."));
                return false;
            }
            LegendaryCash.getInstance().shops.keySet().forEach(s -> p.sendMessage(prefix + s));
            return false;
        }
        if (args[0].equals("생성")) {
            if (!p.hasPermission("dlc.shop.create")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
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
            if (!p.hasPermission("dlc.shop.display")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            if(args.length == 2) {
                p.sendMessage(prefix + "진열할 페이지를 입력해주세요.");
                return false;
            }
            if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                p.sendMessage(prefix + lang.get("shop_is_not_exists"));
                return false;
            }
            CashShopFunction.openShopShowCase(p, args[1], args[2]);
            return false;
        }
        if (args[0].equals("타이틀")) {
            if (!p.hasPermission("dlc.shop.setTitle")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + "타이틀을 입력해주세요.");
                return false;
            }
            if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                p.sendMessage(prefix + lang.get("shop_is_not_exists"));
                return false;
            }
            CashShopFunction.setShopTitle(p, args[1], args);
            return false;
        }
        if(args[0].equals("최대페이지")) {
            if (!p.hasPermission("dlc.shop.setMaxPage")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            if(args.length == 2) {
                p.sendMessage(prefix + "최대 페이지를 입력해주세요.");
                return false;
            }
            if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                p.sendMessage(prefix + lang.get("shop_is_not_exists"));
                return false;
            }
            CashShopFunction.setMaxPages(p, args[1], args[2]);
            return false;
        }
        if(args[0].equals("페이지툴")) {
            if (!p.hasPermission("dlc.shop.getPageTool")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            CashShopFunction.givePageTools(p);
            return false;
        }
        if (args[0].equals("가격")) {
            if (!p.hasPermission("dlc.shop.price")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("shop_name_required"));
                return false;
            }
            if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                p.sendMessage(prefix + lang.get("shop_is_not_exists"));
            } else {
                CashShopFunction.openShopPriceSetting(p, args[1], 0);
                return false;
            }
        }
        if (args[0].equals("삭제")) {
            if (!p.hasPermission("dlc.shop.delete")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
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
            if (!p.hasPermission("dlc.shop.reload")) {
                p.sendMessage(prefix + "권한이 없습니다.");
                return false;
            }
            Utils.reloadConfig();
            ShopConfigUtil.loadAllShop();
            p.sendMessage(prefix + lang.get("reload_cmd_successful"));
            return false;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList("생성", "진열", "가격", "삭제", "목록", "오픈", "리로드", "타이틀", "최대페이지", "페이지툴");
            }
            return Arrays.asList("오픈", "목록");
        }
        if (args.length == 2) {
            if (!args[0].equals("목록") && !args[0].equals("리로드")) {
                return LegendaryCash.getInstance().shops.keySet().stream().collect(Collectors.toList());
            }
        }
        return null;
    }
}
