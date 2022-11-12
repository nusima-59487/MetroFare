package com.hasunemiku2015.metrofare.Gate.Train;

import com.hasunemiku2015.metrofare.Company.CompanyStore;
import com.hasunemiku2015.metrofare.Gate.People.GateExecutionIn;
import com.hasunemiku2015.metrofare.Gate.People.GateExecutionOut;
import com.hasunemiku2015.metrofare.Gate.People.GateUtil;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import me.hasunemiku2015.mikucore.Vehicle.MinecartSignEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Clearance implements Listener {

    @EventHandler
    public void onClearanceSignBuild(SignChangeEvent event) {
        if (!"[MetroFareValidator]".equalsIgnoreCase(event.getLine(0))) {
            return;
        }
        if (!MFConfig.INSTANCE.hasBuildGatePermission(event.getPlayer())) {
            event.getBlock().setType(Material.AIR);
            return;
        }
        if (event.getLine(2) == null || event.getLine(3) == null) {
            event.getBlock().setType(Material.AIR);
            return;
        }

        String[] infoOut = GateUtil.parseData(Objects.requireNonNull(event.getLine(2)));
        String[] infoIn = GateUtil.parseData(Objects.requireNonNull(event.getLine(3)));
        if (!CompanyStore.CompanyTable.containsKey(infoOut[0]) || !CompanyStore.CompanyTable.containsKey(infoIn[0])) {
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onClearance(MinecartSignEvent event) {
        if (!event.getHeader().equalsIgnoreCase(MFConfig.INSTANCE.getValidatorVanillaPrefix())) return;
        if (!(event.getCart().getPassengers().get(0) instanceof Player)) return;

        Player player = (Player) event.getCart().getPassengers().get(0);
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (stack == null) {
                if (i == 35) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
                    break;
                } else {
                    continue;
                }
            }
            if (!stack.getType().equals(Material.NAME_TAG)) {
                if (i != 35) {
                    continue;
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
                    break;
                }
            }

            boolean done0 = GateExecutionOut.DCExitLogic(player, CompanyStore.CompanyTable.get(GateUtil.parseData(event.getLine(2))[0]), stack, event.getLine(2));
            boolean done1 = false;
            if (done0) {
                done1 = GateExecutionIn.DCEntryLogic(player, stack, event.getLine(3), true);
            }

            if (done0 && done1) {
                Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN, () -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getValidatorComplete())), 10);
                break;
            } else if (i == 35) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
            }
        }
    }
}
