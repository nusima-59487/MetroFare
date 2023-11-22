package com.hasunemiku2015.metrofare.ticketing.types;

import com.hasunemiku2015.metrofare.company.AbstractCompany;
import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import lombok.Data;
import lombok.Getter;
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

/**
 * Adapter class for interacting with an ItemStack as a Ticket. <br/><br/>
 *
 * Recommended Editing Procedure:
 * <ol>
 *     <li>Create a Ticket instance with a given ItemStack.</li>
 *     <li>Check if the ItemStack is a valid DebitCard instance by using {@link Ticket#isValid()}.</li>
 *     <li>Obtain data of ticket using get methods, or enter a gate by calling {@link Ticket#entryProcedure()}.</li>
 * </ol>
 *
 * @author hasunemiku2015
 */
public class Ticket {
    // ============================================================================================================== //
    //                                             Validity Check String                                              //
    // ============================================================================================================== //
    private static final String VALIDITY_KEY = "ZjXpSfxFQ97UQqwg";

    // ============================================================================================================== //
    //                                                  Ticket Data                                                   //
    // ============================================================================================================== //
    private final boolean valid;
    private final int entered;
    private final ItemStack stack;

    @Getter
    private final String companyFrom;
    @Getter
    private final String companyTo;
    @Getter
    private final String entryData;
    @Getter
    private final String exitData;
    private final int fare;

    // ============================================================================================================== //
    //                                                  NamespaceKey                                                  //
    // ============================================================================================================== //
    private static final NamespacedKey ENTERED_KEY = new NamespacedKey(MTFA.PLUGIN, "Entered");

    private static final NamespacedKey ENTRY_DATA_KEY = new NamespacedKey(MTFA.PLUGIN, "EntryData");
    private static final NamespacedKey EXIT_DATA_KEY = new NamespacedKey(MTFA.PLUGIN, "ExitData");

    private static final NamespacedKey FARE_KEY = new NamespacedKey(MTFA.PLUGIN, "Fare");
    private static final NamespacedKey VALID_KEY = new NamespacedKey(MTFA.PLUGIN, "Valid");

    // ============================================================================================================== //
    //                                                  API Methods                                                   //
    // ============================================================================================================== //
    public Ticket(ItemStack stack) {
        this.stack = stack;
        ItemMeta itm = stack.getItemMeta();
        assert itm != null;
        PersistentDataContainer pdc = itm.getPersistentDataContainer();

        String enter = pdc.has(ENTRY_DATA_KEY, PersistentDataType.STRING) ?
                pdc.get(ENTRY_DATA_KEY, PersistentDataType.STRING) : ",";
        assert enter != null;
        entryData = enter.split(",")[0];
        companyFrom = enter.split(",")[1];

        String exit = pdc.has(EXIT_DATA_KEY, PersistentDataType.STRING) ?
                pdc.get(EXIT_DATA_KEY, PersistentDataType.STRING) : ",";
        assert exit != null;
        exitData = exit.split(",")[0];
        companyTo = exit.split(",")[1];

        fare = pdc.has(FARE_KEY, PersistentDataType.INTEGER) ? pdc.get(FARE_KEY, PersistentDataType.INTEGER) : -1;
        entered = pdc.has(ENTERED_KEY, PersistentDataType.INTEGER) ? pdc.get(ENTERED_KEY, PersistentDataType.INTEGER) : 1;
        String check = pdc.has(VALID_KEY, PersistentDataType.STRING) ? pdc.get(VALID_KEY, PersistentDataType.STRING) : null;
        valid = check != null && check.equals(VALIDITY_KEY);
    }

    /**
     * Checks if a ItemStack is a valid Ticket object.
     * Please check for validity before interacting with other methods.
     * @return True if the item is a valid Ticket, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Check if a Ticket contain an entry record.
     * @return True if the ticket contains an entry record, false otherwise.
     */
    public boolean hasEntered() {
        return entered > 0;
    }

    /**
     * Returns the value of a Ticket in terms of MetroFare's smallest denomination (0.001 unit).
     * @return 1000 times the balance in the Ticket.
     */
    public int getFare() {
        return fare;
    }

    /**
     * Returns the balance of a Ticket in floating-point number. (Not recommended).
     * @deprecated Use {@link Ticket#getFare()} for accuracy.
     * @return Ticket fare in floating-point number.
     */
    @Deprecated
    public double getFareDecimal() {
        return fare / 1000.0;
    }

