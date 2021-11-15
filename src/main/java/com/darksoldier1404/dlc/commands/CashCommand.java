package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
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
    private final String prefix = LegendaryCash.getInstance().prefix;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(prefix + "§c플레이어만 사용 가능한 명령어 입니다.");
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(prefix + "§a/캐시 확인 - 자신이 보유중인 캐시와 마일리지를 확인합니다.");
            p.sendMessage(prefix + "§a/캐시 확인 <닉네임> - 다른 유저의 캐시와 마일리지를 확인합니다. (상대가 보기를 허용중일 경우)");
            p.sendMessage(prefix + "§a/캐시 공개 - 자신의 캐시와 마일리지의 공개 여부를 설정합니다. (토글방식)");
            p.sendMessage(prefix + "§a/캐시 송금 C/M <닉네임> <금액> - 상대방에게 자신의 캐시 또는 마일리지를 송금합니다.");
            return false;
        }
        if (args[0].equalsIgnoreCase("확인")) {
            if (args.length == 1) {
                p.sendMessage(prefix + "보유 캐시 : " + CashFunction.getCash(p));
                p.sendMessage(prefix + "보유 마일리지 : " + CashFunction.getMileage(p));
                return false;
            }
            if (args.length == 2) {
                Player target = p.getServer().getPlayer(args[1]);
                if (target == null) {
                    p.sendMessage(prefix + "§c존재하지 않거나 오프라인 플레이어입니다.");
                    return false;
                }
                if (!CashFunction.isOpen(target)) {
                    p.sendMessage(prefix + "§c" + target.getName() + "님은 캐시 공개가 비활성화 되어 있습니다.");
                    return false;
                }
                p.sendMessage(prefix + "§a" + target.getName() + "님의 보유 캐시 : " + CashFunction.getCash(target));
                p.sendMessage(prefix + "§a" + target.getName() + "님의 보유 마일리지 : " + CashFunction.getMileage(target));
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("공개")) {
            if (args.length == 1) {
                if (CashFunction.isOpen(p)) {
                    CashFunction.setOpen(p, false);
                    p.sendMessage(prefix + "§c캐시 공개가 비활성화 되었습니다.");
                } else {
                    CashFunction.setOpen(p, true);
                    p.sendMessage(prefix + "§a캐시 공개가 활성화 되었습니다.");
                }
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("송금")) {
            if (args.length == 1) {
                p.sendMessage(prefix + "§c사용법 : /캐시 송금 <C/M> <닉네임> <금액>");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + "송금할 자금 종류를 입력해주세요! <C/M>");
                return false;
            }
            if(args.length == 3) {
                p.sendMessage(prefix + "송금할 대상의 닉네임을 입력해주세요!");
                return false;
            }
            if(args.length == 4) {
                p.sendMessage(prefix + "송금할 금액을 입력해주세요!");
                return false;
            }
            if(args[1].equalsIgnoreCase("c")) {
                CashFunction.sendCash(p, Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                return false;
            }
            if(args[1].equalsIgnoreCase("m")) {
                CashFunction.sendMileage(p, Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                return false;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return Arrays.asList("확인", "공개", "송금");
        }
        if(args[0].equals("송금")) {
            if(args.length == 2) {
                return Arrays.asList("C", "M");
            }
            if(args.length == 3) {
                List<String> names = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(o -> names.add(o.getName()));
                return names;
            }
        }
        if(args[0].equals("확인")) {
            if(args.length == 2) {
                List<String> names = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(o -> names.add(o.getName()));
                return names;
            }
        }
        return null;
    }
}
