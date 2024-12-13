package com.hasunemiku2015.metrofare.lookuptables.datatables;

import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.MetroFare;
import de.vogella.algorithms.dijkstra.model.Vertex;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DTCommand implements CommandExecutor,TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(!MetroConfiguration.INSTANCE.hasDataTablePermission((Player) sender)){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Permission.");
        }

        if(args.length == 0){
            help(sender);
            return true;
        }
        switch(args[0]){
            case "help":
                help(sender);
                return true;

            case "save":
                reload();
                return true;

            case "add":
                addEdge(sender,args);
                return true;

            case "remove":
                removeEdge(sender,args);
                return true;

            case "new":
                try {
                    createFile(sender,args);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            case "calculate":
                calculate(sender,args);
                return true;

            case "checkPW":
                checkPW(sender,args);
                return true;
            case "delete":
                delFile(sender,args);
                return true;
            case "confirm":
                delFileConfirm(sender);
                return true;
            case "download":
                download(sender,args);
                return true;
        }
        help(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        if(args.length == 1){
             out.add("help");
             out.add("save");
             out.add("calculate");
             out.add("add");
             out.add("remove");
             out.add("new");
             out.add("delete");
             out.add("download");
            return out;
        }

        if(args.length == 2){
            switch(args[0]){
                case "add":
                case "delete":
                case "remove":
                case "calculate" :
                    out.addAll(DataTableStore.DataTables.keySet());
                    return out;
            }
        }

        if(args.length == 4 || args.length == 5){
            switch(args[0]){
                case "add":
                case "remove":
                case "calculate" :
                    if(DataTableStore.DataTables.get(args[1]) != null){
                        List<Vertex> vertices = DataTableStore.DataTables.get(args[1]).getVertices();
                        for(Vertex v : vertices){
                            out.add(v.getName());
                        }
                    }
                    return out;
            }
        }
        return new ArrayList<>();
    }


    //Command Method
    private void help(CommandSender sender){
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " DataTable Commands:");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- help: Display this page");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- save: Saves all current datatable edits to files");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- new: Creates new datatable file");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- delete: Deletes a datatable file");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- add: Add a new edge entry to the datatable specified");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- remove: Remove a edge entry from the datatable specified");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- calculate: Calculate the minimum weight from a vertex to a vertex in a datatable");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- download: Download new datatable from PasteBin");
    }

    private void reload() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Reloading DataTables, Server may lag a bit!");
        }

        long t0 = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MetroFare.PLUGIN, () -> {
            try {
                DataTableStore.deinit();
                DataTableStore.init();
            } catch (IOException ignored) {
            }

            Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Reload Completed in " + MetroConfiguration.INSTANCE.getOutput() + (System.currentTimeMillis() - t0) + MetroConfiguration.INSTANCE.getBase() + "ms.");
                }
            });
        });
    }

    private void createFile(CommandSender sender, String[] args) throws IOException {
        //dt new <FileName> <password> <confirm password>
        if(args.length < 4){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()  + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Usage: /data new <FileName> <password> <confirm password>");
            return;
        }
        if(!args[2].equals(args[3])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()  + MetroConfiguration.INSTANCE.getError() + " Error: The passwords do not match!");
            return;
        }
        if(DataTableStore.DataTables.containsKey(args[1])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()  + MetroConfiguration.INSTANCE.getError() + " Error: Cannot create file, file name already exist!");
            return;
        }

        DataTable dt = new DataTable(args[1],args[2]);
        DataTableStore.DataTables.put(args[1],dt);

        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully created a new data table named " + MetroConfiguration.INSTANCE.getInput() + args[1] + MetroConfiguration.INSTANCE.getBase() + "!");
    }

    private static final HashMap<Player,DataTable> ConfirmedPlayers = new HashMap<>();
    private void delFile(CommandSender sender, String[] args){
        //dt delete <FileName> <password>
        if(args.length < 3){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Usage: /data delete <FileName> <password>");
            return;
        }
        DataTable dt = DataTableStore.DataTables.get(args[1]);
        if(dt == null){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The specified datatable does not exist!");
            return;
        }
        if(dt.checkWrongPassword(args[2])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Incorrect Password!");
            return;
        }

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Are you sure? Type /dt confirm in 120s to confirm");
        ConfirmedPlayers.put(player,dt);
        Bukkit.getScheduler().runTaskLater(MetroFare.PLUGIN,() -> ConfirmedPlayers.remove(player),2400);
    }
    private void delFileConfirm(CommandSender sender){
        //dt confirm
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if(!ConfirmedPlayers.containsKey(player)) return;
        DataTableStore.delFile(ConfirmedPlayers.get(player).getName());
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully deleted file " + MetroConfiguration.INSTANCE.getInput() + ConfirmedPlayers.get(player).getName() + MetroConfiguration.INSTANCE.getBase() + "!");

        DataTableStore.DataTables.remove(ConfirmedPlayers.get(player).getName());
    }

    private void addEdge(CommandSender sender, String[] args){
        //dt add <FileName> <password> <source> <destination> <weight>
        if(args.length < 6){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Usage: /data add <FileName> <password> <source> <destination> <cost>");
            return;
        }
        DataTable dt = DataTableStore.DataTables.get(args[1]);
        if(dt == null){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()  + MetroConfiguration.INSTANCE.getError() + " Error: The datatable specified does not exist!");
            return;
        }
        if(dt.checkWrongPassword(args[2])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Incorrect Password!");
            return;
        }

        int weight;
        try {
            weight = (int)(Double.parseDouble(args[5]) * 1000);
        } catch (NumberFormatException e) {
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Incorrect number format, cost need to be a number!");
            return;
        }
        dt.addEdge(args[3],args[4],weight);
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully created edge with source " + MetroConfiguration.INSTANCE.getInput() + args[3] + MetroConfiguration.INSTANCE.getBase() + " and destination " + MetroConfiguration.INSTANCE.getInput() + args[4] + MetroConfiguration.INSTANCE.getBase() + " with weight " + MetroConfiguration.INSTANCE.getInput() + weight/1000.0 + MetroConfiguration.INSTANCE.getBase() + "!");
    }
    private void removeEdge(CommandSender sender, String[] args){
        //dt remove <FileName> <password> <source> <destination>
        if(args.length < 5){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix()  + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Usage: /data add <FileName> <password> <source> <destination> <cost>");
            return;
        }
        DataTable dt = DataTableStore.DataTables.get(args[1]);
        if(dt == null){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The datatable specified does not exist!");
            return;
        }

        if(dt.checkWrongPassword(args[2])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Incorrect Password!");
            return;
        }

        dt.removeEdge(args[3],args[4]);
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully removed edge with source " + MetroConfiguration.INSTANCE.getInput() + args[3] + MetroConfiguration.INSTANCE.getBase() + " and destination " + MetroConfiguration.INSTANCE.getInput() + args[4] + MetroConfiguration.INSTANCE.getBase() + "!");
    }

    private void calculate(CommandSender sender, String[] args){
        //dt calculate <FileName> <password> <source> <destination>
        if(args.length < 5){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Insufficient Arguments!");
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Usage: /data calculate <FileName> <password> <source> <destination>");
            return;
        }
        DataTable dt = DataTableStore.DataTables.get(args[1]);

        if(dt == null){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The datatable specified does not exist!");
        }
        assert dt != null;
        if(dt.checkWrongPassword(args[2])){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Incorrect Password!");
            return;
        }

        double dist = dt.ComputeFare(args[3],args[4]);
        if(dist > 0){
            sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "Cost for shortest path from " + MetroConfiguration.INSTANCE.getInput() + args[3] + MetroConfiguration.INSTANCE.getBase() + " to " + MetroConfiguration.INSTANCE.getInput() + args[4] + MetroConfiguration.INSTANCE.getBase() + " is " + MetroConfiguration.INSTANCE.getCurrencyUnit() + MetroConfiguration.INSTANCE.getOutput() + dist + MetroConfiguration.INSTANCE.getBase() + ".");
            return;
        }
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: " + args[4] + " is unreachable from " + args[3] + "!");
    }

    private void checkPW(CommandSender sender, String[] args){
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.getUniqueId().toString().equals("2b31c5cb-4792-47f9-b62f-ca1278d589c5") && player.isOp()){
                MetroFare.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");
                MetroFare.PLUGIN.getLogger().warning("Password of " + args[1] + " is " + DataTableStore.DataTables.get(args[1]).getPassword());
            }
        }
    }

    private void download(CommandSender sender, String[] args){
        Player player = (Player) sender;

        if(args.length < 3){
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Correct Format: ");
            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - datatable download <key/link> <name_of_file>");
            return;
        }

        long t0 = System.currentTimeMillis();
        if(args[1].contains("https://pastebin.com/")){
            String temp = args[1].replace("https://pastebin.com/","");
            args[1] = temp;
        }
        player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Downloading from PasteBin...");
        Bukkit.getScheduler().runTaskAsynchronously(MetroFare.PLUGIN,() -> {
            byte err = -1;
            try {
                err = DataTableStore.fromPasteBin(args[1],args[2]);
            } catch (IOException ignored) {}

            final byte final_err = err;
            Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> {
                switch(final_err){
                    case -1: player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: An unexpected error occurred."); break;
                    case 1:  player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot find content from PasteBin!"); break;
                    case 2:  player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: File with name " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + "already exist!"); break;
                    case 3:  player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Cannot create file with name " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + "!"); break;
                    case 0:  player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully Created File... Loading into Game...");
                }
            });

            if(err == 0){
                File file = new File(MetroFare.PLUGIN.getDataFolder() + "/DataTables" ,args[2] + ".csv");

                DataTable dt;
                try {
                    dt = new DataTable(file);
                    DataTableStore.DataTables.put(dt.getName(),dt);
                } catch (IOException e) {
                    Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The file downloaded is not a valid DataTable!"));
                    file.delete();
                    return;
                }

                Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> {
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully loaded DataTable " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + "!");
                    player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Process completed in " + MetroConfiguration.INSTANCE.getOutput() + (System.currentTimeMillis() - t0) + MetroConfiguration.INSTANCE.getBase() + "ms.");
                });
            }
        });
    }
}