    // ============================================================================================================== //
    //                                               "Private" Methods                                                //
    // ============================================================================================================== //
    public boolean checkEntryCompany(AbstractCompany company) {
        return companyFrom.equals(company.getName());
    }

    public boolean checkExitCompany(AbstractCompany company) {
        return companyTo.equals(company.getName());
    }

    public void entryProcedure() {
        ItemMeta itm = stack.getItemMeta();
        assert itm != null;
        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(new NamespacedKey(MTFA.PLUGIN, "Entered"), PersistentDataType.INTEGER, 1);
        stack.setItemMeta(itm);
    }

    // ============================================================================================================== //
    //                                                  Issue Ticket                                                  //
    // ============================================================================================================== //

    /**
     * Creates an inter/intra-company ticket with specified entry and exit data, with customized fare.
     * @param entryCompany The company that this ticket will travel from.
     * @param exitCompany The company that this ticket will travel to.
     * @param entryData Entry data (according to the format of specified by <strong>source</strong> company type).
     * @param exitData  Exit  data (according to the format of specified by <strong>destination</strong> company type).
     * @param fare Price of the ticket.
     * @return ItemStack of the Ticket issued.
     */
    public static ItemStack newTicket(AbstractCompany entryCompany, AbstractCompany exitCompany,
                                      String entryData, String exitData, int fare) {
        ItemStack its = new ItemStack(Material.PAPER, 1);
        its.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemMeta itm = its.getItemMeta();
        assert itm != null;
        itm.setDisplayName(MFConfig.INSTANCE.getTicketName());

        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.INSTANCE.getTicketPrefixIn() + ": " + MFConfig.INSTANCE.getBase() + entryData + " (" + entryCompany.getName() + ")");
        lore.add(MFConfig.INSTANCE.getTicketPrefixOut() + ": " + MFConfig.INSTANCE.getBase() + exitData + " (" + exitCompany.getName() + ")");
        lore.add(MFConfig.INSTANCE.getTicketPrefixFare() + ": " + MFConfig.INSTANCE.getBase() + MFConfig.INSTANCE.getCurrencyUnit() + fare / 1000.0);
        itm.setLore(lore);

        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(ENTRY_DATA_KEY, PersistentDataType.STRING, entryData + "," + entryCompany.getName());
        pdc.set(EXIT_DATA_KEY, PersistentDataType.STRING, exitData + "," + exitCompany.getName());
        pdc.set(VALID_KEY, PersistentDataType.STRING, VALIDITY_KEY);
        pdc.set(ENTERED_KEY, PersistentDataType.INTEGER, 0);
        pdc.set(FARE_KEY, PersistentDataType.INTEGER, fare);

        pdc.set(new NamespacedKey(MTFA.PLUGIN, "UUID"), PersistentDataType.STRING, UUID.randomUUID().toString());
        its.setItemMeta(itm);
        return its;
    }

    /**
     * Creates an intra-company ticket with specified entry and exit data, with customized fare.
     * @param company Company that the ticket would be used.
     * @param entryData Entry data (according to the format of specified by company type).
     * @param exitData Exit Data  (according to the format of specified by company type).
     * @param fare Price of the ticket.
     * @return ItemStack of the Ticket issued.
     */
    public static ItemStack newTicket(AbstractCompany company, String entryData, String exitData, int fare) {
        return newTicket(company, company, entryData, exitData, fare);
    }

    /**
     * Creates an intra-company ticket with the specified entry and exit data.
     * @param company   Company that the ticket would be used.
     * @param entryData Entry data (according to the format of specified by company type).
     * @param exitData  Exit Data  (according to the format of specified by company type).
     * @return TicketIssueData: A data class containing the ItemStack of the issued ticket and the fare of the ticket.
     */
    public static TicketIssueData newTicket(AbstractCompany company, String entryData, String exitData) {
        int fare = company.computeFare(entryData, exitData);
        ItemStack ticket = newTicket(company, entryData, exitData, fare);
        return new TicketIssueData(ticket, fare);
    }

    /**
     * POJO for returning the ItemStack and Fare in {@link Ticket#newTicket(AbstractCompany, String, String)}
     * @author hasunemiku2015
     */
    @Data
    static class TicketIssueData {
        public final ItemStack ticket;
        public final int fare;
    }
}
