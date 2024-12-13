package com.hasunemiku2015.metrofare.gate.people;

import com.hasunemiku2015.metrofare.MetroConfiguration;
import com.hasunemiku2015.metrofare.company.CompanyStore;
import com.hasunemiku2015.metrofare.company.CompanyType;
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

        boolean entryGate = MetroConfiguration.INSTANCE.getPrefixIn().equalsIgnoreCase(event.getLine(0)) && MetroConfiguration.INSTANCE.isEntryGateEnabled();
        boolean exitGate = MetroConfiguration.INSTANCE.getPrefixOut().equalsIgnoreCase(event.getLine(0)) && MetroConfiguration.INSTANCE.isExitGateEnabled();
        boolean oneTimePaymentMachine = MetroConfiguration.INSTANCE.getPrefixOTP().equalsIgnoreCase(event.getLine(0)) && MetroConfiguration.INSTANCE.isOtpEnabled();
        if (!entryGate && !exitGate && !oneTimePaymentMachine) {
            return;
        }

        if (!(event.getBlock().getState().getBlockData() instanceof WallSign)) {
            event.getBlock().setType(Material.AIR);
            return;
        }
        if (!MetroConfiguration.INSTANCE.hasBuildGatePermission(event.getPlayer())) {
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

        if (MetroConfiguration.INSTANCE.getPrefixOTP().equalsIgnoreCase(event.getLine(0))) {
            try {
                Double.parseDouble(data[1]);
            } catch (Exception ex) {
                event.getBlock().setType(Material.AIR);
                return;
            }
        } else {
            if ((CompanyStore.CompanyTable.get(data[0])).getType() != CompanyType.UNIFORM && data[1] == null) {
                event.getBlock().setType(Material.AIR);
                return;
            }
        }

        //Prettify
        if (MetroConfiguration.INSTANCE.getPrefixIn().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MetroConfiguration.INSTANCE.getInfo1In());
            event.setLine(3, MetroConfiguration.INSTANCE.getInfo2In());
        }

        if (MetroConfiguration.INSTANCE.getPrefixOut().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MetroConfiguration.INSTANCE.getInfo1Out());
            event.setLine(3, MetroConfiguration.INSTANCE.getInfo2Out());
        }

        if (MetroConfiguration.INSTANCE.getPrefixOTP().equalsIgnoreCase(event.getLine(0))) {
            event.setLine(2, MetroConfiguration.INSTANCE.getInfo1OTP());
            event.setLine(3, MetroConfiguration.INSTANCE.getInfo2OTP());
        }
    }
}
