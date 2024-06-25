package com.hasunemiku2015.metrofare.gate.people;

import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import com.hasunemiku2015.metrofare.ticketing.types.DebitCard;
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

public class OTPExecution implements Listener {
    @EventHandler
    public void onOTPMachineUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (Objects.requireNonNull(event.getClickedBlock()).getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            ItemStack hand = event.getItem();

            if (GateUtil.checkValid(sign, MFConfig.INSTANCE.getPrefixOTP()) && GateUtil.validFace(sign, event.getBlockFace())) {
                if (hand == null) {
                    return;
                }

                if (hand.getType().equals(Material.NAME_TAG)) {
                    if (dcOtpLogic(event.getPlayer(), hand, sign.getLine(1))) {
                        sign.setLine(2, MFConfig.INSTANCE.getTransient1OTP());
                        sign.setLine(3, MFConfig.INSTANCE.getTransient2OTP());
                        sign.update();

                        final Location signCoordinate = sign.getLocation();
                        Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN, () -> {
                            Sign updateSign = ((Sign) signCoordinate.getBlock().getState());
                            updateSign.setLine(2, MFConfig.INSTANCE.getInfo1OTP());
                            updateSign.setLine(3, MFConfig.INSTANCE.getInfo2OTP());
                            updateSign.update();
                        }, MFConfig.INSTANCE.getOpenTime());
                        GateUtil.setBlock(sign);
                    }
                }
            }
        }
    }

    public boolean dcOtpLogic(Player p, ItemStack hand, String inputData) {
        DebitCard card = new DebitCard(hand);
        if (!card.isValid()) {
            // Return card Invalid message
            return false;
        }
        String[] data = GateUtil.parseData(inputData);
        if (!CompanyStore.CompanyTable.containsKey(data[0])) {
            // Return company Invalid message
            return false;
        }
        AbstractCompany company = CompanyStore.CompanyTable.get(data[0]);

        if (!card.getOwner().equals(p.getUniqueId().toString())) {
            //Return wrong player error message
            return false;
        }

        if (card.getBalance() <= 0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getInsufficientOTP()));
            return false;
        }

        int deductAmount1000 = (int) Math.round(Double.parseDouble(data[1]) * 1000);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getChatFareOTP() + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + (deductAmount1000 / 1000.0)));
        Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN, () -> p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getChatRemainingOTP() + " " + MFConfig.INSTANCE.getCurrencyUnit() + card.getBalance() / 1000.0)), 20);

        card.setBalance(card.getBalance() - deductAmount1000);
        card.addPaymentRecord(company.getName(), true, deductAmount1000);
        card.updateCard();

        company.addRevenue(deductAmount1000 / 1000.0);
        return true;
    }
}
