package com.darksoldier1404.dlc.events;

import com.darksoldier1404.dlc.LegendaryCash;
import com.darksoldier1404.dlc.functions.CashFunction;
import com.darksoldier1404.dppc.api.benta.etc.EvtCulturelandFail;
import com.darksoldier1404.dppc.api.benta.etc.EvtCulturelandSuccess;
import com.darksoldier1404.dppc.api.benta.etc.EvtDepositSuccess;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

@SuppressWarnings("all")
public class BentaEvent implements Listener {
    private final LegendaryCash plugin = LegendaryCash.getInstance();
    @EventHandler
    public void onCultuerlandFail(EvtCulturelandFail e) {
        OfflinePlayer op = Bukkit.getPlayer(UUID.fromString(e.getIdentifier()));
        if(op != null) {
            if(op.isOnline()) {
                Player p = Bukkit.getPlayer(op.getUniqueId());
                p.sendMessage(plugin.prefix + "§c컬처랜드 결제에 실패했습니다.");
                if(plugin.logger != null) {
                    plugin.logger.log(p.getName() + "님이 컬처랜드 결제에 실패했습니다.");
                }
            }
        }
    }

    @EventHandler
    public void onCultuerlandSuccess(EvtCulturelandSuccess e) {
        OfflinePlayer op = Bukkit.getPlayer(UUID.fromString(e.getIdentifier()));
        if(op != null) {
            if(op.isOnline()) {
                Player p = Bukkit.getPlayer(op.getUniqueId());
                p.sendMessage(plugin.prefix + "§a컬처랜드 결제에 성공했습니다.");
                p.sendMessage(plugin.prefix + "§a금액 : " + e.getPaidAmount() + " 원");
                CashFunction.addCash(p, e.getPaidAmount());
                if(plugin.logger != null) {
                    plugin.logger.log(p.getName() + "님이 컬처랜드 결제에 성공했습니다.");
                }
            }else{
                CashFunction.addCash(op, e.getPaidAmount());
                if (plugin.logger != null) {
                    plugin.logger.log(op.getName() + "님이 컬처랜드 결제에 성공했습니다. (유저 오프라인)");
                }
            }
        }
    }

    @EventHandler
    public void onDepositSuccess(EvtDepositSuccess e) {
        OfflinePlayer op = Bukkit.getPlayer(UUID.fromString(e.getIdentifier()));
        if(op != null) {
            if(op.isOnline()) {
                Player p = Bukkit.getPlayer(op.getUniqueId());
                p.sendMessage(plugin.prefix + "§a입금에 성공했습니다.");
                p.sendMessage(plugin.prefix + "§a금액 : " + e.getPaidAmount() + " 원");
                CashFunction.addCash(p, e.getPaidAmount());
                if(plugin.logger != null) {
                    plugin.logger.log(p.getName() + "님이 입금에 성공했습니다.");
                }
            }else{
                CashFunction.addCash(op, e.getPaidAmount());
                if (plugin.logger != null) {
                    plugin.logger.log(op.getName() + "님이 입금에 성공했습니다. (유저 오프라인)");
                }
            }
        }
    }
}
