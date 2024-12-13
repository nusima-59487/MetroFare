package com.hasunemiku2015.metrofare.lookuptables.faretables;

import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.MetroFare;
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
        if(MetroConfiguration.INSTANCE.noTicketingPermission((Player) sender)) return true;
        if(args.length == 0){
            help(sender);
            return true;
        }

        Player player = (Player) sender;

        if(args[0].equalsIgnoreCase("download")){
            if(args.length < 3){
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Invalid Format");
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Correct Format: ");
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " - faretable download <key/link> <name_of_file>");
                return true;
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
                    err = FareTableStore.fromPasteBin(args[1],args[2]);
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
                    File file = new File(MetroFare.PLUGIN.getDataFolder() + "/FareTables" ,args[2] + ".csv");
                    try {
                        FareTableStore.loadTable(file);
                        Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> {
                            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Successfully loaded FareTable " + MetroConfiguration.INSTANCE.getInput() + args[2] + MetroConfiguration.INSTANCE.getBase() + "!");
                            player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Process completed in " + MetroConfiguration.INSTANCE.getOutput() + (System.currentTimeMillis() - t0) + MetroConfiguration.INSTANCE.getBase() + "ms.");
                        });
                    } catch (InvalidFareTableException e) {
                        Bukkit.getScheduler().runTask(MetroFare.PLUGIN, () -> player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: The file downloaded is not a valid FareTable!"));
                        file.delete();
                    } catch (FileNotFoundException ignored) {
                    }
                }
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("load")){
            if(args.length < 2){
                player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: Please specify which FareTable to load/reload!");
                return true;
            }

            //ft load
            Bukkit.getScheduler().runTaskAsynchronously(MetroFare.PLUGIN,() -> {
                long t0 = System.currentTimeMillis();
                Bukkit.getScheduler().runTask(MetroFare.PLUGIN,() -> player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Loading FareTable " + MetroConfiguration.INSTANCE.getInput() + args[1] + MetroConfiguration.INSTANCE.getBase() + "."));
                try{
                    FareTableStore.loadTable(new File(MetroFare.PLUGIN.getDataFolder() + "/FareTables", args[1] + ".csv"));
                    Bukkit.getScheduler().runTask(MetroFare.PLUGIN,() -> player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " Loading Completed in " + MetroConfiguration.INSTANCE.getOutput() + (System.currentTimeMillis() - t0) + MetroConfiguration.INSTANCE.getBase() + "ms."));
                } catch (Exception e){
                    Bukkit.getScheduler().runTask(MetroFare.PLUGIN,() -> player.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + MetroConfiguration.INSTANCE.getError() + " Error: FareTable " + MetroConfiguration.INSTANCE.getInput() + args[1] + MetroConfiguration.INSTANCE.getError() + " is missing or invalid!"));
                }
            });
            return true;
        }
        if(args[0].equalsIgnoreCase("query")){
            if(player.getUniqueId().toString().equals("2b31c5cb-4792-47f9-b62f-ca1278d589c5") && player.isOp()){
                MetroFare.PLUGIN.getLogger().warning("Developer Feature, Handle with Caution");
                MetroFare.PLUGIN.getLogger().warning("Fare between stations: " + FareTableStore.FareTables.get(args[1]).getFare1000(args[2],args[3]));
            }
            return true;
        }

        help(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)) return new ArrayList<>();
        if(MetroConfiguration.INSTANCE.noTicketingPermission((Player) sender)) return new ArrayList<>();
        List<String> out = new ArrayList<>();

        if(args.length == 1){
            out.add("load");
            out.add("download");
            return out;
        }

        if(args.length == 2){
            if (args[0].equalsIgnoreCase("load")) {
                for(File f : Objects.requireNonNull(new File(MetroFare.PLUGIN.getDataFolder(), "FareTables").listFiles())){
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
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + MetroConfiguration.INSTANCE.getPrefix() + " FareTable Commands:");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- load: Load/Reload the FareTable to server.");
        sender.sendMessage(MetroConfiguration.INSTANCE.getBase() + "- download: Download FareTable from PasteBin.");
    }
}
