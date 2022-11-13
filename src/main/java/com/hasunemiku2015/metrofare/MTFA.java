package com.hasunemiku2015.metrofare;

import com.hasunemiku2015.metrofare.company.CompanyCommand;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.gate.FenceGate;
import com.hasunemiku2015.metrofare.gate.people.*;
import com.hasunemiku2015.metrofare.gate.train.Clearance;
import com.hasunemiku2015.metrofare.gate.train.TCSignToggle;
import com.hasunemiku2015.metrofare.lookuptables.datatables.DTCommand;
import com.hasunemiku2015.metrofare.lookuptables.datatables.DataTableStore;
import com.hasunemiku2015.metrofare.lookuptables.faretables.FTCommand;
import com.hasunemiku2015.metrofare.lookuptables.faretables.FareTableStore;
import com.hasunemiku2015.metrofare.lookuptables.faretables.InvalidFareTableException;
import com.hasunemiku2015.metrofare.ticketing.commands.DebitCardCBlkCommand;
import com.hasunemiku2015.metrofare.ticketing.commands.DebitCardCommand;
import com.hasunemiku2015.metrofare.ticketing.commands.TicketCBlkCommand;
import com.hasunemiku2015.metrofare.ticketing.commands.TicketCommand;
import com.hasunemiku2015.metrofare.ticketing.sign.DebitCardEditor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public final class MTFA extends JavaPlugin {
    public static MTFA PLUGIN;

    @Override
    public void onEnable() {
        PLUGIN = this;
        PLUGIN.saveDefaultConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault") || MFConfig.INSTANCE.isVaultIntegrationEnabled()) {
            VaultIntegration.init();
        } else {
            VaultIntegration.vault = false;
        }

        DataTableStore.init();
        if (VaultIntegration.vault && MFConfig.INSTANCE.isDceEnabled()) {
            //Note: MUST be after Config (cuz need to use ChatColor from Config)
            DebitCardEditor.init();
        }

        try {
            FareTableStore.init();
        } catch (IOException | InvalidFareTableException e) {
            e.printStackTrace();
        }

        //Note: MUST be after DataTable and FareTable cuz DataTable/FareTable Object is Transient
        CompanyStore.init();

        if (Bukkit.getPluginManager().isPluginEnabled("Train_Carts") && MFConfig.INSTANCE.isValidatorEnabled()) {
            TCSignToggle.init();
        }

        //Events
        this.getServer().getPluginManager().registerEvents(new GateConstruct(), this);

        if (MFConfig.INSTANCE.isEntryGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new GateExecutionIn(), this);
        }
        if (MFConfig.INSTANCE.isExitGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new GateExecutionOut(), this);
        }
        if (MFConfig.INSTANCE.isOtpEnabled()) {
            this.getServer().getPluginManager().registerEvents(new OTPExecution(), this);
        }
        if (MFConfig.INSTANCE.isTransferGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new TransferGate(), this);
        }

        this.getServer().getPluginManager().registerEvents(new FenceGate(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("MikuCore") && MFConfig.INSTANCE.isValidatorEnabled()) {
            this.getServer().getPluginManager().registerEvents(new Clearance(), this);
        }
        if (VaultIntegration.vault && MFConfig.INSTANCE.isDceEnabled()) {
            this.getServer().getPluginManager().registerEvents(new DebitCardEditor(), this);
        }

        //Commands
        Objects.requireNonNull(this.getServer().getPluginCommand("datum")).setExecutor(new DTCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("company")).setExecutor(new CompanyCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("debitcard")).setExecutor(new DebitCardCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("valueaddmachine")).setExecutor(new DebitCardCBlkCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("ticket")).setExecutor(new TicketCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("vending")).setExecutor(new TicketCBlkCommand());
        Objects.requireNonNull(this.getServer().getPluginCommand("faretable")).setExecutor(new FTCommand());
    }

    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().isPluginEnabled("Train_Carts")) {
            TCSignToggle.deInit();
        }

        try {
            CompanyStore.deInit();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            DataTableStore.deinit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConfig() {
        PLUGIN.saveDefaultConfig();
        YamlConfiguration oldConfig = (YamlConfiguration) PLUGIN.getConfig();
        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(PLUGIN.getResource("config.yml"))));

        if (newConfig.getKeys(true).containsAll(oldConfig.getKeys(true))) {
            return;
        }

        PLUGIN.saveResource("config.yml", true);
        PLUGIN.reloadConfig();
        for (String key: oldConfig.getKeys(true)) {
            PLUGIN.getConfig().set(key, oldConfig.get(key));
        }
        PLUGIN.saveConfig();
    }
}
