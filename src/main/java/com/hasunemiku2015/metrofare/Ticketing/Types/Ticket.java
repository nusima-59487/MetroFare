package com.hasunemiku2015.metrofare.Ticketing.Types;

import com.hasunemiku2015.metrofare.Company.AbstractCompany;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ticket {
    //Var
    private static final String VALIDITY_KEY = "ZjXpSfxFQ97UQqwg";

    String EntryData;
    String ExitData;
    boolean valid;
    int entered;
    ItemStack stack;

    String CompanyFrom;
    String CompanyTo;
    int fare1000;

    public Ticket(ItemStack stack) {
        this.stack = stack;
        ItemMeta itm = stack.getItemMeta();
        assert itm != null;
        PersistentDataContainer pdc = itm.getPersistentDataContainer();

        String enter = pdc.has(new NamespacedKey(MTFA.plugin, "EntryData"), PersistentDataType.STRING) ? pdc.get(new NamespacedKey(MTFA.plugin, "EntryData"), PersistentDataType.STRING) : ",";
        assert enter != null;
        EntryData = enter.split(",")[0];
        CompanyFrom = enter.split(",")[1];

        String exit = pdc.has(new NamespacedKey(MTFA.plugin, "ExitData"), PersistentDataType.STRING) ? pdc.get(new NamespacedKey(MTFA.plugin, "ExitData"), PersistentDataType.STRING) : ",";
        assert exit != null;
        ExitData = exit.split(",")[0];
        CompanyTo = exit.split(",")[1];

        fare1000 = pdc.has(new NamespacedKey(MTFA.plugin, "Fare"), PersistentDataType.INTEGER) ? pdc.get(new NamespacedKey(MTFA.plugin, "Fare"), PersistentDataType.INTEGER) : -1;

        entered = pdc.has(new NamespacedKey(MTFA.plugin, "Entered"), PersistentDataType.INTEGER) ? pdc.get(new NamespacedKey(MTFA.plugin, "Entered"), PersistentDataType.INTEGER) : 1;
        String check = pdc.has(new NamespacedKey(MTFA.plugin, "Valid"), PersistentDataType.STRING) ? pdc.get(new NamespacedKey(MTFA.plugin, "Valid"), PersistentDataType.STRING) : null;
        valid = check != null && check.equals(VALIDITY_KEY);
    }

    public String getEntryData() {
        return EntryData;
    }

    public String getExitData() {
        return ExitData;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean hasEntered() {
        return entered > 0;
    }

    public String getCompanyFrom() {
        return CompanyFrom;
    }

    public String getCompanyTo() {
        return CompanyTo;
    }

    public double getFare1000() {
        return fare1000;
    }

    public boolean checkEntryCompany(AbstractCompany company) {
        return CompanyFrom.equals(company.getName());
    }

    public boolean checkExitCompany(AbstractCompany company) {
        return CompanyTo.equals(company.getName());
    }

    public void entryProcedure() {
        ItemMeta itm = stack.getItemMeta();
        assert itm != null;
        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(new NamespacedKey(MTFA.plugin, "Entered"), PersistentDataType.INTEGER, 1);
        stack.setItemMeta(itm);
    }

    //New Ticket
    public static ItemStack newTicket(AbstractCompany entryCompany, AbstractCompany exitCompany, String EntryData, String ExitData, int Fare) {
        ItemStack its = new ItemStack(Material.PAPER, 1);
        its.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemMeta itm = its.getItemMeta();
        assert itm != null;
        itm.setDisplayName(MFConfig.INSTANCE.getTicketName());

        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.INSTANCE.getTicketPrefixIn() + ": " + MFConfig.INSTANCE.getBase() + EntryData + " (" + entryCompany.getName() + ")");
        lore.add(MFConfig.INSTANCE.getTicketPrefixOut() + ": " + MFConfig.INSTANCE.getBase() + ExitData + " (" + exitCompany.getName() + ")");
        lore.add(MFConfig.INSTANCE.getTicketPrefixFare() + ": " + MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getCurrencyUnit() + Fare / 1000.0);
        itm.setLore(lore);

        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(new NamespacedKey(MTFA.plugin, "EntryData"), PersistentDataType.STRING, EntryData + "," + entryCompany.getName());
        pdc.set(new NamespacedKey(MTFA.plugin, "ExitData"), PersistentDataType.STRING, ExitData + "," + exitCompany.getName());
        pdc.set(new NamespacedKey(MTFA.plugin, "Valid"), PersistentDataType.STRING, VALIDITY_KEY);
        pdc.set(new NamespacedKey(MTFA.plugin, "UUID"), PersistentDataType.STRING, UUID.randomUUID().toString());
        pdc.set(new NamespacedKey(MTFA.plugin, "Entered"), PersistentDataType.INTEGER, 0);
        pdc.set(new NamespacedKey(MTFA.plugin, "Fare"), PersistentDataType.INTEGER, Fare);

        its.setItemMeta(itm);
        return its;
    }

    public static ItemStack newTicket(AbstractCompany company, String EntryData, String ExitData, int Fare) {
        return newTicket(company, company, EntryData, ExitData, Fare);
    }
}
