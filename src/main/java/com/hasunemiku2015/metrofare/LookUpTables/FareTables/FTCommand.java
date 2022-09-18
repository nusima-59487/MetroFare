package com.hasunemiku2015.metrofare.LookUpTables.FareTables;

import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FTCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        if(MFConfig.noTicketingPermission((Player) sender)) return true;
        if(args.length == 0){
            help(sender);
            return true;
        }

        Player player = (Player) sender;

        if(args[0].equalsIgnoreCase("download")){
            if(args.length < 3){
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Invalid Format");
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Correct Format: ");
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " - faretable download <key/link> <name_of_file>");
                return true;
            }

            long t0 = System.currentTimeMillis();
            if(args[1].contains("https://pastebin.com/")){
                String temp = args[1].replace("https://pastebin.com/","");
                args[1] = temp;
            }
            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Downloading from PasteBin...");
            Bukkit.getScheduler().runTaskAsynchronously(MTFA.plugin,() -> {
                byte err = -1;
                try {
                    err = FareTableStore.fromPasteBin(args[1],args[2]);
                } catch (IOException ignored) {}

                final byte final_err = err;
                Bukkit.getScheduler().runTask(MTFA.plugin, () -> {
                    switch(final_err){
                        case -1: player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: An unexpected error occurred."); break;
                        case 1:  player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot find content from PasteBin!"); break;
                        case 2:  player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: File with name " + MFConfig.getInput() + args[2] + MFConfig.getBase() + "already exist!"); break;
                        case 3:  player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Cannot create file with name " + MFConfig.getInput() + args[2] + MFConfig.getBase() + "!"); break;
                        case 0:  player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully Created File... Loading into Game...");
                    }
                });

                if(err == 0){
                    File file = new File(MTFA.plugin.getDataFolder() + "/FareTables" ,args[2] + ".csv");
                    try {
                        FareTableStore.loadTable(file);
                        Bukkit.getScheduler().runTask(MTFA.plugin, () -> {
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Successfully loaded FareTable " + MFConfig.getInput() + args[2] + MFConfig.getBase() + "!");
                            player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Process completed in " + MFConfig.getOutput() + (System.currentTimeMillis() - t0) + MFConfig.getBase() + "ms.");
                        });
                    } catch (InvalidFareTableException e) {
                        Bukkit.getScheduler().runTask(MTFA.plugin, () -> player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: The file downloaded is not a valid FareTable!"));
                        file.delete();
                    } catch (FileNotFoundException ignored) {
                    }
                }
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("load")){
            if(args.length < 2){
                player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: Please specify which FareTable to load/reload!");
                return true;
            }

            //ft load
            Bukkit.getScheduler().runTaskAsynchronously(MTFA.plugin,() -> {
                long t0 = System.currentTimeMillis();
                Bukkit.getScheduler().runTask(MTFA.plugin,() -> player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Loading FareTable " + MFConfig.getInput() + args[1] + MFConfig.getBase() + "."));
                try{
                    FareTableStore.loadTable(new File(MTFA.plugin.getDataFolder() + "/FareTables", args[1] + ".csv"));
                    Bukkit.getScheduler().runTask(MTFA.plugin,() -> player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " Loading Completed in " + MFConfig.getOutput() + (System.currentTimeMillis() - t0) + MFConfig.getBase() + "ms."));
                } catch (Exception e){
                    Bukkit.getScheduler().runTask(MTFA.plugin,() -> player.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + MFConfig.getError() + " Error: FareTable " + MFConfig.getInput() + args[1] + MFConfig.getError() + " is missing or invalid!"));
                }
            });
            return true;
        }
        if(args[0].equalsIgnoreCase("query")){
            if(player.getUniqueId().toString().equals("2b31c5cb-4792-47f9-b62f-ca1278d589c5") && player.isOp()){
                MTFA.plugin.getLogger().warning("Developer Feature, Handle with Caution");
                MTFA.plugin.getLogger().warning("Fare between stations: " + FareTableStore.FareTables.get(args[1]).getFare1000(args[2],args[3]));
            }
            return true;
        }

        help(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)) return new ArrayList<>();
        if(MFConfig.noTicketingPermission((Player) sender)) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        if(args.length == 1){
            out.add("load");
            out.add("download");
            return out;
        }

        if(args.length == 2){
            if (args[0].equalsIgnoreCase("load")) {
                for(File f : Objects.requireNonNull(new File(MTFA.plugin.getDataFolder(), "FareTables").listFiles())){
                    if(f.getName().endsWith(".csv")){
                        out.add(f.getName().replace(".csv",""));
                    }
                }
                return out;
            }
        }

        return new ArrayList<>();
    }

    private void help(CommandSender sender){
        sender.sendMessage(MFConfig.getBase() + MFConfig.getPrefix() + " FareTable Commands:");
        sender.sendMessage(MFConfig.getBase() + "- load: Load/Reload the FareTable to server.");
        sender.sendMessage(MFConfig.getBase() + "- download: Download FareTable from PasteBin.");
    }
}
