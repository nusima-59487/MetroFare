package com.hasunemiku2015.metrofare.ticketing.commands;

import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.ticketing.types.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TicketCBlkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3) return true;

        List<Player> affectedPlayers = new ArrayList<>();
        if(sender instanceof BlockCommandSender) {
            BlockCommandSender bcs = (BlockCommandSender) sender;
            //@p,@a selector
            switch(args[1]){
                case "@p": {
                    Location bloc = bcs.getBlock().getLocation();
                    double dist = Double.MAX_VALUE;
                    Player nearest = null;
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(Objects.equals(p.getLocation().getWorld(), bloc.getWorld())){
                            if(p.getLocation().toVector().subtract(bloc.toVector()).length() < dist){
                                dist = p.getLocation().toVector().subtract(bloc.toVector()).length();
                                nearest = p;
                            }
                        }
                    }
                    affectedPlayers.add(nearest);
                    break;
                }
                case "@a": {
                    affectedPlayers.addAll(Bukkit.getOnlinePlayers());
                    break;
                }
                default: {
                    if(Bukkit.getPlayer(args[1]) != null){
                        affectedPlayers.add(Bukkit.getPlayer(args[1]));
                    }
                    break;
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if(Bukkit.getPlayer(args[1]) != null){
                affectedPlayers.add(Bukkit.getPlayer(args[1]));
            }
        } else {
            return true;
        }

        if(affectedPlayers.isEmpty()) return true;

        List<String> in = new ArrayList<>();
        in.add(args[0]);
        in.addAll(Arrays.asList(args).subList(2, args.length));

        for (Player p : affectedPlayers) {
            if(args[0].equalsIgnoreCase("issue")){
                issue(p, in.toArray(new String[0]));
            }

            if(args[0].equalsIgnoreCase("buy")){
                TicketCommand.buy(p, in.toArray(new String[0]));
            }
        }
        return true;
    }

    /// Tab Completion do Later. (Or someone help me do.)
//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        if(!(sender instanceof CommandBlock)) return new ArrayList<>();
//        List<String> out = new ArrayList<>();
//
//        if(args.length == 1){
//            out.add("issue");
//            out.add("buy");
//            return out;
//        }
//        if(args[0].equalsIgnoreCase("issue") || args[0].equalsIgnoreCase("buy")){
//            if(args.length == 2){
//                for(Player p : Bukkit.getOnlinePlayers()){
//                    out.add(p.getName());
//                }
//                out.add("@p");
//                out.add("@a");
//                return out;
//            }
//            if(args.length == 3){
//                out.addAll(CompanyStore.CompanyTable.keySet());
//                return out;
//            }
//            if(args.length == 4){
//                out.addAll(TicketCommand.suggestions(args[2]));
//                return out;
//            }
//        }
//
//        if (args.length == 5) {
//            if (args[0].equalsIgnoreCase("issue")) {
//                out.addAll(CompanyStore.CompanyTable.keySet());
//            }
//            if (args[0].equalsIgnoreCase("buy")) {
//                out.addAll(TicketCommand.suggestions(args[2]));
//            }
//            return out;
//        }
//        if (args.length == 6) {
//            if (args[0].equalsIgnoreCase("issue")) {
//                out.addAll(TicketCommand.suggestions(args[4]));
//            }
//            return out;
//        }
//
//        return new ArrayList<>();
//    }

    //Copied from TicketCmd
    private void issue(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
        }

        //ticket issue <company> <from> <company> <to> <cost>(Optional) <hasEntered>(Optional)
        AbstractCompany sourceComp;
        AbstractCompany destComp;
        if (CompanyStore.CompanyTable.containsKey(args[1]) && CompanyStore.CompanyTable.containsKey(args[3])) {
            sourceComp = CompanyStore.CompanyTable.get(args[1]);
            destComp = CompanyStore.CompanyTable.get(args[3]);
        } else {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Company(s) not found");
            return;
        }

        ItemStack its;
        int fare1000 = 0;
        if (args.length > 5) {
            try {
                fare1000 = (int) Math.round(Double.parseDouble(args[5]) * 1000);
            } catch (NumberFormatException ignored) {
            }
        }
        its = Ticket.newTicket(sourceComp, destComp, args[2], args[4], fare1000);

        if (args.length > 6) {
            if (args[6].equalsIgnoreCase("true")) {
                Ticket ticket = new Ticket(its);
                ticket.entryProcedure();
            }
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, its);
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully issued a new ticket from " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + " to " + MetroConfiguration.INSTANCE.getInput() + args[4] + MetroConfiguration.INSTANCE.getBase() + " with price " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getInput() + fare1000 / 1000.0 + MetroConfiguration.INSTANCE.getBase() + "!");
                return;
            }
        }
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Your inventory is full!");
    }
}
