package com.hasunemiku2015.metrofare.Gate.People;

import com.hasunemiku2015.metrofare.Company.CompanyStore;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import com.hasunemiku2015.metrofare.Ticketing.Types.DebitCard;
import com.hasunemiku2015.metrofare.Ticketing.Types.Ticket;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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

            if (GateUtil.checkValid(sign, MFConfig.INSTANCE.getPrefixIn()) && GateUtil.validFace(sign, event.getBlockFace())) {
                boolean openGate = false;

                if (hand == null) return;
                if (hand.getType().equals(Material.NAME_TAG)) {
                    openGate = DCEntryLogic(event.getPlayer(), hand, sign.getLine(1), false);
                }

                if (hand.getType().equals(Material.PAPER)) {
                    openGate = TicketEntryLogic(event.getPlayer(), hand, sign.getLine(1));
                }

                if (openGate) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatIn()));

                    sign.setLine(2, MFConfig.INSTANCE.getTransient1In());
                    sign.setLine(3, MFConfig.INSTANCE.getTransient2In());
                    sign.update();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(MTFA.PLUGIN, () -> {
                        sign.setLine(2, MFConfig.INSTANCE.getInfo1In());
                        sign.setLine(3, MFConfig.INSTANCE.getInfo2In());
                        sign.update();
                    }, MFConfig.INSTANCE.getOpenTime());

                    GateUtil.setBlock(sign);
                }
            }
        }
    }

    public static boolean DCEntryLogic(Player p, ItemStack hand, String inputData, boolean isTransferGate) {
        DebitCard card = new DebitCard(hand);
        if (!card.isValid()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getDebitCardInvalidIn()));
            return false;
        }
        String[] data = GateUtil.parseData(inputData);
        if (!CompanyStore.CompanyTable.containsKey(data[0])) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getCompanyInvalidIn()));
            return false;
        }
        if (!card.getOwner().equals(p.getUniqueId().toString())) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getPlayerInvalidIn()));
            return false;
        }
        if (card.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getCardEnteredIn()));
            return false;
        }
        if (card.getBalance() <= 0 && !isTransferGate) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getInsufficientIn()));
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
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketInvalidIn()));
            return false;
        }
        if (ticket.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketEnteredIn()));
            return false;
        }

        if (!ticket.checkEntryCompany(CompanyStore.CompanyTable.get(inputData.split(",")[0]))) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getEntryCompanyInvalidIn()));
            return false;
        }
        if (!ticket.getEntryData().equals(inputData.split(",")[1])) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getStationInvalidIn()));
            return false;
        }
        ticket.entryProcedure();
        return true;

    }
}
