package com.hasunemiku2015.metrofare.Ticketing.Commands;

import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.Ticketing.Types.DebitCard;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebitCardCBlkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 2){
            return true;
        }

        List<Player> affectedPlayers = new ArrayList<>();
        if(sender instanceof BlockCommandSender) {
            BlockCommandSender bcs = (BlockCommandSender) sender;

            //<name>: '@p' and '@a'
            if(args[1].equals("@p")){
                Vector bLoc = bcs.getBlock().getLocation().toVector();
                double dist = Double.MAX_VALUE;
                Player nearestPlayer = null;
                for(Player p : Bukkit.getOnlinePlayers()){
                    Vector pLoc = p.getLocation().toVector();
                    if(pLoc.subtract(bLoc).length() < dist){
                        dist = pLoc.subtract(bLoc).length();
                        nearestPlayer = p;
                    }
                }
                affectedPlayers.add(nearestPlayer);
            } else if(args[1].equals("@a")){
                affectedPlayers.addAll(Bukkit.getOnlinePlayers());
            } else {
                Player p = Bukkit.getPlayer(args[1]);
                if(p == null){
                    sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot find the affected player!");
                    return true;
                }
                affectedPlayers.add(p);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null){
                sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot find the affected player!");
                return true;
            }
            affectedPlayers.add(p);
        } else {
            return true;
        }

        //vam givecard <name>
        if(args[0].equalsIgnoreCase("givecard")){
            for(Player player: affectedPlayers){
                Inventory inv = player.getInventory();
                ItemStack its = DebitCard.newCard(player);

                for(int i = 0 ; i < 36 ; i++){
                    if(inv.getItem(i) == null){
                        inv.setItem(i,its);
                        player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully issued a new debit card!");
                        return true;
                    }
                }
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Your inventory is full!");
            }
        }

        //vam add <name> <value>
        if(args[0].equalsIgnoreCase("add")){
            for(Player player: affectedPlayers){
                DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                if(!dc.isValid()){
                    player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                    continue;
                }

                double add;
                try {
                    add = Double.parseDouble(args[2]);
                } catch (NumberFormatException ex) {
                    return true;
                }
                dc.setBalance(dc.getBalance() + (int)(add * 1000));
                dc.addPaymentRecord("Command Block / Console", false, (int) (add * 1000));
                dc.updateCard();
            }
            return true;
        }
        //vam deduct <name> <value>
        if(args[0].equalsIgnoreCase("deduct")){
            for(Player player: affectedPlayers){
                DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                if(!dc.isValid()){
                    player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                    continue;
                }

                double deduct;
                try {
                    deduct = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    return true;
                }
                dc.setBalance(dc.getBalance() - (int)(deduct * 1000));
                dc.addPaymentRecord("Command Block", true, (int) (deduct * 1000));
                dc.updateCard();
            }
        }

        //vam exit <name>
        if(args[0].equalsIgnoreCase("exit")){
            for(Player player: affectedPlayers){
                DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                if(!dc.isValid()){
                    player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                    continue;
                }

                dc.removeEntryData();
                dc.updateCard();
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully removed the entry data of " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
            }
            return true;
        }

        //vam records <name>
        if(args[0].equalsIgnoreCase("records")){
            for(Player player: affectedPlayers){
                DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                if(!dc.isValid()){
                    player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                    continue;
                }

                DebitCardCommand.printPaymentRecord(player,dc);
            }
        }

        //vam auto <name> <selector> <value1> <value2>
        if(args[0].equalsIgnoreCase("auto")){
            for(Player player: affectedPlayers){
                if (args.length < 3) {
                    sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Unknown selector!");
                    sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Possible Selectors:");
                    sender.sendMessage(MFConfig.getBase() + "- dailylimit");
                    sender.sendMessage(MFConfig.getBase() + "- addamount");
                    sender.sendMessage(MFConfig.getBase() + "- info");
                    sender.sendMessage(MFConfig.getBase() + "- enable");
                    sender.sendMessage(MFConfig.getBase() + "- disable");
                    return true;
                }

                DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                if (!dc.isValid()) {
                    player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a " + MFConfig.getDebitCardName() + "!");
                    return true;
                }

                switch (args[2]) {
                    case "info": {
                        player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " " + MFConfig.getDebitCardName() + MFConfig.getBase() + " Info (Auto Top Up):");
                        if (dc.getLastAddedAuto() != 0) {
                            player.sendMessage(MFConfig.getBase() + "Add Amount: " + MFConfig.getCurrencyUnit() + MFConfig.getOutput() + dc.getAddAmount() / 1000.0);
                            player.sendMessage(MFConfig.getBase() + "Daily Limit: " + MFConfig.getCurrencyUnit() + MFConfig.getOutput() + dc.getDailyLimit() / 1000.0);
                            player.sendMessage(MFConfig.getBase() + "Last Auto Value Add Trigger Time (Unix Time): " + MFConfig.getOutput() + dc.getLastAddedAuto());
                        } else {
                            player.sendMessage(MFConfig.getBase() + "This card doesn't have auto top up enabled.");
                        }
                        return true;
                    }
                    case "dailylimit": {
                        if (dc.getLastAddedAuto() == 0) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: This " + MFConfig.getDebitCardName() + MFConfig.getError() + "doesn't have auto top up enabled.");
                            return true;
                        }

                        int limit;
                        try {
                            limit = (int) (Double.parseDouble(args[3]) * 1000);
                            if (dc.getAddAmount() >= limit) {
                                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                                return true;
                            }
                        } catch (Exception ex) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Daily limit must be a number!");
                            return true;
                        }

                        dc.setDailyLimit(limit);
                        dc.updateCard();
                        return true;
                    }

                    case "addamount": {
                        if (dc.getLastAddedAuto() == 0) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: This " + MFConfig.getDebitCardName() + MFConfig.getError() + "doesn't have auto top up enabled.");
                            return true;
                        }

                        int amount;
                        try {
                            amount = (int) (Double.parseDouble(args[3]) * 1000);

                            if (amount <= 0) {
                                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount must be greater than 0.");
                                return true;
                            }

                            if (amount >= dc.getDailyLimit()) {
                                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                                return true;
                            }
                        } catch (Exception ex) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount must be a number!");
                            return true;
                        }

                        dc.setAddAmount(amount);
                        dc.updateCard();
                        return true;
                    }

                    case "enable": {
                        if (dc.getLastAddedAuto() != 0) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Auto top-up is already enabled on this " + MFConfig.getDebitCardName() + MFConfig.getBase() + ".");
                            return true;
                        }

                        int amount, limit;
                        try {
                            amount = (int) (Double.parseDouble(args[3]) * 1000);
                            limit = (int) (Double.parseDouble(args[4]) * 1000);

                            if (amount <= 0) {
                                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount must be greater than 0.");
                                return true;
                            }

                            if (amount > limit) {
                                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Add amount cannot be lower than Daily Limit.");
                                return true;
                            }
                        } catch (Exception ex) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Insufficient/Invalid Arguments");
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + "debitcard auto enable <add amount(Number)> <daily limit(Number)>");
                            return true;
                        }

                        dc.setLastAddedAuto(1);
                        dc.setAddAmount(amount);
                        dc.setAddedAmount(0);
                        dc.setDailyLimit(limit);
                        dc.updateCard();

                        player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully enabled auto top-up for this " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                        return true;
                    }

                    case "disable": {
                        if (dc.getLastAddedAuto() == 0) {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Auto top-up is already disabled on this " + MFConfig.getDebitCardName() + MFConfig.getBase() + ".");
                            return true;
                        }

                        dc.removeAddAmount();
                        dc.removeAddedAmount();
                        dc.removeDailyLimit();
                        dc.removeLastAddedAuto();
                        dc.updateCard();

                        player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully disabled auto top-up for this " + MFConfig.getDebitCardName() + MFConfig.getBase() + "!");
                        return true;
                    }
                }

                sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Unknown selector!");
                sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + "Possible Selectors:");
                sender.sendMessage(MFConfig.getBase() + "- dailylimit");
                sender.sendMessage(MFConfig.getBase() + "- addamount");
                sender.sendMessage(MFConfig.getBase() + "- info");
                sender.sendMessage(MFConfig.getBase() + "- enable");
                sender.sendMessage(MFConfig.getBase() + "- disable");
                return true;
            }
        }

        return true;
    }

    /// Tab Completion do Later. (Or someone help me do.)
//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        if(!(sender instanceof CommandBlock)) return new ArrayList<>();
//
//        List<String> out = new ArrayList<>();
//        List<String> names = new ArrayList<>();
//        for(Player p: Bukkit.getOnlinePlayers()){
//            names.add(p.getName());
//        }
//
//        switch (args.length){
//            case 1: {
//                out.add("givecard");
//                out.add("add");
//                out.add("deduct");
//                return out;
//            }
//            case 2: {
//                if(args[0].equalsIgnoreCase("givecard") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("deduct")){
//                    out.add("@p");
//                    out.add("@a");
//                    out.addAll(names);
//                }
//                return out;
//            }
//            default: return out;
//        }
//    }
}
