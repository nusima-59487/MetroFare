package com.hasunemiku2015.metrofare.Gate.People;

import com.hasunemiku2015.metrofare.Company.CompanyStore;
import com.hasunemiku2015.metrofare.Gate.GateType;
import com.hasunemiku2015.metrofare.MFConfig;
import org.bukkit.Material;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Objects;


public class GateConstruct implements Listener {
    @EventHandler
    public void onGateBuild(SignChangeEvent event) {
        //Execute Check
        if (event.getLine(0) == null) {
            return;
        }

        boolean entryGate = MFConfig.getPrefixIn().equalsIgnoreCase(event.getLine(0)) && MFConfig.isEntryGateEnabled();
        boolean exitGate = MFConfig.getPrefixOut().equalsIgnoreCase(event.getLine(0)) && MFConfig.isExitGateEnabled();
        boolean oneTimePaymentMachine = MFConfig.getPrefixOTP().equalsIgnoreCase(event.getLine(0)) && MFConfig.isOtpEnabled();
        if (!entryGate && !exitGate && !oneTimePaymentMachine) {
            return;
        }

        if (!(event.getBlock().getState().getBlockData() instanceof WallSign)) {
            event.getBlock().setType(Material.AIR);
            return;
        }
        if (!MFConfig.hasBuildGatePermission(event.getPlayer())) {
            event.getBlock().setType(Material.AIR);
            return;
        }

        //Syntax Check
        if (event.getLine(1) == null) {
            event.getBlock().setType(Material.AIR);
            return;
        }
        String[] data = GateUtil.parseData(Objects.requireNonNull(event.getLine(1)));
        if (data.length != 2) {
            event.getBlock().setType(Material.AIR);
            return;
        }
        if (!CompanyStore.CompanyTable.containsKey(data[0])) {
            event.getBlock().setType(Material.AIR);
            return;
        }

        if (MFConfig.getPrefixOTP().equalsIgnoreCase(event.getLine(0))) {
            try {
                Double.parseDouble(data[1]);
            } catch (Exception ex) {
                event.getBlock().setType(Material.AIR);
                return;
            }
        } else {
            if ((CompanyStore.CompanyTable.get(data[0])).getType() != GateType.UNIFORM && data[1] == null) {
                event.getBlock().setType(Material.AIR);
                return;
            }
        }

        //Prettify
        if (MFConfig.getPrefixIn().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MFConfig.getInfo1In());
            event.setLine(3, MFConfig.getInfo2In());
        }

        if (MFConfig.getPrefixOut().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MFConfig.getInfo1Out());
            event.setLine(3, MFConfig.getInfo2Out());
        }

        if (MFConfig.getPrefixOTP().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MFConfig.getInfo1OTP());
            event.setLine(3, MFConfig.getInfo2OTP());
        }
    }
}
