package com.hasunemiku2015.metrofare.company;

import com.hasunemiku2015.metrofare.lookuptables.datatables.DataTableStore;
import com.hasunemiku2015.metrofare.lookuptables.faretables.FareTableStore;
import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.MetroFare;
import com.hasunemiku2015.metrofare.ticketing.types.DebitCard;
import com.hasunemiku2015.metrofare.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CompanyCommand implements CommandExecutor, TabCompleter {
    static HashMap<Player, String> confirmedDeletions = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length == 0) {
            help(sender);
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("admin")) {
            if (MetroConfiguration.INSTANCE.hasAdminCompanyPermission(player)) {
                //cp admin reload
                if (args[1].equalsIgnoreCase("reload")) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                            "Reloading Company Serializable");
                    long startTime = System.currentTimeMillis();
                    Bukkit.getScheduler().runTaskAsynchronously(MetroFare.PLUGIN, () -> {
                        CompanyStore.reload();
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                                String.format("Reload completed in %d ms.", System.currentTimeMillis() - startTime));
                    });
                    return true;
                }

                //cp admin list
                if (args[1].equalsIgnoreCase("list")) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                            "All existing companies: ");
                    for (String i : CompanyStore.CompanyTable.keySet()) {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- " + i);
                    }
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("new")) {
            if (!MetroConfiguration.INSTANCE.hasCreateCompanyPermission(player)) {
                return true;
            }

            if (args.length == 2 || args.length == 3) {
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format");
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Correct Format: ");

                CompanyType gateType;
                try {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Company Type");
                    gateType = CompanyType.valueOf(args[1]);
                } catch (Exception ex) {
                    return true;
                }

                switch (gateType) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new ZONE <Name> <Multiplier> <Constant>");
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new ABS_COORDINATE <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new DIJKSTRA <Name> <DataTable Name>");
                        return true;
                    }

                    case UNIFORM: {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new UNIFORM <Name> <Uniform Fare>");
                        return true;
                    }

                    case FARE_TABLE: {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new FARE_TABLE <Name> <FareTable Name>");
                        return true;
                    }
                }
            }
            if (args.length >= 4) {
                switch (CompanyType.valueOf(args[1])) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format");
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Correct Format: ");
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new Zone <Name> <Multiplier> <Constant>");
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - company new Abs_Coordinate <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        if (DataTableStore.DataTables.containsKey(args[3])) {
                            newCompany.put("datatable", args[3]);
                        } else {
                            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot find DataTable with name: " + MetroConfiguration.INSTANCE.getInput() + args[3] + MetroConfiguration.INSTANCE.getError() + "!");
                            return true;
                        }
                        newCompany.put("type", CompanyType.DIJKSTRA);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(player.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, player, newCompany);
                        return true;
                    }

                    case UNIFORM: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        try {
                            newCompany.put("fare", Double.parseDouble(args[3]));
                        } catch (NumberFormatException e) {
                            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Fare must be a number!");
                        }
                        newCompany.put("type", CompanyType.UNIFORM);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(player.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, player, newCompany);
                        return true;
                    }

                    case FARE_TABLE: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        if (FareTableStore.FareTables.containsKey(args[3])) {
                            newCompany.put("faretable", args[3]);
                        } else {
                            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot find FareTable with name: " + MetroConfiguration.INSTANCE.getInput() + args[3] + MetroConfiguration.INSTANCE.getError() + "!");
                            return true;
                        }
                        newCompany.put("type", CompanyType.FARE_TABLE);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(player.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, player, newCompany);
                        return true;
                    }
                }
            }

            //args.length = 1
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Please Select a Company Type!");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Possible Company Types:");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- Zone");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- Abs_Coordinate");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- Dijkstra");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- Uniform");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- FareTable");
            return true;
        }

        //Identity Check
        if (args.length == 1) {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                    MetroConfiguration.INSTANCE.getError() + " Error: Please Specify Company Name!");
            return true;
        }
        AbstractCompany comp = CompanyStore.CompanyTable.get(args[1]);
        if (comp == null) {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                    MetroConfiguration.INSTANCE.getError() + " Error: The company specified does not exist!");
            return true;
        }
        if (!comp.hasOwner(player.getUniqueId().toString()) || !MetroConfiguration.INSTANCE.hasAdminCompanyPermission(player)) {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() +
                    MetroConfiguration.INSTANCE.getError() + " Error: You are not member of this company!");
            return true;
        }

        //Restricted Commands
        if (args[0].equalsIgnoreCase("info")) {
            showCompanyInfo(player, comp);
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (comp instanceof UniformCompany) {
                UniformCompany u = (UniformCompany) comp;
                try {
                    double set = Double.parseDouble(args[2]);
                    u.setFare(set);
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully set the uniform fare to $" + MetroConfiguration.INSTANCE.getInput() + set + MetroConfiguration.INSTANCE.getBase() + "!");
                } catch (Exception ex) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Please specify the uniform fare you want to set!");
                    return true;
                }
                return true;
            }

            if (comp instanceof ZoneAbsCompany) {
                ZoneAbsCompany z = (ZoneAbsCompany) comp;

                if (args[2].equalsIgnoreCase("multiplier")) {
                    try {
                        double set = Double.parseDouble(args[2]);
                        z.setMultiplier(set);
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully set the multiplier to " + MetroConfiguration.INSTANCE.getInput() + set + MetroConfiguration.INSTANCE.getBase() + "!");
                    } catch (Exception ex) {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else if (args[2].equalsIgnoreCase("constant")) {
                    try {
                        double set = Double.parseDouble(args[2]);
                        z.setConstant(set);
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully set the constant to " + MetroConfiguration.INSTANCE.getInput() + set + MetroConfiguration.INSTANCE.getBase() + "!");
                    } catch (Exception ex) {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Please specify what you want to set! (Multiplier or Constant?)");
                    return true;
                }
                return true;
            }

            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Company Type!");
            return true;
        }
        if (args[0].equalsIgnoreCase("revenue")) {
            if (args.length == 2 || args[2].equalsIgnoreCase("check")) {
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Revenue Account Balance: " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getOutput() + comp.getRevenue());
                return true;
            }
            if (args[2].equalsIgnoreCase("withdraw")) {
                if (args.length < 4) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Please Specify the amount to withdraw!");
                } else {
                    int amount1000 = (int) (Double.parseDouble(args[3]) * 1000);
                    double amount = amount1000 / 1000.0;
                    boolean b = comp.deductRevenue(amount);

                    if (b) {
                        if (VaultIntegration.vault) {
                            //Add to Bank Account if Vault
                            VaultIntegration.add(player, amount);
                        } else {
                            DebitCard dc = new DebitCard(player.getInventory().getItemInMainHand());
                            if (!dc.isValid()) {
                                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: You are not holding a debit card!");
                                return true;
                            }

                            int bal = dc.getBalance() + amount1000;
                            dc.setBalance(bal);
                        }

                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully withdraw " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getInput() + amount + MetroConfiguration.INSTANCE.getBase() + " from Revenue Account of " + MetroConfiguration.INSTANCE.getInput() + comp.getName());
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] New Balance: " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getOutput() + comp.getRevenue());
                        return true;
                    }

                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: There is no money in revenue account!");
                }
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("owners")) {
            //cp owners <CompName> <add/remove> <name>
            if (args.length < 4) {
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments");
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                Player add = Bukkit.getPlayer(args[3]);

                if (add == null) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The specified player is not online!");
                    return true;
                }

                if (!comp.hasOwner(add.getUniqueId().toString())) {
                    comp.addOwner(add.getUniqueId().toString());
                } else {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: This player is already owner of the company!");
                    return true;
                }

                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully added " + MetroConfiguration.INSTANCE.getInput() + add.getName() + MetroConfiguration.INSTANCE.getBase() + " to " + MetroConfiguration.INSTANCE.getInput() + comp.getName() + MetroConfiguration.INSTANCE.getBase() + "!");
                add.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " You have been added to " + MetroConfiguration.INSTANCE.getInput() + comp.getName() + MetroConfiguration.INSTANCE.getBase() + "!");
                return true;
            }

            if (args[2].equalsIgnoreCase("remove")) {
                OfflinePlayer remove = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));

                if (remove.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot remove that player, is he/she yourself? Does he/she exsist?");
                    return true;
                }
                boolean b = comp.removeOwner(remove.getUniqueId().toString(), player.getUniqueId().toString());
                if (b) {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully removed " + MetroConfiguration.INSTANCE.getInput() + remove.getName() + MetroConfiguration.INSTANCE.getBase() + " from " + MetroConfiguration.INSTANCE.getInput() + comp.getName() + MetroConfiguration.INSTANCE.getBase() + "!");
                    if (remove.isOnline()) {
                        ((Player) remove).sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] You have been removed from " + MetroConfiguration.INSTANCE.getInput() + comp.getName() + MetroConfiguration.INSTANCE.getBase() + "!");
                    }
                    return true;
                }
                return true;
            }

            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Please Specify Selector! (Add/Remove?)");
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (!confirmedDeletions.containsKey(player)) {
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Are you sure? Type /cp delete <companyName> confirm in 120s to confirm");
                confirmedDeletions.put(player, args[1]);
                Bukkit.getScheduler().runTaskLater(MetroFare.PLUGIN, () -> confirmedDeletions.remove(player), 7200);

            } else {
                if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                    boolean b = CompanyStore.delCompany(args[1]);
                    if (b) {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully deleted company " + MetroConfiguration.INSTANCE.getInput() + args[1] + MetroConfiguration.INSTANCE.getBase() + "!");
                    } else {
                        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot delete the company specified!");
                    }
                    confirmedDeletions.remove(player);
                }
            }
            return true;
        }

        help(sender);
        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        Player player = (Player) sender;
        boolean createCompany = MetroConfiguration.INSTANCE.hasCreateCompanyPermission(player);
        boolean monitorCompany = MetroConfiguration.INSTANCE.hasCreateCompanyPermission(player);

        if (args.length == 1) {
            if (createCompany) {
                out.add("new");
            }

            if (monitorCompany) {
                out.add("admin");
            }

            out.add("info");
            out.add("set");
            out.add("revenue");
            out.add("owners");
            out.add("delete");
            return out;
        }

        if (args.length == 2) {
            String firstArg = args[0].toLowerCase();
            switch (firstArg) {
                case "admin": {
                    if (monitorCompany) {
                        out.add("reload");
                        out.add("list");
                    }
                    break;
                }

                case "new" : {
                    if (createCompany) {
                        for (CompanyType i : CompanyType.values()) {
                            out.add(i.name());
                        }

                    }
                    break;
                }

                default: {
                    if (MetroConfiguration.INSTANCE.hasAdminCompanyPermission(player)) {
                        out.addAll(CompanyStore.CompanyTable.keySet());
                    } else {
                        out.addAll(CompanyStore.CompanyTable.keySet().stream()
                                .filter(s -> CompanyStore.CompanyTable.get(s).hasOwner(player.getUniqueId().toString()))
                                .collect(Collectors.toList()));
                    }
                }
            }

            return out;
        }

        if (args.length == 3) {
            switch (args[0]) {
                case "owners": {
                    out.add("add");
                    out.add("remove");
                    return out;
                }
                case "revenue": {
                    out.add("check");
                    out.add("withdraw");
                    return out;
                }
            }
        }
        if (args.length == 4) {
            switch (args[0]) {
                case "set":
                case "new": {
                    if (args[1].equalsIgnoreCase("dijkstra")) {
                        out.addAll(DataTableStore.DataTables.keySet());
                    } else if (args[1].equalsIgnoreCase("faretable")) {
                        out.addAll(FareTableStore.FareTables.keySet());
                    }
                    return out;
                }
                case "owners": {
                    if (args[2].equalsIgnoreCase("add")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            out.add(p.getName());
                        }
                    } else if (args[2].equalsIgnoreCase("remove")) {
                        AbstractCompany comp = CompanyStore.CompanyTable.get(args[1]);
                        if (comp != null) {
                            out.addAll(comp.getOwners());
                        }
                    }
                    return out;
                }
            }
        }
        return out;
    }

    //Dupe Code Method
    private void help(CommandSender sender) {
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Company Commands:");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- help: Display this page");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- new: Creates a new company");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- info: Gets the information of a certain company");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- revenue: Interact with revenue account of a certain company");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- deleteComp: Deletes a certain company");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- owners: Interact with list of owners in a company");
    }

    private void CheckNameExist(String[] args, Player p, HashMap<String, Object> newCompany) {
        if (CompanyStore.CompanyTable.containsKey(args[2])) {
            p.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot create company,name already in use!");
            return;
        }
        boolean b = CompanyStore.newCompany(newCompany);
        if (!b) {
            p.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot create company, an unexpected error occurred.");
            return;
        }

        p.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Successfully created new company with name " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + "!");
        Bukkit.getScheduler().runTaskAsynchronously(MetroFare.PLUGIN, CompanyStore::reload);
    }

    private void showCompanyInfo(Player player, AbstractCompany company) {
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "[MetroFare] Company Info:");
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Name: " + company.getName());
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Type: " + company.getType().toString());
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Revenue Account Balance: " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getOutput() + company.getRevenue());
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Owners:");

        List<String> owners = company.getOwners();
        for (String s : owners) {
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- " + s + " (" + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + ")");
        }

        //Type Specific Info
        switch (company.getType()) {
            case ZONE:
            case ABS_COORDINATE: {
                ZoneAbsCompany corp = (ZoneAbsCompany) company;
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Fare is Calculated by: Multiplier x Difference + Constant");
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Multiplier: " + corp.getMultiplier());
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Constant: " + corp.getConstant());
                return;
            }

            case DIJKSTRA: {
                DijkstraCompany corp = (DijkstraCompany) company;
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Data Table Name: " + corp.getDataTable().getName());
                return;
            }

            case UNIFORM: {
                UniformCompany corp = (UniformCompany) company;
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Uniform Fare: " + corp.getFare());
                return;
            }

            case FARE_TABLE: {
                FareTableCompany corp = (FareTableCompany) company;
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Fare Table Name: " + corp.getFareTableName());
                return;
            }
        }
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " An unexpected Error Occurred.");
    }
}
