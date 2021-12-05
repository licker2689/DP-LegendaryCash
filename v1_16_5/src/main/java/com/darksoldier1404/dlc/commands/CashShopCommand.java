package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashShopFunction;
import com.darksoldier1404.dlc.utils.ShopConfigUtil;
import com.darksoldier1404.dlc.utils.Utils;
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
    private final String prefix = LegendaryCash.getInstance().prefix;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§c플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(prefix + "/캐시상점 오픈 <상점이름> - 해당 캐시 상점을 오픈합니다.");
            p.sendMessage(prefix + "/캐시상점 목록 - 모든 캐시 상점의 목록을 표시합니다.");
            if (p.isOp()) {
                p.sendMessage(prefix + "/캐시상점 생성 <상점이름> - 캐시 상점을 생성합니다.");
                p.sendMessage(prefix + "/캐시상점 진열 <상점이름> - 캐시 상점에 아이템을 진열하기 위한 창을 엽니다.");
                p.sendMessage(prefix + "/캐시상점 가격 <상점이름> <C/M> <슬롯> <가격> - 해당 캐시상점의 해당 슬롯의 아이템의 가격을 설정합니다.");
                p.sendMessage(prefix + "* 가격을 -1원으로 설정하면 구매 불가로 설정됩니다.");
                p.sendMessage(prefix + "/캐시상점 삭제 <상점이름> - 캐시 상점을 삭제합니다.");
                p.sendMessage(prefix + "/캐시상점 리로드/rl - 콘피그 파일과 상점 파일을 리로드 합니다.");
                return false;
            }
        }

        if (args[0].equals("오픈")) {
            if (args.length == 1) {
                p.sendMessage(prefix + "상점 이름을 입력해주세요!");
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
                    p.sendMessage(prefix + "상점 이름을 입력해주세요!");
                    return false;
                }
                if (ShopConfigUtil.createShop(args[1])) {
                    p.sendMessage(prefix + args[1] + " 캐시상점이 생성되었습니다.");
                } else {
                    p.sendMessage(prefix + args[1] + "캐시상점은 이미 존재합니다.");
                }
                return false;
            }
            if (args[0].equals("진열")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "상점 이름을 입력해주세요!");
                    return false;
                }
                CashShopFunction.openShopShowCase(p, args[1]);
                return false;
            }
            if (args[0].equals("가격")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "상점 이름을 입력해주세요!");
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + "설정할 가격 종류를 입력해주세요. C/M");
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + "슬롯의 번호를 입력해주세요. 0~53");
                    return false;
                }
                if (args.length == 4) {
                    p.sendMessage(prefix + "설정할 가격을 입력해주세요.");
                    return false;
                }
                //p.sendMessage(prefix + "/캐시상점 가격 <상점이름> <C/M> <슬롯> <가격> - 해당 캐시상점의 해당 슬롯의 아이템의 가격을 설정합니다.");
                if (!LegendaryCash.getInstance().shops.containsKey(args[1])) {
                    p.sendMessage(prefix + "존재하지 않는 캐시상점 입니다.");
                } else {
                    if (args[2].equalsIgnoreCase("c")) {
                        try {
                            int slot = Integer.parseInt(args[3]);
                            try {
                                double price = Double.parseDouble(args[4]);
                                CashShopFunction.setShopCashPrice(p, slot, price, args[1]);
                                return false;
                            } catch (Exception e) {
                                p.sendMessage(prefix + "옳바른 가격을 입력해주세요.");
                                return false;
                            }
                        } catch (Exception e) {
                            p.sendMessage(prefix + "옳바른 슬롯 번호를 입력해주세요.");
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
                                p.sendMessage(prefix + "옳바른 가격을 입력해주세요.");
                                return false;
                            }
                        } catch (Exception e) {
                            p.sendMessage(prefix + "옳바른 슬롯 번호를 입력해주세요.");
                            return false;
                        }
                    }
                    p.sendMessage(prefix + "가격 종류를 제대로 입력해주세요.");
                    return false;
                }
            }
            if (args[0].equals("삭제")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "상점 이름을 입력해주세요!");
                    return false;
                }
                if (ShopConfigUtil.deleteShop(args[1])) {
                    p.sendMessage(prefix + args[1] + "캐시상점이 삭제되었습니다.");
                } else {
                    p.sendMessage(prefix + args[1] + "캐시상점은 존재하지 않습니다.");
                    return false;
                }
            }
            if (args[0].equals("리로드") || args[0].equals("rl")) {
                Utils.reloadConfig();
                ShopConfigUtil.loadAllShop();
                p.sendMessage(prefix + "콘피그 파일과 캐시상점 파일을 리로드 하였습니다.");
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
