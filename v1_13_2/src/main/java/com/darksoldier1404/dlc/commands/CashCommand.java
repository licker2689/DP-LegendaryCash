package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dlc.utils.Utils;
import com.darksoldier1404.dppc.lang.DLang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CashCommand implements CommandExecutor, TabCompleter {
    private final LegendaryCash plugin = LegendaryCash.getInstance();
    private final String prefix = LegendaryCash.getInstance().prefix;
    private final DLang lang = plugin.lang;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "§c플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(prefix + lang.get("cash_cmd_balance"));
            p.sendMessage(prefix + lang.get("cash_cmd_balance_others"));
            p.sendMessage(prefix + lang.get("cash_cmd_balance_public"));
            p.sendMessage(prefix + lang.get("cash_cmd_transfer"));
            p.sendMessage(prefix + lang.get("cash_cmd_check"));
            if (p.isOp()) {
                p.sendMessage(prefix + lang.get("cash_cmd_admin_give"));
                p.sendMessage(prefix + lang.get("cash_cmd_admin_take"));
                p.sendMessage(prefix + lang.get("cash_cmd_admin_set"));
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("확인")) {
            if (args.length == 1) {
                p.sendMessage(prefix + plugin.dphm.getPlaceholder("cash").applyAsPlayer(lang.get("balance_cmd_balance_cash"), p));
                p.sendMessage(prefix + plugin.dphm.getPlaceholder("mileage").applyAsPlayer(lang.get("balance_cmd_balance_mileage"), p));
                return false;
            }
            if (args.length == 2) {
                Player target = p.getServer().getPlayer(args[1]);
                if (target == null) {
                    p.sendMessage(prefix + lang.get("target_player_is_offline"));
                    return false;
                }
                if (!CashFunction.isOpen(target) && !p.isOp()) {
                    p.sendMessage(prefix + lang.get("balance_cmd_others_deny"));
                    return false;
                }
                p.sendMessage(prefix + plugin.dphm.getPlaceholder("cash").applyAsPlayer(lang.get("balance_cmd_others_cash"), target));
                p.sendMessage(prefix + plugin.dphm.getPlaceholder("mileage").applyAsPlayer(lang.get("balance_cmd_others_mileage"), target));
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("공개")) {
            if (args.length == 1) {
                if (CashFunction.isOpen(p)) {
                    CashFunction.setOpen(p, false);
                    p.sendMessage(prefix + lang.get("public_cmd_public_off"));
                } else {
                    CashFunction.setOpen(p, true);
                    p.sendMessage(prefix + lang.get("public_cmd_public_on"));
                }
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("송금")) {
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + lang.get("target_player_name_required"));
                return false;
            }
            if (args.length == 3) {
                p.sendMessage(prefix + lang.get("amount_required"));
                return false;
            }
            if (args[1].equalsIgnoreCase("c")) {
                CashFunction.sendCash(p, Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                return false;
            }
            if (args[1].equalsIgnoreCase("m")) {
                CashFunction.sendMileage(p, Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("수표")) {
            if (args.length == 1) {
                p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + lang.get("amount_required"));
                return false;
            }
            if (args.length == 3) {
                if(args[1].equalsIgnoreCase("c")) {
                    if(CashFunction.canUseCashCheck()) {
                        CashFunction.getCashCheck(p, Double.parseDouble(args[2]), 1);
                    }else{
                        p.sendMessage(prefix + lang.get("check_cmd_cant_use"));
                    }
                    return false;
                }
                if(args[1].equalsIgnoreCase("m")) {
                    if(CashFunction.canUseMileageCheck()) {
                        CashFunction.getMileageCheck(p, Double.parseDouble(args[2]), 1);
                    }else{
                        p.sendMessage(prefix + lang.get("check_cmd_cant_use"));
                    }
                    return false;
                }
                p.sendMessage(prefix + lang.get("command_is_not_valid"));
                return false;
            }
            if (args.length == 4) {
                if(args[1].equalsIgnoreCase("c")) {
                    if(CashFunction.canUseCashCheck()) {
                        CashFunction.getCashCheck(p, Double.parseDouble(args[2]), Integer.parseInt(args[3]));
                    }else{
                        p.sendMessage(prefix + lang.get("check_cmd_cant_use"));
                    }
                    return false;
                }
                if(args[1].equalsIgnoreCase("m")) {
                    if(CashFunction.canUseMileageCheck()) {
                        CashFunction.getMileageCheck(p, Double.parseDouble(args[2]), Integer.parseInt(args[3]));
                    }else{
                        p.sendMessage(prefix + lang.get("check_cmd_cant_use"));
                    }
                    return false;
                }
                p.sendMessage(prefix + lang.get("command_is_not_valid"));
                return false;
            }
        }
        if (p.isOp()) {
            if (args[0].equals("주기")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + lang.get("target_player_name_required"));
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + lang.get("amount_required"));
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + lang.get("command_is_not_valid"));
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + lang.getWithArgs("give_cmd_cash_successful", args[2], args[3]));
                    CashFunction.addCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + lang.getWithArgs("give_cmd_mileage_successful", args[2], args[3]));
                    CashFunction.addMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("빼기")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + lang.get("target_player_name_required"));
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + lang.get("amount_required"));
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + lang.get("command_is_not_valid"));
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + lang.getWithArgs("take_cmd_cash_successful", args[2], args[3]));
                    CashFunction.takeCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + lang.getWithArgs("take_cmd_mileage_successful", args[2], args[3]));
                    CashFunction.takeMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("설정")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + lang.get("insert_type_of_fund"));
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + lang.get("target_player_name_required"));
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + lang.get("amount_required"));
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + lang.get("command_is_not_valid"));
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + lang.getWithArgs("set_cmd_cash_successful", args[2], args[3]));
                    CashFunction.setCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + lang.getWithArgs("set_cmd_mileage_successful", args[2], args[3]));
                    CashFunction.setMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("리로드")) {
                Utils.reloadConfig();
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
                return Arrays.asList("주기", "빼기", "설정", "확인", "공개", "송금", "수표", "리로드");
            }
            return Arrays.asList("확인", "공개", "송금", "수표");
        }
        if (args[0].equals("송금") || args[0].equals("주기") || args[0].equals("빼기") || args[0].equals("설정") || args[0].equals("수표")) {
            if (args.length == 2) {
                return Arrays.asList("C", "M");
            }
            if (args.length == 3) {
                List<String> names = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(o -> names.add(o.getName()));
                return names;
            }
        }
        if (args[0].equals("확인")) {
            if (args.length == 2) {
                List<String> names = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(o -> names.add(o.getName()));
                return names;
            }
        }
        return null;
    }
}
