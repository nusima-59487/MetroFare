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
import com.hasunemiku2015.metrofare.ticketing.sign.SingleUseTicketMachine;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public final class MetroFare extends JavaPlugin {
    public static MetroFare PLUGIN;

    @Override
    public void onEnable() {
        PLUGIN = this;
        initConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("Vault") || MetroConfiguration.INSTANCE.isVaultIntegrationEnabled()) {
            VaultIntegration.init();
        } else {
            VaultIntegration.vault = false;
        }

        DataTableStore.init();
        if (VaultIntegration.vault && MetroConfiguration.INSTANCE.isDceEnabled()) {
            //Note: MUST be after Config (cuz need to use ChatColor from Config)
            DebitCardEditor.init();
        }

        if (VaultIntegration.vault && MetroConfiguration.INSTANCE.isStmEnabled()) {
            SingleUseTicketMachine.init(); 
        }

        try {
            FareTableStore.init();
        } catch (IOException | InvalidFareTableException e) {
            e.printStackTrace();
        }

        //Note: MUST be after DataTable and FareTable cuz DataTable/FareTable Object is Transient
        CompanyStore.init();

        if (Bukkit.getPluginManager().isPluginEnabled("Train_Carts") && MetroConfiguration.INSTANCE.isValidatorEnabled()) {
            TCSignToggle.init();
        }

        //Events
        this.getServer().getPluginManager().registerEvents(new GateConstruct(), this);

        if (MetroConfiguration.INSTANCE.isEntryGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new GateExecutionIn(), this);
        }
        if (MetroConfiguration.INSTANCE.isExitGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new GateExecutionOut(), this);
        }
        if (MetroConfiguration.INSTANCE.isOtpEnabled()) {
            this.getServer().getPluginManager().registerEvents(new OTPExecution(), this);
        }
        if (MetroConfiguration.INSTANCE.isTransferGateEnabled()) {
            this.getServer().getPluginManager().registerEvents(new TransferGate(), this);
        }

        this.getServer().getPluginManager().registerEvents(new FenceGate(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("MikuCore") && MetroConfiguration.INSTANCE.isValidatorEnabled()) {
            this.getServer().getPluginManager().registerEvents(new Clearance(), this);
        }
        if (VaultIntegration.vault && MetroConfiguration.INSTANCE.isDceEnabled()) {
            this.getServer().getPluginManager().registerEvents(new DebitCardEditor(), this);
        }
        if (VaultIntegration.vault && MetroConfiguration.INSTANCE.isStmEnabled()) {
            this.getServer().getPluginManager().registerEvents(new SingleUseTicketMachine(), this);
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes and auto-updates the config.yml file. The following would be done: 
     * <ol>
     *     <li>Read the config.yml, check if it contains all keys of default config.yml file.</li>
     *     <li>If (1) is true, do nothing.</li>
     *     <li>Else, copy all values of old config into new and overwrite the old config.</li>
     * </ol>
     * New keys would be set as the default value.
     */
    private void initConfig() {
        PLUGIN.saveDefaultConfig();
        YamlConfiguration oldConfig = (YamlConfiguration) PLUGIN.getConfig();
        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(PLUGIN.getResource("config.yml"))));

        if (newConfig.getKeys(true).containsAll(oldConfig.getKeys(true))) {
            return;
        }

        PLUGIN.saveResource("config.yml", true);
        for (String key: oldConfig.getKeys(false)) {
            PLUGIN.getConfig().set(key, oldConfig.get(key));
        }
        PLUGIN.saveConfig();
        PLUGIN.reloadConfig();
    }
}
