package com.darksoldier1404.dlc.commands;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dlc.utils.Utils;
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
            p.sendMessage(prefix + "§a/캐시 수표 C/M <금액> (수량) - 캐시 또는 마일리지를 수표로 만듭니다.");
            if (p.isOp()) {
                p.sendMessage(prefix + "§a/캐시 주기 C/M <닉네임> <금액> - 해당 플레이어에게 캐시 또는 마일리지를 줍니다.");
                p.sendMessage(prefix + "§a/캐시 빼기 C/M <닉네임> <금액> - 해당 플레이어의 캐시 또는 마일리지를 가져옵니다.");
                p.sendMessage(prefix + "§a/캐시 설정 C/M <닉네임> <금액> - 해당 플레이어의 캐시 또는 마일리지를 설정합니다.");
            }
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
                p.sendMessage(prefix + "송금할 자금 종류를 입력해주세요! <C/M>");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + "송금할 대상의 닉네임을 입력해주세요!");
                return false;
            }
            if (args.length == 3) {
                p.sendMessage(prefix + "송금할 금액을 입력해주세요!");
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
                p.sendMessage(prefix + "자금 종류를 입력해주세요! <C/M>");
                return false;
            }
            if (args.length == 2) {
                p.sendMessage(prefix + "금액을 입력해주세요!");
                return false;
            }
            if (args.length == 3) {
                if(args[1].equalsIgnoreCase("c")) {
                    if(CashFunction.canUseCashCheck()) {
                        CashFunction.getCashCheck(p, Double.parseDouble(args[2]), 1);
                    }else{
                        p.sendMessage(prefix + "수표 기능을 사용할 수 없습니다.");
                    }
                    return false;
                }
                if(args[1].equalsIgnoreCase("m")) {
                    if(CashFunction.canUseMileageCheck()) {
                        CashFunction.getMileageCheck(p, Double.parseDouble(args[2]), 1);
                    }else{
                        p.sendMessage(prefix + "수표 기능을 사용할 수 없습니다.");
                    }
                    return false;
                }
                p.sendMessage(prefix + "명령어가 옳바르지 않습니다.");
                return false;
            }
            if (args.length == 4) {
                if(args[1].equalsIgnoreCase("c")) {
                    if(CashFunction.canUseCashCheck()) {
                        CashFunction.getCashCheck(p, Double.parseDouble(args[2]), Integer.parseInt(args[3]));
                    }else{
                        p.sendMessage(prefix + "수표 기능을 사용할 수 없습니다.");
                    }
                    return false;
                }
                if(args[1].equalsIgnoreCase("m")) {
                    if(CashFunction.canUseMileageCheck()) {
                        CashFunction.getMileageCheck(p, Double.parseDouble(args[2]), Integer.parseInt(args[3]));
                    }else{
                        p.sendMessage(prefix + "수표 기능을 사용할 수 없습니다.");
                    }
                    return false;
                }
                p.sendMessage(prefix + "명령어가 옳바르지 않습니다.");
                return false;
            }
        }
        if (p.isOp()) {
            if (args[0].equals("주기")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "지급할 자금 종류를 입력해주세요! <C/M>");
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + "지급할 대상의 닉네임을 입력해주세요!");
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + "지급할 금액을 입력해주세요!");
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + "명령어를 다시 확인해주시기 바랍니다.");
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + args[2] + "에게 " + args[3] + "만큼의 캐시를 지급합니다.");
                    CashFunction.addCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + args[2] + "에게 " + args[3] + "만큼의 마일리지를 지급합니다.");
                    CashFunction.addMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("빼기")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "회수할 자금 종류를 입력해주세요! <C/M>");
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + "회수할 대상의 닉네임을 입력해주세요!");
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + "회수할 금액을 입력해주세요!");
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + "명령어를 다시 확인해주시기 바랍니다.");
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + args[2] + "의 " + args[3] + "만큼의 캐시를 회수합니다.");
                    CashFunction.takeCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + args[2] + "의 " + args[3] + "만큼의 마일리지를 회수합니다.");
                    CashFunction.takeMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("설정")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "설정할 자금 종류를 입력해주세요! <C/M>");
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + "설정할 대상의 닉네임을 입력해주세요!");
                    return false;
                }
                if (args.length == 3) {
                    p.sendMessage(prefix + "설정할 금액을 입력해주세요!");
                    return false;
                }
                try {
                    Bukkit.getPlayer(args[2]);
                    Double.parseDouble(args[3]);
                } catch (Exception e) {
                    p.sendMessage(prefix + "명령어를 다시 확인해주시기 바랍니다.");
                    return false;
                }
                if (args[1].equalsIgnoreCase("c")) {
                    p.sendMessage(prefix + args[2] + "의 " + args[3] + "만큼의 캐시를 설정합니다.");
                    CashFunction.setCash(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
                if (args[1].equalsIgnoreCase("m")) {
                    p.sendMessage(prefix + args[2] + "의 " + args[3] + "만큼의 마일리지를 설정합니다.");
                    CashFunction.setMileage(Bukkit.getPlayer(args[2]), Double.parseDouble(args[3]));
                    return false;
                }
            }
            if (args[0].equals("리로드")) {
                Utils.reloadConfig();
                p.sendMessage(prefix + "콘피그 파일을 리로드하였습니다.");
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
