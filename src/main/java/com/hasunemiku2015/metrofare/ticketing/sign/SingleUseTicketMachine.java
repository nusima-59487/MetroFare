package com.hasunemiku2015.metrofare.ticketing.sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.MetroFare;
import com.hasunemiku2015.metrofare.VaultIntegration;
import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.company.DijkstraCompany;
import com.hasunemiku2015.metrofare.company.FareTableCompany;
import com.hasunemiku2015.metrofare.gate.people.GateUtil;
import com.hasunemiku2015.metrofare.ticketing.types.Ticket;

import de.vogella.algorithms.dijkstra.model.Vertex;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.wesjd.anvilgui.AnvilGUI;

/**
 * @author nusima-59487 (Monnette)
 */
public class SingleUseTicketMachine implements Listener {
    private static Inventory inventoryGUI; 
    private static AnvilGUI.Builder inputStationCode;

    private static final NamespacedKey STATION_CODE_KEY = new NamespacedKey(MetroFare.PLUGIN, "station_code"); 
    private static final NamespacedKey COMPANY_KEY = new NamespacedKey(MetroFare.PLUGIN, "company"); 


    public static void init () {
        inventoryGUI = Bukkit.createInventory(null, 9, MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()); 

        inventoryGUI.setItem(0, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(1, newItem(Material.PAPER, "New " + MetroConfiguration.INSTANCE.getTicketName())); 
        inventoryGUI.setItem(2, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(3, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(4, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(5, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(6, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(7, newItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        inventoryGUI.setItem(8, newItem(Material.BARRIER, ChatColor.RED + "Cancel"));

        inputStationCode = newInputInventory(MetroConfiguration.INSTANCE.getPromptStationCodeSTM()); 
        inputStationCode.onClick((slot, stateSnapshot) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }
            Player player = stateSnapshot.getPlayer();
            try {
                String endStn = stateSnapshot.getText();
                String companyName = player.getPersistentDataContainer().get(COMPANY_KEY, PersistentDataType.STRING); 
                String startStn = player.getPersistentDataContainer().get(STATION_CODE_KEY, PersistentDataType.STRING); 
                List<String> possibleStationCodes = getPossibleStationCodes(companyName); 
                if (!possibleStationCodes.contains(endStn)) {
                    return Collections.singletonList(AnvilGUI.ResponseAction.updateTitle(MetroConfiguration.INSTANCE.getPromptInvalidStationCodeSTM(), true)); 
                }
                AbstractCompany company = CompanyStore.CompanyTable.get(companyName); 
                double fare = company.computeFare(startStn, endStn) / 1000.0; 
                if (VaultIntegration.hasEnough(player, fare)) {
                    VaultIntegration.deduct(player, fare);
                    ItemStack ticket = Ticket.newTicket(company, startStn, endStn, (int) fare*1000); 
                    for (int i = 0; i < 36; i++) {
                        if (player.getInventory().getItem(i) == null) {
                            player.getInventory().setItem(i, ticket);
                            company.addRevenue(fare);
                            player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                new TextComponent(
                                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + 
                                    " Successfully issued a new ticket from " + 
                                    MetroConfiguration.INSTANCE.getInput() + startStn + MetroConfiguration.INSTANCE.getBase() + 
                                    " to " + 
                                    MetroConfiguration.INSTANCE.getInput() + endStn + MetroConfiguration.INSTANCE.getBase() + 
                                    "!"
                                )
                            );
                            return Collections.singletonList(AnvilGUI.ResponseAction.close());
                        }
                    }
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Please clean up your inventory and try again"));
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
            } catch (Exception ignored) {
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getFailSTM()));
            return Collections.singletonList(AnvilGUI.ResponseAction.close());
        });
    }
    
    private static ItemStack newItem(Material material, String name) {
        ItemStack stack = new ItemStack(material, 1);
        stack.addUnsafeEnchantment(Enchantment.LURE, 1);
        ItemMeta stackMeta = stack.getItemMeta();
        assert stackMeta != null;
        stackMeta.setDisplayName(name);
        stackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(stackMeta);
        return stack;
    }
    
    private static AnvilGUI.Builder newInputInventory(String name) {
        AnvilGUI.Builder var0 = new AnvilGUI.Builder();
        var0.plugin(MetroFare.PLUGIN);
        var0.itemLeft(new ItemStack(Material.PAPER, 1));
        var0.title(name);
        return var0;
    }
    
    @EventHandler
    public void onBuild(SignChangeEvent event) {
        if (!(event.getBlock().getBlockData() instanceof WallSign)) return;
        if (!Objects.requireNonNull(event.getLine(0)).equalsIgnoreCase(MetroConfiguration.INSTANCE.getPrefixSTM())) return; 
        String[] data = GateUtil.parseData(Objects.requireNonNull(event.getLine(1))); 
        if (data.length != 2) return; 
        if (!CompanyStore.CompanyTable.containsKey(data[0])) return;
        List<String> possibleStationCodes = getPossibleStationCodes(data[0]); 
        if (!possibleStationCodes.contains(data[1])) return; // TODO: No second value if 2 remaining types
        if (!MetroConfiguration.INSTANCE.hasBuildEditorPermission(event.getPlayer())) {
            event.getBlock().setType(Material.AIR);
            return;
        }

        event.setLine(2, MetroConfiguration.INSTANCE.getInfo1STM()); 
        event.setLine(3, MetroConfiguration.INSTANCE.getInfo2STM()); 
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer().isSneaking()) return;
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getBlockData() instanceof WallSign)) return;
        if (((WallSign) event.getClickedBlock().getBlockData()).getFacing() != event.getBlockFace()) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!sign.getLine(0).equalsIgnoreCase(MetroConfiguration.INSTANCE.getPrefixSTM())) return;
        event.setCancelled(true);
        String[] data = GateUtil.parseData(Objects.requireNonNull(sign.getLine(1)));
        event.getPlayer().getPersistentDataContainer().set(COMPANY_KEY, PersistentDataType.STRING, data[0]);
        event.getPlayer().getPersistentDataContainer().set(STATION_CODE_KEY, PersistentDataType.STRING, data[1]); 
        event.getPlayer().openInventory(inventoryGUI);
    }

