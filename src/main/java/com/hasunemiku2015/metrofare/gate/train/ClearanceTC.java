package com.hasunemiku2015.metrofare.gate.train;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.gate.people.GateExecutionIn;
import com.hasunemiku2015.metrofare.gate.people.GateExecutionOut;
import com.hasunemiku2015.metrofare.gate.people.GateUtil;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearanceTC extends SignAction {
    @Override
    public boolean match(SignActionEvent info) {
        return info.isType(MFConfig.INSTANCE.getValidatorTrainCartsPrefix());
    }

    @Override
    public void execute(SignActionEvent event) {
        if(event.isAction(SignActionType.GROUP_ENTER,SignActionType.REDSTONE_CHANGE) && event.isPowered()){
            if(event.hasGroup()){
                for(MinecartMember<?> mem : event.getMembers()){
                    if(mem.getEntity().getPassengers().size() == 0) return;
                    if(mem.getEntity().getPassengers().get(0) instanceof Player){
                        Player player = (Player) mem.getEntity().getPassengers().get(0);
                        for(int i= 0; i<= 35; i++){
                            ItemStack stack = player.getInventory().getItem(i);

                            if(stack == null){
                                if(i == 35){
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
                                    break;
                                } else {
                                    continue;
                                }
                            }
                            if(!stack.getType().equals(Material.NAME_TAG)){
                                if(i != 35){
                                    continue;
                                } else {
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
                                    break;
                                }
                            }

                            boolean done0 = GateExecutionOut.DCExitLogic(player, CompanyStore.CompanyTable.get(GateUtil.parseData(event.getLine(2))[0]),stack,event.getLine(2));
                            boolean done1 = false;
                            if (done0) {
                                done1 = GateExecutionIn.DCEntryLogic(player,stack,event.getLine(3), true);
                            }

                            if(done0 && done1){
                                Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN,() -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getValidatorComplete())),10);
                                break;
                            } else if(i == 35) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " " + MFConfig.INSTANCE.getValidatorFail()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent info) {
        String[] infoOut = GateUtil.parseData(info.getLine(2));
        String[] infoIn = GateUtil.parseData(info.getLine(3));

        if(CompanyStore.CompanyTable.containsKey(infoOut[0]) && CompanyStore.CompanyTable.containsKey(infoIn[0]) && MFConfig.INSTANCE.hasBuildGatePermission(info.getPlayer())){
            SignBuildOptions opt = SignBuildOptions.create()
                    .setName(MFConfig.INSTANCE.getOutput() + MFConfig.INSTANCE.getValidatorTrainCartsName());
            opt.setDescription(MFConfig.INSTANCE.getValidatorTrainCartsDescription());
            return opt.handle(info.getPlayer());
        }

        return false;
    }
}
