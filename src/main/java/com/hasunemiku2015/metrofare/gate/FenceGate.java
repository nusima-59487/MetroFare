package com.hasunemiku2015.metrofare.gate;

import com.hasunemiku2015.metrofare.MFConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Gate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class FenceGate implements Listener {
    @EventHandler
    public void onFenceGateLock(BlockPlaceEvent event){
        Location blockLoc = event.getBlock().getLocation();
        int y = blockLoc.getBlockY() - 1;
        blockLoc.setY(y);

        if((blockLoc.getBlock().getBlockData() instanceof Gate)){
            if(!MFConfig.INSTANCE.hasFenceGatePermission(event.getPlayer())){
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerOpenFenceGate(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!(Objects.requireNonNull(event.getClickedBlock()).getBlockData() instanceof Gate)) return;

        //Check if there is Structure Void
        Location blockLoc = event.getClickedBlock().getLocation();
        int y = blockLoc.getBlockY() + 1;
        blockLoc.setY(y);

        if(blockLoc.getBlock().getType() == Material.STRUCTURE_VOID){
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " This gate is locked!"));
            event.setCancelled(true);
        }
    }
}