    @EventHandler
    public void onIGUIDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inventoryGUI)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIGUIClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventoryGUI)) {
            Bukkit.getScheduler().runTaskLater(MetroFare.PLUGIN, () -> ((Player) event.getPlayer()).updateInventory(), 1);
        }
    }

    //Main Logic
    @EventHandler
    public void onIGUIClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventoryGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        switch (event.getRawSlot()) {
            case 1: {
                

                inputStationCode.open(player); 
                return; 
            }
            case 8: {
                //Cancel
                player.closeInventory();
                break;
            }
            default: {
                event.setCancelled(true);
                return; 
            }
        }
    }


    private static List<String> getPossibleStationCodes (String companyName) {
        List<String> possibleStationCodes = new ArrayList<>();

        switch (CompanyStore.CompanyTable.get(companyName).getType()) {
            case UNIFORM: {
                possibleStationCodes.add("Uniform"); // ['Uniform']
                break;
            }
            case DIJKSTRA: {
                DijkstraCompany dc = (DijkstraCompany) CompanyStore.CompanyTable.get(companyName);
                for (Vertex v : dc.getDataTable().getVertices()) {
                    possibleStationCodes.add(v.getName());
                }
                break;
            }
            case FARE_TABLE: {
                FareTableCompany fc = (FareTableCompany) CompanyStore.CompanyTable.get(companyName);
                possibleStationCodes.addAll(fc.getFareTable().getKeys());
                break;
            }
            // TODO: Cover 2 remaining types (ABS_COORD, ZONE)
        }

        return possibleStationCodes; 
    }

}
