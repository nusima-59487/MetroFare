package com.hasunemiku2015.metrofare.Company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hasunemiku2015.metrofare.Gate.GateType;
import com.hasunemiku2015.metrofare.LookUpTables.DataTables.DataTableStore;
import com.hasunemiku2015.metrofare.LookUpTables.FareTables.FareTableStore;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import com.hasunemiku2015.metrofare.Ticketing.Types.DebitCard;
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

public class CompanyCommand implements CommandExecutor, TabCompleter {
    static HashMap<Player, String> confirmedDeletions = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length == 0) {
            help(sender);
            return true;
        }
        Player p = (Player) sender;

        if (args[0].equalsIgnoreCase("dev")) {
            //Identity Check
            if (p.getUniqueId().toString().equals("2b31c5cb-4792-47f9-b62f-ca1278d589c5") && p.isOp()) {
                //cp dev testcomp
                if (args[1].equalsIgnoreCase("testcomp")) {
                    MTFA.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");

                    HashMap<String, Object> in = new HashMap<>();
                    in.put("name", "MikuTek");
                    in.put("type", GateType.UNIFORM);
                    List<String> Owners = new ArrayList<>();
                    Owners.add("2b31c5cb-4792-47f9-b62f-ca1278d589c5");
                    in.put("owners", Owners);

                    in.put("fare", Double.parseDouble("1"));
                    CompanyStore.newCompany(in);
                    CompanyStore.reload();

                    MTFA.PLUGIN.getLogger().warning("Created New Example Company \"MikuTek\"");
                    return true;
                }

                //cp dev info
                if (args[1].equalsIgnoreCase("info")) {
                    Object o = CompanyStore.CompanyTable.get(args[2]);
                    MTFA.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");
                    Gson gson = new GsonBuilder().create();
                    MTFA.PLUGIN.getLogger().warning(gson.toJson(o));
                    return true;
                }

                //cp dev reload
                if (args[1].equalsIgnoreCase("reload")) {
                    MTFA.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");
                    MTFA.PLUGIN.getLogger().warning("Reloading Company Serializable");
                    Bukkit.getScheduler().runTaskAsynchronously(MTFA.PLUGIN, CompanyStore::reload);
                    return true;
                }

                //cp dev delete <string>
                if (args[1].equalsIgnoreCase("delete")) {
                    MTFA.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");
                    MTFA.PLUGIN.getLogger().warning("Reloading Company Serializable");
                    boolean b = CompanyStore.delCompany(args[2]);
                    MTFA.PLUGIN.getLogger().warning("Company Deleted: " + b);
                    return true;
                }

                //cp dev list
                if (args[1].equalsIgnoreCase("list")) {
                    MTFA.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");

                    MTFA.PLUGIN.getLogger().warning("All existing companies");
                    for (String i : CompanyStore.CompanyTable.keySet()) {
                        MTFA.PLUGIN.getLogger().warning(i);
                    }
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("new")) {
            if (args.length == 2 || args.length == 3) {
                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Format");
                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Correct Format: ");

                GateType gateType;
                try {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Company Type");
                    gateType = GateType.valueOf(args[1]);
                } catch (Exception ex) {
                    return true;
                }

                switch (gateType) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new ZONE <Name> <Multiplier> <Constant>");
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new ABS_COORDINATE <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new DIJKSTRA <Name> <DataTable Name>");
                        return true;
                    }

                    case UNIFORM: {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new UNIFORM <Name> <Uniform Fare>");
                        return true;
                    }

                    case FARE_TABLE: {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new FARE_TABLE <Name> <FareTable Name>");
                        return true;
                    }
                }
            }
            if (args.length >= 4) {
                switch (GateType.valueOf(args[1])) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Format");
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Correct Format: ");
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new Zone <Name> <Multiplier> <Constant>");
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " - company new Abs_Coordinate <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        if (DataTableStore.DataTables.containsKey(args[3])) {
                            newCompany.put("datatable", args[3]);
                        } else {
                            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot find DataTable with name: " + MFConfig.INSTANCE.getInput() + args[3] + MFConfig.INSTANCE.getError() + "!");
                            return true;
                        }
                        newCompany.put("type", GateType.DIJKSTRA);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(p.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, p, newCompany);
                        return true;
                    }

                    case UNIFORM: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        try {
                            newCompany.put("fare", Double.parseDouble(args[3]));
                        } catch (NumberFormatException e) {
                            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Fare must be a number!");
                        }
                        newCompany.put("type", GateType.UNIFORM);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(p.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, p, newCompany);
                        return true;
                    }

                    case FARE_TABLE: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        if (FareTableStore.FareTables.containsKey(args[3])) {
                            newCompany.put("faretable", args[3]);
                        } else {
                            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot find FareTable with name: " + MFConfig.INSTANCE.getInput() + args[3] + MFConfig.INSTANCE.getError() + "!");
                            return true;
                        }
                        newCompany.put("type", GateType.FARE_TABLE);

                        List<String> playerNames = new ArrayList<>();
                        playerNames.add(p.getUniqueId().toString());
                        newCompany.put("owners", playerNames);

                        //Name Check
                        CheckNameExist(args, p, newCompany);
                        return true;
                    }
                }
            }

            //args.length = 1
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Please Select a Company Type!");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Possible Company Types:");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "- Zone");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "- Abs_Coordinate");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "- Dijkstra");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "- Uniform");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "- FareTable");
            return true;
        }

        //Identity Check
        if (args.length == 1) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Please Specify Company Name!");
            return true;
        }
        AbstractCompany comp = CompanyStore.CompanyTable.get(args[1]);
        if (comp == null) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: The company specified does not exist!");
            return true;
        }
        if (!comp.hasOwner(p.getUniqueId().toString())) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not member of this company!");
            return true;
        }

        //Restricted Cmds
        if (args[0].equalsIgnoreCase("info")) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Company Info:");
            p.sendMessage(MFConfig.INSTANCE.getBase() + "Name: " + comp.getName());
            p.sendMessage(MFConfig.INSTANCE.getBase() + "Type: " + comp.getType().toString());
            p.sendMessage(MFConfig.INSTANCE.getBase() + "Revenue Account Balance: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + comp.getRevenue());
            p.sendMessage(MFConfig.INSTANCE.getBase() + "Owners:");

            List<String> owners = comp.getOwners();
            for (String s : owners) {
                p.sendMessage(MFConfig.INSTANCE.getBase() + "- " + s + " (" + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + ")");
            }

            //Type Specific Info
            switch (comp.getType()) {
                case ZONE:
                case ABS_COORDINATE: {
                    ZoneAbsCompany corp = (ZoneAbsCompany) comp;
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Fare is Calculated by: Multiplier x Difference + Constant");
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Multiplier: " + corp.getMultiplier());
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Constant: " + corp.getConstant());
                    return true;
                }

                case DIJKSTRA: {
                    DijkstraCompany corp = (DijkstraCompany) comp;
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Data Table Name: " + corp.getDataTable().getName());
                    return true;
                }

                case UNIFORM: {
                    UniformCompany corp = (UniformCompany) comp;
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Uniform Fare: " + corp.getFare());
                    return true;
                }

                case FARE_TABLE: {
                    FareTableCompany corp = (FareTableCompany) comp;
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "Fare Table Name: " + corp.getFareTableName());
                    return true;
                }
            }
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " An unexpected Error Occurred.");
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (comp instanceof UniformCompany) {
                UniformCompany u = (UniformCompany) comp;
                try {
                    double set = Double.parseDouble(args[2]);
                    u.setFare(set);
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully set the uniform fare to $" + MFConfig.INSTANCE.getInput() + set + MFConfig.INSTANCE.getBase() + "!");
                } catch (Exception ex) {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Please specify the uniform fare you want to set!");
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
                        p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully set the multiplier to " + MFConfig.INSTANCE.getInput() + set + MFConfig.INSTANCE.getBase() + "!");
                    } catch (Exception ex) {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else if (args[2].equalsIgnoreCase("constant")) {
                    try {
                        double set = Double.parseDouble(args[2]);
                        z.setConstant(set);
                        p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully set the constant to " + MFConfig.INSTANCE.getInput() + set + MFConfig.INSTANCE.getBase() + "!");
                    } catch (Exception ex) {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Please specify what you want to set! (Multiplier or Constant?)");
                    return true;
                }
                return true;
            }

            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Invalid Company Type!");
            return true;
        }
        if (args[0].equalsIgnoreCase("revenue")) {
            if (args.length == 2 || args[2].equalsIgnoreCase("check")) {
                p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Revenue Account Balance: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + comp.getRevenue());
                return true;
            }
            if (args[2].equalsIgnoreCase("withdraw")) {
                if (args.length < 4) {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Please Specify the amount to withdraw!");
                } else {
                    int amount1000 = (int) (Double.parseDouble(args[3]) * 1000);
                    double amount = amount1000 / 1000.0;
                    boolean b = comp.deductRevenue(amount);

                    if (b) {
                        if (VaultIntegration.vault) {
                            //Add to Bank Account if Vault
                            VaultIntegration.add(p, amount);
                        } else {
                            DebitCard dc = new DebitCard(p.getInventory().getItemInMainHand());
                            if (!dc.isValid()) {
                                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: You are not holding a debit card!");
                                return true;
                            }

                            int bal = dc.getBalance() + amount1000;
                            dc.setBalance(bal);
                        }

                        p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully withdraw " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getInput() + amount + MFConfig.INSTANCE.getBase() + " from Revenue Account of " + MFConfig.INSTANCE.getInput() + comp.getName());
                        p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] New Balance: " + MFConfig.INSTANCE.getCurrencyUnit() + MFConfig.INSTANCE.getOutput() + comp.getRevenue());
                        return true;
                    }

                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: There is no money in revenue account!");
                }
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("owners")) {
            //cp owners <CompName> <add/remove> <name>
            if (args.length < 4) {
                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Insufficient Arguments");
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                Player add = Bukkit.getPlayer(args[3]);

                if (add == null) {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: The specified player is not online!");
                    return true;
                }

                if (!comp.hasOwner(add.getUniqueId().toString())) {
                    comp.addOwner(add.getUniqueId().toString());
                } else {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: This player is already owner of the company!");
                    return true;
                }

                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully added " + MFConfig.INSTANCE.getInput() + add.getName() + MFConfig.INSTANCE.getBase() + " to " + MFConfig.INSTANCE.getInput() + comp.getName() + MFConfig.INSTANCE.getBase() + "!");
                add.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " You have been added to " + MFConfig.INSTANCE.getInput() + comp.getName() + MFConfig.INSTANCE.getBase() + "!");
                return true;
            }

            if (args[2].equalsIgnoreCase("remove")) {
                OfflinePlayer remove = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));

                if (remove.getUniqueId().equals(p.getUniqueId())) {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot remove that player, is he/she yourself? Does he/she exsist?");
                    return true;
                }
                boolean b = comp.removeOwner(remove.getUniqueId().toString(), p.getUniqueId().toString());
                if (b) {
                    p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully removed " + MFConfig.INSTANCE.getInput() + remove.getName() + MFConfig.INSTANCE.getBase() + " from " + MFConfig.INSTANCE.getInput() + comp.getName() + MFConfig.INSTANCE.getBase() + "!");
                    if (remove.isOnline()) {
                        ((Player) remove).sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] You have been removed from " + MFConfig.INSTANCE.getInput() + comp.getName() + MFConfig.INSTANCE.getBase() + "!");
                    }
                    return true;
                }
                return true;
            }

            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Please Specify Selector! (Add/Remove?)");
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (!confirmedDeletions.containsKey(p)) {
                p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Are you sure? Type /cp delete <companyName> confirm in 120s to confirm");
                confirmedDeletions.put(p, args[1]);
                Bukkit.getScheduler().runTaskLater(MTFA.PLUGIN, () -> confirmedDeletions.remove(p), 7200);

            } else {
                if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                    boolean b = CompanyStore.delCompany(args[1]);
                    if (b) {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Successfully deleted company " + MFConfig.INSTANCE.getInput() + args[1] + MFConfig.INSTANCE.getBase() + "!");
                    } else {
                        p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot delete the company specified!");
                    }
                    confirmedDeletions.remove(p);
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
        if (args.length == 1) {
            out.add("new");
            out.add("info");
            out.add("set");
            out.add("revenue");
            out.add("owners");
            out.add("delete");
            return out;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("new")) {
                for (GateType i : GateType.values()) {
                    out.add(i.name());
                }
                return out;
            }
            out.addAll(CompanyStore.CompanyTable.keySet());
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
        sender.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + " Company Commands:");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- help: Display this page");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- new: Creates a new company");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- info: Gets the information of a certain company");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- revenue: Interact with revenue account of a certain company");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- deleteComp: Deletes a certain company");
        sender.sendMessage(MFConfig.INSTANCE.getBase() + "- owners: Interact with list of owners in a company");
    }

    private void CheckNameExist(String[] args, Player p, HashMap<String, Object> newCompany) {
        if (CompanyStore.CompanyTable.containsKey(args[2])) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot create company,name already in use!");
            return;
        }
        boolean b = CompanyStore.newCompany(newCompany);
        if (!b) {
            p.sendMessage(MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getPrefix() + MFConfig.INSTANCE.getError() + " Error: Cannot create company, an unexpected error occurred.");
            return;
        }

        p.sendMessage(MFConfig.INSTANCE.getBase() + "[MetroFare] Successfully created new company with name " + MFConfig.INSTANCE.getInput() + args[2] + MFConfig.INSTANCE.getBase() + "!");
        Bukkit.getScheduler().runTaskAsynchronously(MTFA.PLUGIN, CompanyStore::reload);
    }
}
