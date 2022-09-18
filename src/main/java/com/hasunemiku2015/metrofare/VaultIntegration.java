package com.hasunemiku2015.metrofare;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration {
    private static Economy eco;
    public static boolean vault;

    public static void init() {
        vault = setupEconomy();
    }

    private static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return true;
    }

    public static void deduct(Player player, double value) {
        eco.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), value);
    }

    public static void add(Player player, double value) {
        eco.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), value);
    }

    public static boolean hasEnough(Player player, double value) {
        double balance = eco.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
        return balance >= value;
    }
}
