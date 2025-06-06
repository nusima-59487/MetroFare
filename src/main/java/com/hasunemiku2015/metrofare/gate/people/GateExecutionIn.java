package com.hasunemiku2015.metrofare.gate.people;

import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.MetroFare;
import com.hasunemiku2015.metrofare.company.UniformCompany;
import com.hasunemiku2015.metrofare.ticketing.types.DebitCard;
import com.hasunemiku2015.metrofare.ticketing.types.Ticket;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GateExecutionIn implements Listener {
    @EventHandler
    public void onEntryGateUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (Objects.requireNonNull(event.getClickedBlock()).getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            ItemStack hand = event.getItem();

            if (GateUtil.checkValid(sign, MetroConfiguration.INSTANCE.getPrefixIn()) && GateUtil.validFace(sign, event.getBlockFace())) {
                event.setCancelled(true);
                boolean openGate = false;

                if (hand == null) return;
                if (hand.getType().equals(Material.NAME_TAG)) {
                    openGate = DCEntryLogic(event.getPlayer(), hand, sign.getLine(1), false);
                }

                if (hand.getType().equals(Material.PAPER)) {
                    openGate = TicketEntryLogic(event.getPlayer(), hand, sign.getLine(1));
                }

                if (openGate) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getChatIn()));

                    sign.setLine(2, MetroConfiguration.INSTANCE.getTransient1In());
                    sign.setLine(3, MetroConfiguration.INSTANCE.getTransient2In());
                    sign.update();

                    final Location signCoordinate = sign.getLocation();
                    Bukkit.getScheduler().runTaskLater(MetroFare.PLUGIN, () -> {
                        Sign updateSign = ((Sign) signCoordinate.getBlock().getState());
                        updateSign.setLine(2, MetroConfiguration.INSTANCE.getInfo1In());
                        updateSign.setLine(3, MetroConfiguration.INSTANCE.getInfo2In());
                        updateSign.update();
                    }, MetroConfiguration.INSTANCE.getOpenTime());

                    GateUtil.setBlock(sign);
                }
            }
        }
    }

    public static boolean DCEntryLogic(Player p, ItemStack hand, String inputData, boolean isTransferGate) {
        DebitCard card = new DebitCard(hand);
        if (!card.isValid()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getDebitCardInvalidIn()));
            return false;
        }
        String[] data = GateUtil.parseData(inputData);
        if (!CompanyStore.CompanyTable.containsKey(data[0])) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getCompanyInvalidIn()));
            return false;
        }
        if (!card.getOwner().equals(p.getUniqueId().toString())) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getPlayerInvalidIn()));
            return false;
        }
        if (card.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getCardEnteredIn()));
            return false;
        }
        if (card.getBalance() <= 0 && !isTransferGate) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getInsufficientIn()));
            return false;
        }

        card.setCompany(data[0]);
        card.setEntryData(data[1]);
        card.updateCard();
        return true;
    }

    public boolean TicketEntryLogic(Player p, ItemStack hand, String inputData) {
        Ticket ticket = new Ticket(hand);
        if (!ticket.isValid()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getTicketInvalidIn()));
            return false;
        }
        if (ticket.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getTicketEnteredIn()));
            return false;
        }

        if (!ticket.checkEntryCompany(CompanyStore.CompanyTable.get(inputData.split(",")[0]))) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getEntryCompanyInvalidIn()));
            return false;
        }

        String[] var0 = GateUtil.parseData(inputData);
        String companyName = var0[0];
        String stationName = var0[1];

        // Do not check invalid station if UniformCompany
        if (!(CompanyStore.CompanyTable.get(companyName) instanceof UniformCompany)) {
            if (!ticket.getEntryData().equals(stationName)) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " " + MetroConfiguration.INSTANCE.getStationInvalidIn()));
                return false;
            }
        }

        ticket.entryProcedure();
        return true;
    }
}
