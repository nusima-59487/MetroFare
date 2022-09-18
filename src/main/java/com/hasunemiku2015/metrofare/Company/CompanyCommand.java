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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompanyCommand implements CommandExecutor, TabCompleter {
    static HashMap<Player, String> confirmedDeletions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
                    MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");

                    HashMap<String, Object> in = new HashMap<>();
                    in.put("name", "MikuTek");
                    in.put("type", GateType.UNIFORM);
                    List<String> Owners = new ArrayList<>();
                    Owners.add("2b31c5cb-4792-47f9-b62f-ca1278d589c5");
                    in.put("owners", Owners);

                    in.put("fare", Double.parseDouble("1"));
                    CompanyStore.newCompany(in);
                    CompanyStore.reload();

                    MTFA.plugin.getLogger().warning("Created New Example Company \"MikuTek\"");
                    return true;
                }

                //cp dev info
                if (args[1].equalsIgnoreCase("info")) {
                    Object o = CompanyStore.CompanyTable.get(args[2]);
                    MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");
                    Gson gson = new GsonBuilder().create();
                    MTFA.plugin.getLogger().warning(gson.toJson(o));
                    return true;
                }

                //cp dev reload
                if (args[1].equalsIgnoreCase("reload")) {
                    MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");
                    MTFA.plugin.getLogger().warning("Reloading Company Serializable");
                    Bukkit.getScheduler().runTaskAsynchronously(MTFA.plugin, CompanyStore::reload);
                    return true;
                }

                //cp dev delete <string>
                if (args[1].equalsIgnoreCase("delete")) {
                    MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");
                    MTFA.plugin.getLogger().warning("Reloading Company Serializable");
                    boolean b = CompanyStore.delCompany(args[2]);
                    MTFA.plugin.getLogger().warning("Company Deleted: " + b);
                    return true;
                }

                //cp dev list
                if (args[1].equalsIgnoreCase("list")) {
                    MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");

                    MTFA.plugin.getLogger().warning("All existing companies");
                    for (String i : CompanyStore.CompanyTable.keySet()) {
                        MTFA.plugin.getLogger().warning(i);
                    }
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("new")) {
            if (args.length == 2 || args.length == 3) {
                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Format");
                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Correct Format: ");

                GateType gateType;
                try {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Company Type");
                    gateType = GateType.valueOf(args[1]);
                } catch (Exception ex) {
                    return true;
                }

                switch (gateType) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new ZONE <Name> <Multiplier> <Constant>");
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new ABS_COORDINATE <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new DIJKSTRA <Name> <DataTable Name>");
                        return true;
                    }

                    case UNIFORM: {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new UNIFORM <Name> <Uniform Fare>");
                        return true;
                    }

                    case FARE_TABLE: {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new FARE_TABLE <Name> <FareTable Name>");
                        return true;
                    }
                }
            }
            if (args.length >= 4) {
                switch (GateType.valueOf(args[1])) {
                    case ZONE:
                    case ABS_COORDINATE: {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Format");
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Correct Format: ");
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new Zone <Name> <Multiplier> <Constant>");
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - company new Abs_Coordinate <Name> <Multiplier> <Constant>");
                        return true;
                    }

                    case DIJKSTRA: {
                        HashMap<String, Object> newCompany = new HashMap<>();
                        newCompany.put("name", args[2]);
                        if (DataTableStore.DataTables.containsKey(args[3])) {
                            newCompany.put("datatable", args[3]);
                        } else {
                            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot find DataTable with name: " + MFConfig.getInput() + args[3] + MFConfig.getError() + "!");
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
                            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Fare must be a number!");
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
                            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot find FareTable with name: " + MFConfig.getInput() + args[3] + MFConfig.getError() + "!");
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
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please Select a Company Type!");
            p.sendMessage(MFConfig.getBase() + "[MetroFare] Possible Company Types:");
            p.sendMessage(MFConfig.getBase() + "- Zone");
            p.sendMessage(MFConfig.getBase() + "- Abs_Coordinate");
            p.sendMessage(MFConfig.getBase() + "- Dijkstra");
            p.sendMessage(MFConfig.getBase() + "- Uniform");
            p.sendMessage(MFConfig.getBase() + "- FareTable");
            return true;
        }

        //Identity Check
        if (args.length == 1) {
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please Specify Company Name!");
            return true;
        }
        AbstractCompany comp = CompanyStore.CompanyTable.get(args[1]);
        if (comp == null) {
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: The company specified does not exist!");
            return true;
        }
        if (!comp.hasOwner(p.getUniqueId().toString())) {
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not member of this company!");
            return true;
        }

        //Restricted Cmds
        if (args[0].equalsIgnoreCase("info")) {
            p.sendMessage(MFConfig.getBase() + "[MetroFare] Company Info:");
            p.sendMessage(MFConfig.getBase() + "Name: " + comp.getName());
            p.sendMessage(MFConfig.getBase() + "Type: " + comp.getType().toString());
            p.sendMessage(MFConfig.getBase() + "Revenue Account Balance: " + MFConfig.getCurrencyUnit() + MFConfig.getOutput() + comp.getRevenue());
            p.sendMessage(MFConfig.getBase() + "Owners:");

            List<String> owners = comp.getOwners();
            for (String s : owners) {
                p.sendMessage(MFConfig.getBase() + "- " + s + " (" + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName() + ")");
            }

            //Type Specific Info
            switch (comp.getType()) {
                case ZONE:
                case ABS_COORDINATE: {
                    ZoneAbsCompany corp = (ZoneAbsCompany) comp;
                    p.sendMessage(MFConfig.getBase() + "Fare is Calculated by: Multiplier x Difference + Constant");
                    p.sendMessage(MFConfig.getBase() + "Multiplier: " + corp.getMultiplier());
                    p.sendMessage(MFConfig.getBase() + "Constant: " + corp.getConstant());
                    return true;
                }

                case DIJKSTRA: {
                    DijkstraCompany corp = (DijkstraCompany) comp;
                    p.sendMessage(MFConfig.getBase() + "Data Table Name: " + corp.getDataTable().getName());
                    return true;
                }

                case UNIFORM: {
                    UniformCompany corp = (UniformCompany) comp;
                    p.sendMessage(MFConfig.getBase() + "Uniform Fare: " + corp.getFare());
                    return true;
                }

                case FARE_TABLE: {
                    FareTableCompany corp = (FareTableCompany) comp;
                    p.sendMessage(MFConfig.getBase() + "Fare Table Name: " + corp.getFareTableName());
                    return true;
                }
            }
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " An unexpected Error Occurred.");
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (comp instanceof UniformCompany) {
                UniformCompany u = (UniformCompany) comp;
                try {
                    double set = Double.parseDouble(args[2]);
                    u.setFare(set);
                    p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully set the uniform fare to $" + MFConfig.getInput() + set + MFConfig.getBase() + "!");
                } catch (Exception ex) {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Please specify the uniform fare you want to set!");
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
                        p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully set the multiplier to " + MFConfig.getInput() + set + MFConfig.getBase() + "!");
                    } catch (Exception ex) {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else if (args[2].equalsIgnoreCase("constant")) {
                    try {
                        double set = Double.parseDouble(args[2]);
                        z.setConstant(set);
                        p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully set the constant to " + MFConfig.getInput() + set + MFConfig.getBase() + "!");
                    } catch (Exception ex) {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Format, is it a Number?");
                        return true;
                    }
                } else {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please specify what you want to set! (Multiplier or Constant?)");
                    return true;
                }
                return true;
            }

            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Company Type!");
            return true;
        }
        if (args[0].equalsIgnoreCase("revenue")) {
            if (args.length == 2 || args[2].equalsIgnoreCase("check")) {
                p.sendMessage(MFConfig.getBase() + "[MetroFare] Revenue Account Balance: " + MFConfig.getCurrencyUnit() + MFConfig.getOutput() + comp.getRevenue());
                return true;
            }
            if (args[2].equalsIgnoreCase("withdraw")) {
                if (args.length < 4) {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please Specify the amount to withdraw!");
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
                                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: You are not holding a debit card!");
                                return true;
                            }

                            int bal = dc.getBalance() + amount1000;
                            dc.setBalance(bal);
                        }

                        p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully withdraw " + MFConfig.getCurrencyUnit() + MFConfig.getInput() + amount + MFConfig.getBase() + " from Revenue Account of " + MFConfig.getInput() + comp.getName());
                        p.sendMessage(MFConfig.getBase() + "[MetroFare] New Balance: " + MFConfig.getCurrencyUnit() + MFConfig.getOutput() + comp.getRevenue());
                        return true;
                    }

                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: There is no money in revenue account!");
                }
                return true;
            }
        }
        if (args[0].equalsIgnoreCase("owners")) {
            //cp owners <CompName> <add/remove> <name>
            if (args.length < 4) {
                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Insufficient Arguments");
                return true;
            }

            if (args[2].equalsIgnoreCase("add")) {
                Player add = Bukkit.getPlayer(args[3]);

                if (add == null) {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: The specified player is not online!");
                    return true;
                }

                if (!comp.hasOwner(add.getUniqueId().toString())) {
                    comp.addOwner(add.getUniqueId().toString());
                } else {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: This player is already owner of the company!");
                    return true;
                }

                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully added " + MFConfig.getInput() + add.getName() + MFConfig.getBase() + " to " + MFConfig.getInput() + comp.getName() + MFConfig.getBase() + "!");
                add.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " You have been added to " + MFConfig.getInput() + comp.getName() + MFConfig.getBase() + "!");
                return true;
            }

            if (args[2].equalsIgnoreCase("remove")) {
                OfflinePlayer remove = Bukkit.getOfflinePlayer(UUID.fromString(args[3]));

                if (remove.getUniqueId().equals(p.getUniqueId())) {
                    p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot remove that player, is he/she yourself? Does he/she exsist?");
                    return true;
                }
                boolean b = comp.removeOwner(remove.getUniqueId().toString(), p.getUniqueId().toString());
                if (b) {
                    p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully removed " + MFConfig.getInput() + remove.getName() + MFConfig.getBase() + " from " + MFConfig.getInput() + comp.getName() + MFConfig.getBase() + "!");
                    if (remove.isOnline()) {
                        ((Player) remove).sendMessage(MFConfig.getBase() + "[MetroFare] You have been removed from " + MFConfig.getInput() + comp.getName() + MFConfig.getBase() + "!");
                    }
                    return true;
                }
                return true;
            }

            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please Specify Selector! (Add/Remove?)");
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (!confirmedDeletions.containsKey(p)) {
                p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Are you sure? Type /cp delete <companyName> confirm in 120s to confirm");
                confirmedDeletions.put(p, args[1]);
                Bukkit.getScheduler().runTaskLater(MTFA.plugin, () -> confirmedDeletions.remove(p), 7200);

            } else {
                if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                    boolean b = CompanyStore.delCompany(args[1]);
                    if (b) {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully deleted company " + MFConfig.getInput() + args[1] + MFConfig.getBase() + "!");
                    } else {
                        p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot delete the company specified!");
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
        sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Company Commands:");
        sender.sendMessage(MFConfig.getBase() + "- help: Display this page");
        sender.sendMessage(MFConfig.getBase() + "- new: Creates a new company");
        sender.sendMessage(MFConfig.getBase() + "- info: Gets the information of a certain company");
        sender.sendMessage(MFConfig.getBase() + "- revenue: Interact with revenue account of a certain company");
        sender.sendMessage(MFConfig.getBase() + "- deleteComp: Deletes a certain company");
        sender.sendMessage(MFConfig.getBase() + "- owners: Interact with list of owners in a company");
    }

    private void CheckNameExist(String[] args, Player p, HashMap<String, Object> newCompany) {
        if (CompanyStore.CompanyTable.containsKey(args[2])) {
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot create company,name already in use!");
            return;
        }
        boolean b = CompanyStore.newCompany(newCompany);
        if (!b) {
            p.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot create company, an unexpected error occurred.");
            return;
        }

        p.sendMessage(MFConfig.getBase() + "[MetroFare] Successfully created new company with name " + MFConfig.getInput() + args[2] + MFConfig.getBase() + "!");
        Bukkit.getScheduler().runTaskAsynchronously(MTFA.plugin, CompanyStore::reload);
    }
}
