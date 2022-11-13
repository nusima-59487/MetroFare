package com.hasunemiku2015.metrofare.gate.people;

import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import com.hasunemiku2015.metrofare.ticketing.types.DebitCard;
import com.hasunemiku2015.metrofare.ticketing.types.Ticket;
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

public class GateExecutionOut implements Listener {

    @EventHandler
    public void onExitGateUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (Objects.requireNonNull(event.getClickedBlock()).getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            ItemStack hand = event.getItem();
            String[] data = GateUtil.parseData(sign.getLine(1));

            if (GateUtil.checkValid(sign, MFConfig.INSTANCE.getPrefixOut()) && GateUtil.validFace(sign, event.getBlockFace())) {
                boolean openGate = false;
                if (hand == null) return;
                AbstractCompany company = CompanyStore.CompanyTable.get(data[0]);
                if (company == null) return;

                if (hand.getType().equals(Material.NAME_TAG)) {
                    openGate = DCExitLogic(event.getPlayer(), company, hand, sign.getLine(1));
                }

                if (hand.getType().equals(Material.PAPER)) {
                    openGate = TicketExitLogic(event.getPlayer(), company, hand, sign);
                    if (openGate) {
                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatOut()));
                    }
                }

                if (openGate) {
                    sign.setLine(2, MFConfig.INSTANCE.getTransient1Out());
                    sign.setLine(3, MFConfig.INSTANCE.getTransient2Out());
                    sign.update();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(MTFA.PLUGIN, () -> {
                        sign.setLine(2, MFConfig.INSTANCE.getInfo1Out());
                        sign.setLine(3, MFConfig.INSTANCE.getInfo2Out());
                        sign.update();
                    }, MFConfig.INSTANCE.getOpenTime());
                    GateUtil.setBlock(sign);
                }
            }
        }
    }

    public static boolean DCExitLogic(Player p, AbstractCompany company, ItemStack hand, String inputData) {
        DebitCard card = new DebitCard(hand);
        if (!card.isValid()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getDebitCardInvalidOut()));
            return false;
        }
        if (!card.getOwner().equals(p.getUniqueId().toString())) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getPlayerInvalidOut()));
            return false;
        }
        if (!card.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getCardNotEnteredOut()));
            return false;
        }

        String s = card.getEntryData();
        double fare = MFConfig.INSTANCE.getDefaultFare();

        String dat = "";
        try {
            dat = inputData.split(",")[1];
        } catch (Exception ignored) {
        }

        double fareUpdate = company.computeFare(s, dat) / 1000.0;
        if (fareUpdate > 0) {
            fare = fareUpdate;
        }

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatOut() + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + fare));
        Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN, () -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatRemaining() + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + (card.getBalance() / 1000.0))), 20);

        card.removeEntryData();
        card.removeCompany();
        card.setBalance(card.getBalance() - (int) (fare * 1000));
        card.addPaymentRecord(company.getName(), true, (int) (fare * 1000));
        card.updateCard();

        company.addRevenue(fare);
        return true;
    }

    public static boolean TicketExitLogic(Player p, AbstractCompany company, ItemStack hand, Sign sign) {
        Ticket ticket = new Ticket(hand);
        if (!ticket.isValid()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketInvalidOut()));
            return false;
        }
        if (!ticket.hasEntered()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketNotEnteredOut()));
            return false;
        }
        if (!ticket.checkExitCompany(company)) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketWrongExitCompanyOut()));
            return false;
        }

        if (ticket.getExitData().equals(sign.getLine(1).split(",")[1])) {
            return true;
        }

        if (!ticket.checkExitCompany(CompanyStore.CompanyTable.get(ticket.getCompanyFrom()))) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketInvalidInterCompanyOut()));
            return false;
        }

        if (ticket.getFare1000() < company.computeFare(ticket.getEntryData(), sign.getLine(1).split(",")[1])) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getTicketInsufficientFareOut()));
            return false;
        }
        return true;
    }
}
