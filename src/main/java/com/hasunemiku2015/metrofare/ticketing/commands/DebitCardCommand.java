package com.hasunemiku2015.metrofare.ticketing.commands;

import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.ticketing.types.DebitCard;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebitCardCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (MFConfig.INSTANCE.noTicketingPermission(player)) return true;
        if (args.length == 0) {
            help(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("givecard")) {
            Inventory inv = player.getInventory();
            ItemStack its = DebitCard.newCard(player);

            for (int i = 0; i < 36; i++) {
                if (inv.getItem(i) == null) {
                    inv.setItem(i, its);
                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully issued a new debit card!");
                    return true;
                }
            }
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Your inventory is full!");
            return true;
        }
        if (args[0].equalsIgnoreCase("value")) {
            if (args.length < 3) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Insufficient Arguments!");
                return true;
            }

            DebitCard card = new DebitCard(player.getInventory().getItemInMainHand());
            if (!card.isValid()) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a Debit Card!");
                return true;
            }

            double amount;
            int newValue;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: The amount input must be a number!");
                return true;
            }

            switch (args[1]) {
                case "add":{
                    newValue = card.getBalance() + (int) (amount * 1000);
                    card.addPaymentRecord("Command", false, (int) (amount * 1000));
                    break;
                }
                case "deduct":{
                    newValue = card.getBalance() - (int) (amount * 1000);
                    card.addPaymentRecord("Command", true, (int) (amount * 1000));
                    break;
                }
                case "set": {
                    newValue = (int) (amount * 1000);
                    if ((card.getBalance() - (int) (amount * 1000) > 0)) {
                        card.addPaymentRecord("Command", true, card.getBalance() - (int) (amount * 1000));
                    } else {
                        card.addPaymentRecord("Command", false, -card.getBalance() + (int) (amount * 1000));
                    }
                    break;
                }
                default: {
                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + "Error: Unknown selector!");
                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + "Possible Selectors:");
                    player.sendMessage(MFConfig.INSTANCE.getBase() + "- add");
                    player.sendMessage(MFConfig.INSTANCE.getBase() + "- deduct");
                    player.sendMessage(MFConfig.INSTANCE.getBase() + "- set");
                    return true;
                }
            }
            if (newValue < 2000000000 && newValue > -2000000000) {
                card.setBalance(newValue);
                card.updateCard();
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully updated card balance!");
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " New Balance: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + card.getBalance() / 1000.0);
                return true;
            } else {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot update value, the updated value is out of range of allowed values!");
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Allowed values should be between -2,000,000 and 2,000,000.");
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("auto")) {
            if (args.length < 2) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Unknown selector!");
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Possible Selectors:");
                player.sendMessage(MFConfig.INSTANCE.getBase() + "- dailylimit");
                player.sendMessage(MFConfig.INSTANCE.getBase() + "- addamount");
                player.sendMessage(MFConfig.INSTANCE.getBase() + "- info");
                player.sendMessage(MFConfig.INSTANCE.getBase() + "- enable");
                player.sendMessage(MFConfig.INSTANCE.getBase() + "- disable");
                return true;
            }

            DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
            if (!dc.isValid()) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a " + MFConfig.INSTANCE.getDebitCardName() + "!");
                return true;
            }

            switch (args[1]) {
                case "info": {
                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + " Info (Auto Top Up):");
                    if (dc.getLastAddedAuto() != 0) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + "Add Amount: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + dc.getAddAmount() / 1000.0);
                        player.sendMessage(MFConfig.INSTANCE.getBase() + "Daily Limit: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + dc.getDailyLimit() / 1000.0);
                        player.sendMessage(MFConfig.INSTANCE.getBase() + "Last Auto Value Add Trigger Time (Unix Time): " + MFConfig.INSTANCE.getOutput() + dc.getLastAddedAuto());
                    } else {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + "This card doesn't have auto top up enabled.");
                    }
                    return true;
                }
                case "dailylimit": {
                    if (dc.getLastAddedAuto() == 0) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: This " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getError() + "doesn't have auto top up enabled.");
                        return true;
                    }

                    int limit;
                    try {
                        limit = (int) (Double.parseDouble(args[2]) * 1000);
                        if (dc.getAddAmount() >= limit) {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                            return true;
                        }
                    } catch (Exception ex) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Daily limit must be a number!");
                        return true;
                    }

                    dc.setDailyLimit(limit);
                    dc.updateCard();
                    return true;
                }

                case "addamount": {
                    if (dc.getLastAddedAuto() == 0) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: This " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getError() + "doesn't have auto top up enabled.");
                        return true;
                    }

                    int amount;
                    try {
                        amount = (int) (Double.parseDouble(args[2]) * 1000);

                        if (amount <= 0) {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount must be greater than 0.");
                            return true;
                        }

                        if (amount >= dc.getDailyLimit()) {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                            return true;
                        }
                    } catch (Exception ex) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount must be a number!");
                        return true;
                    }

                    dc.setAddAmount(amount);
                    dc.updateCard();
                    return true;
                }

                case "enable": {
                    if (dc.getLastAddedAuto() != 0) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Auto top-up is already enabled on this " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + ".");
                        return true;
                    }

                    int amount, limit;
                    try {
                        amount = (int) (Double.parseDouble(args[2]) * 1000);
                        limit = (int) (Double.parseDouble(args[3]) * 1000);

                        if (amount <= 0) {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount must be greater than 0.");
                            return true;
                        }

                        if (amount > limit) {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                            return true;
                        }
                    } catch (Exception ex) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Insufficient/Invalid Arguments");
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + "debitcard auto enable <add amount(Number)> <daily limit(Number)>");
                        return true;
                    }

                    dc.setLastAddedAuto(1);
                    dc.setAddAmount(amount);
                    dc.setAddedAmount(0);
                    dc.setDailyLimit(limit);
                    dc.updateCard();

                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully enabled auto top-up for this " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + "!");
                    return true;
                }

                case "disable": {
                    if (dc.getLastAddedAuto() == 0) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Auto top-up is already disabled on this " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + ".");
                        return true;
                    }

                    dc.removeAddAmount();
                    dc.removeAddedAmount();
                    dc.removeDailyLimit();
                    dc.removeLastAddedAuto();
                    dc.updateCard();

                    player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully disabled auto top-up for this " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + "!");
                    return true;
                }
            }

            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Unknown selector!");
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + "Possible Selectors:");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- dailylimit");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- addamount");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- info");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- enable");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- disable");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "- records");
            return true;
        }
        if (args[0].equalsIgnoreCase("records")) {
            DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
            if (!dc.isValid()) {
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a " + MFConfig.INSTANCE.getDebitCardName() + "!");
                return true;
            }

            printPaymentRecord(player, dc);
            return true;
        }

        help(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return new ArrayList<>();
        if (MFConfig.INSTANCE.noTicketingPermission((Player) sender)) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        switch (args.length) {
            case 1: {
                out.add("help");
                out.add("givecard");
                out.add("value");
                out.add("auto");
                out.add("records");
                return out;
            }
            case 2: {
                if (args[0].equalsIgnoreCase("value")) {
                    out.add("add");
                    out.add("deduct");
                    out.add("set");
                }

                if (args[0].equalsIgnoreCase("auto")) {
                    out.add("info");
                    out.add("addamount");
                    out.add("dailylimit");
                    out.add("enable");
                    out.add("disable");
                }

                return out;
            }
            default:
                return out;
        }
    }

    private void help(Player player) {
        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Debit Card Commands:");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "- help: Displays this page");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "- givecard: Issue a new card to Command Sender");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "- value: Edit the value of Debit Card");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "- auto: Edit the auto top-up settings of Debit Card");
    }

    public static void printPaymentRecord(Player player, DebitCard dc) {
        List<String[]> records = dc.getPaymentRecords();

        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Payment Records for this " + MFConfig.INSTANCE.getDebitCardName() + MFConfig.INSTANCE.getBase() + ":");
        player.sendMessage(MFConfig.INSTANCE.getBase() + String.format("%1$-5s|%2$-10s|%3$5s", StringUtils.rightPad("Data", 5), StringUtils.center("Company/Individual", 20), StringUtils.leftPad("Change", 10)));
        player.sendMessage(MFConfig.INSTANCE.getBase() + String.format("%s", "----------------------------------------------------"));

        for (int i = 0; i < records.size(); i++) {
            String entry = String.format("%1$-5s|%2$-20s|%3$5s", StringUtils.rightPad(i + ".", 5), StringUtils.center(records.get(i)[0], 20), StringUtils.rightPad(records.get(i)[1].contains("+") ? ChatColor.GREEN + records.get(i)[1] : ChatColor.RED + records.get(i)[1], 10));
            player.sendMessage(MFConfig.INSTANCE.getBase() + entry);
        }
    }
}
