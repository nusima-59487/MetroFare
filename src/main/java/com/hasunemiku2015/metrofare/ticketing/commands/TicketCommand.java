package com.hasunemiku2015.metrofare.ticketing.commands;

import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.company.DijkstraCompany;
import com.hasunemiku2015.metrofare.company.FareTableCompany;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.ticketing.types.Ticket;
import com.hasunemiku2015.metrofare.VaultIntegration;
import de.vogella.algorithms.dijkstra.model.Vertex;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TicketCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (MFConfig.INSTANCE.noTicketingPermission((Player) sender)) return true;
        Player player = (Player) sender;
        if (args.length == 0) {
            help(player);
            return true;
        }

        switch (args[0]) {
            case "info":
                info(player);
                return true;
            case "issue":
                issue(player, args);
                return true;
            case "buy":
                buy(player, args);
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
        if (args.length == 1) {
            out.add("help");
            out.add("info");
            out.add("issue");
            out.add("buy");
            return out;
        }

        //ticket issue <company> <from> <company> <to> <cost>(Optional)
        //ticket buy <company> <from> <to>
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("issue") || args[0].equalsIgnoreCase("buy")) {
                out.addAll(CompanyStore.CompanyTable.keySet());
            }
            return out;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("issue") || args[0].equalsIgnoreCase("buy")) {
                out.addAll(suggestions(args[1]));
            }
            return out;
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("issue")) {
                out.addAll(CompanyStore.CompanyTable.keySet());
            }
            if(args[0].equalsIgnoreCase("buy")){
                out.addAll(suggestions(args[1]));
            }
            return out;
        }
        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("issue")) {
                out.addAll(suggestions(args[3]));
            }
            return out;
        }

        return new ArrayList<>();
    }

    //Private methods for every command.
    private void help(Player player) {
        //ticket help
        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Ticket Commands: ");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "-help: Display this page.");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "-info: Check the info of the ticket held");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "-issue: Issue a new ticket");
        player.sendMessage(MFConfig.INSTANCE.getBase() + "-buy: Buy a new ticket (Travel within one company)");
    }
    private void info(Player player) {
        //ticket info
        ItemStack hand = player.getInventory().getItemInMainHand();
        Ticket ticket;
        try {
            ticket = new Ticket(hand);
        } catch (Exception ex) {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a ticket!");
            return;
        }

        if (ticket.isValid()) {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Ticket has the following properties: ");
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-source company: " + ticket.getCompanyFrom());
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-source info: " + ticket.getEntryData());
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-destination company: " + ticket.getCompanyTo());
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-destination info: " + ticket.getExitData());
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-ticket price: " + ticket.getFare() / 1000.0);
            player.sendMessage(MFConfig.INSTANCE.getBase() + "-entered gate: " + ticket.hasEntered());
            return;
        }

        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a ticket!");
    }
    private void issue(Player player, String[] args) {
        if (args.length < 5) {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Insufficient Arguments!");
            return;
        }

        //ticket issue <company> <from> <company> <to> <cost>(Optional) <hasEntered>(Optional)
        AbstractCompany sourceComp;
        AbstractCompany destComp;
        if (CompanyStore.CompanyTable.containsKey(args[1]) && CompanyStore.CompanyTable.containsKey(args[3])) {
            sourceComp = CompanyStore.CompanyTable.get(args[1]);
            destComp = CompanyStore.CompanyTable.get(args[3]);
        } else {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Company(s) not found");
            return;
        }

        if(!(sourceComp.hasOwner(player.getUniqueId().toString()) && destComp.hasOwner(player.getUniqueId().toString()))){
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not owner of both companies!");
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
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully issued a new ticket from " + MFConfig.INSTANCE.getInput() + args[2] + MFConfig.INSTANCE.getBase() + " to " + MFConfig.INSTANCE.getInput() + args[4] + MFConfig.INSTANCE.getBase() + " with price " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getInput() + fare1000 / 1000.0 + MFConfig.INSTANCE.getBase() + "!");
                return;
            }
        }
        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Your inventory is full!");
    }
    protected static void buy(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Insufficient Arguments!");
            return;
        }

        //ticket buy <company> <from> <to>
        AbstractCompany sourceComp;
        if (CompanyStore.CompanyTable.containsKey(args[1])) {
            sourceComp = CompanyStore.CompanyTable.get(args[1]);
        } else {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Company not found");
            return;
        }

        int fare1000 = sourceComp.computeFare(args[2], args[3]);
        if (fare1000 < 0) {
            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid entry/exit data!");
            return;
        }

        ItemStack its = Ticket.newTicket(sourceComp, args[2], args[3], fare1000);
        Inventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null) {
                if (VaultIntegration.vault) {
                    try {
                        if (VaultIntegration.hasEnough(player,fare1000 / 1000.0)) {
                            VaultIntegration.deduct(player,fare1000 / 1000.0);
                        } else {
                            player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You don't have enough money!");
                        }
                    } catch (Exception ex) {
                        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: An unexpected error occurred.");
                        return;
                    }
                }

                inv.setItem(i, its);
                sourceComp.addRevenue(fare1000/1000.0);
                player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully issued a new ticket from " + MFConfig.INSTANCE.getInput() + args[2] + MFConfig.INSTANCE.getBase() + " to " + MFConfig.INSTANCE.getInput() + args[3] + MFConfig.INSTANCE.getBase() + "!");
                return;
            }
        }
        player.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Your inventory is full!");
    }

    //Protected method for duplicated code
    protected static List<String> suggestions(String str) {
        List<String> out = new ArrayList<>();

        if (CompanyStore.CompanyTable.containsKey(str)) {
            switch (CompanyStore.CompanyTable.get(str).getType()) {
                case UNIFORM: {
                    out.add("Uniform");
                    break;
                }
                case DIJKSTRA: {
                    DijkstraCompany dc = (DijkstraCompany) CompanyStore.CompanyTable.get(str);
                    for (Vertex v : dc.getDataTable().getVertices()) {
                        out.add(v.getName());
                    }
                    break;
                }
                case FARE_TABLE: {
                    FareTableCompany fc = (FareTableCompany) CompanyStore.CompanyTable.get(str);
                    out.addAll(fc.getFareTable().getKeys());
                    break;
                }
            }
        }
        return out;
    }
}
