package com.hasunemiku2015.metrofare.Gate.People;

import com.hasunemiku2015.metrofare.Company.AbstractCompany;
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
                    openGate = TicketExitLogic(company, hand, sign);
                    if (openGate) {
                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatOut()));
                    }
                }


                if (openGate) {
                    sign.setLine(2, MFConfig.INSTANCE.getTransient1Out());
                    sign.setLine(3, MFConfig.INSTANCE.getTransient2Out());
                    sign.update();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(MTFA.plugin, () -> {
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
            // Return card Invalid message
            return false;
        }
        if (!card.getOwner().equals(p.getUniqueId().toString())) {
            //Return wrong player error message
            return false;
        }
        if (!card.hasEntered()) {
            //Return card not yet entered error message
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
        Bukkit.getScheduler().runTaskLater(MTFA.plugin, () -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatRemaining() + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + (card.getBalance() / 1000.0))), 20);

        card.removeEntryData();
        card.removeCompany();
        card.setBalance(card.getBalance() - (int) (fare * 1000));
        card.addPaymentRecord(company.getName(), true, (int) (fare * 1000));
        card.updateCard();

        company.addRevenue(fare);
        return true;
    }

    public static boolean TicketExitLogic(AbstractCompany company, ItemStack hand, Sign sign) {
        Ticket ticket = new Ticket(hand);
        if (!ticket.isValid()) {
            // Return ticket Invalid message
            return false;
        }
        if (!ticket.hasEntered()) {
            //Return ticket entered error message
            return false;
        }
        if (!ticket.checkExitCompany(company)) {
            //Return exiting from wrong company error
            return false;
        }

        if (ticket.getExitData().equals(sign.getLine(1).split(",")[1])) {
            return true;
        }

        if (!ticket.checkExitCompany(CompanyStore.CompanyTable.get(ticket.getCompanyFrom()))) {
            //not valid inter-company ticket
            return false;
        }

        if (ticket.getFare1000() < company.computeFare(ticket.getEntryData(), sign.getLine(1).split(",")[1])) {
            //Return not enough fare error
            return false;
        }
        return true;
    }
}
