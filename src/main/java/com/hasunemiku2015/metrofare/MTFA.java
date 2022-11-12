package com.hasunemiku2015.metrofare;

import com.hasunemiku2015.metrofare.Company.CompanyCommand;
import com.hasunemiku2015.metrofare.Company.CompanyStore;
import com.hasunemiku2015.metrofare.Gate.People.*;
import com.hasunemiku2015.metrofare.Gate.Train.Clearance;
import com.hasunemiku2015.metrofare.Gate.Train.TCSignToggle;
import com.hasunemiku2015.metrofare.LookUpTables.DataTables.DTCommand;
import com.hasunemiku2015.metrofare.LookUpTables.DataTables.DataTableStore;
import com.hasunemiku2015.metrofare.Gate.FenceGate;
import com.hasunemiku2015.metrofare.LookUpTables.FareTables.FTCommand;
import com.hasunemiku2015.metrofare.LookUpTables.FareTables.FareTableStore;
import com.hasunemiku2015.metrofare.LookUpTables.FareTables.InvalidFareTableException;
import com.hasunemiku2015.metrofare.Ticketing.Commands.DebitCardCBlkCommand;
import com.hasunemiku2015.metrofare.Ticketing.Commands.DebitCardCommand;
import com.hasunemiku2015.metrofare.Ticketing.Commands.TicketCBlkCommand;
import com.hasunemiku2015.metrofare.Ticketing.Commands.TicketCommand;
import com.hasunemiku2015.metrofare.Ticketing.Sign.DebitCardEditor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
}
