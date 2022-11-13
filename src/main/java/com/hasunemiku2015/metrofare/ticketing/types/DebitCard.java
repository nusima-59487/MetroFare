package com.hasunemiku2015.metrofare.ticketing.types;

import com.hasunemiku2015.metrofare.MFConfig;
import com.hasunemiku2015.metrofare.MTFA;
import com.hasunemiku2015.metrofare.VaultIntegration;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


/**
 * Adapter class for interacting with an ItemStack as a DebitCard. <br/><br/>
 *
 * Recommended Editing Procedure:
 * <ol>
 *     <li>Create a DebitCard instance with a given ItemStack.</li>
 *     <li>Check if the ItemStack is a valid DebitCard instance by using {@link DebitCard#isValid()}.</li>
 *     <li>Modify the DebitCard instance by using the get/set methods.</li>
 *     <li><strong>Commit changes</strong> by calling <strong>{@link DebitCard#updateCard()}</strong>.</li>
 * </ol>
 *
 * @author hasunemiku2015
 */
public class DebitCard {
    // ============================================================================================================== //
    //                                             Validity Check String                                              //
    // ============================================================================================================== //
    private static final String validity = "0NuXDgTWuZgqMdTm";

    // ============================================================================================================== //
    //                                                   Card Data                                                    //
    // ============================================================================================================== //
    private final ItemStack stack;
    private final ItemMeta meta;
    private PersistentDataContainer cardDataCache;

    private final boolean valid;
    private String entryData;
    private String company;
    private String owner;
    private int balance;

    private long lastAddedAuto;
    private int addAmount;
    private int addedAmount;
    private int dailyLimit;

    private PaymentRecord record;

    // ============================================================================================================== //
    //                                                  NamespaceKey                                                  //
    // ============================================================================================================== //
    private static final NamespacedKey UUID_KEY = new NamespacedKey(MTFA.PLUGIN, "UUID");
    private static final NamespacedKey COMPANY_KEY = new NamespacedKey(MTFA.PLUGIN, "Company");
    private static final NamespacedKey ENTRY_DATA_KEY = new NamespacedKey(MTFA.PLUGIN, "EntryData");
    private static final NamespacedKey OWNER_KEY = new NamespacedKey(MTFA.PLUGIN, "Owner");
    private static final NamespacedKey BALANCE_KEY = new NamespacedKey(MTFA.PLUGIN, "Balance");
    private static final NamespacedKey VALIDITY_KEY = new NamespacedKey(MTFA.PLUGIN, "Valid");

    private static final NamespacedKey LAST_ADDED_AUTO_KEY = new NamespacedKey(MTFA.PLUGIN, "LastAddedAuto");
    private static final NamespacedKey ADD_AMOUNT_KEY = new NamespacedKey(MTFA.PLUGIN, "AddAmount");
    private static final NamespacedKey ADDED_AMOUNT_KEY = new NamespacedKey(MTFA.PLUGIN, "AddedAmount");
    private static final NamespacedKey DAILY_LIMIT_KEY = new NamespacedKey(MTFA.PLUGIN, "DailyLimit");

    private static final NamespacedKey PAYMENT_RECORD_KEY = new NamespacedKey(MTFA.PLUGIN, "PaymentRecord");

    // ============================================================================================================== //
    //                                                  API Methods                                                   //
    // ============================================================================================================== //

    /**
     * Creates a DebitCard Object from a given ItemStack.
     * The DebitCard is not valid if it does not contain all the NBT Data required.
     * @param stack ItemStack of the DebitCard Object.
     */
    public DebitCard(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
        if (meta == null) {
            valid = false;
        } else {
            cardDataCache = meta.getPersistentDataContainer();
            entryData = cardDataCache.has(ENTRY_DATA_KEY, PersistentDataType.STRING) ? cardDataCache.get(ENTRY_DATA_KEY, PersistentDataType.STRING) : null;
            company = cardDataCache.has(COMPANY_KEY, PersistentDataType.STRING) ? cardDataCache.get(COMPANY_KEY, PersistentDataType.STRING) : null;
            owner = cardDataCache.has(OWNER_KEY, PersistentDataType.STRING) ? cardDataCache.get(OWNER_KEY, PersistentDataType.STRING) : null;
            balance = cardDataCache.has(BALANCE_KEY, PersistentDataType.INTEGER) ? cardDataCache.get(BALANCE_KEY, PersistentDataType.INTEGER) : 0;

            lastAddedAuto = cardDataCache.has(LAST_ADDED_AUTO_KEY, PersistentDataType.LONG) ? cardDataCache.get(LAST_ADDED_AUTO_KEY, PersistentDataType.LONG) : 0;
            addAmount = cardDataCache.has(ADD_AMOUNT_KEY, PersistentDataType.INTEGER) ? cardDataCache.get(ADD_AMOUNT_KEY, PersistentDataType.INTEGER) : 0;
            addedAmount = cardDataCache.has(ADDED_AMOUNT_KEY, PersistentDataType.INTEGER) ? cardDataCache.get(ADDED_AMOUNT_KEY, PersistentDataType.INTEGER) : 0;
            dailyLimit = cardDataCache.has(DAILY_LIMIT_KEY, PersistentDataType.INTEGER) ? cardDataCache.get(DAILY_LIMIT_KEY, PersistentDataType.INTEGER) : 0;

            record = cardDataCache.has(PAYMENT_RECORD_KEY, PersistentDataType.STRING) ? new PaymentRecord(cardDataCache.get(PAYMENT_RECORD_KEY, PersistentDataType.STRING)) : PaymentRecord.newInstance();

            String validityCheck = cardDataCache.has(VALIDITY_KEY, PersistentDataType.STRING) ? cardDataCache.get(VALIDITY_KEY, PersistentDataType.STRING) : "";
            valid = validity.equals(validityCheck);
        }
    }

    /**
     * Checks if a ItemStack is a valid DebitCard object.
     * Please check for validity before interacting with other methods.
     * @return True if the item is a valid DebitCard, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Check if a DebitCard contain an entry record.
     * @return True if the card contains an entry record, false otherwise.
     */
    public boolean hasEntered() {
        return entryData != null && company != null;
    }

    /**
     * Returns the entry record of a DebitCard. Use {@link String#split(String)} to get individual components. <br/>
     *
     * Entry Records for various (default) company types:
     * <ul>
     *     <li><strong>Absolute Coordinate:</strong> COMPANY_NAME,STATION_X,STATION_Z</li>
     *     <li><strong>Dijkstra, FareTable:</strong> COMPANY_NAME,STATION_ID</li>
     *     <li><strong>Uniform:</strong> COMPANY_NAME</li>
     *     <li><strong>Zone:</strong> COMPANY_NAME,ZONE_ID</li>
     * </ul>
     * Custom company types may have different entry data format.
     *
     * @return The entry record of the specified DebitCard. Null if it does not have an entry record.
     */
    public String getEntryData() {
        return entryData;
    }

    /**
     * Same as {@link DebitCard#getEntryData()} but returns an empty string ("")
     * if the card does not have an entry record.
     * @return The entry record of the specified DebitCard. "" if it does not have an entry record.
     * @see DebitCard#getEntryData()
     */
    public String getEntryDataOrEmpty() {
        return entryData == null ? "" : entryData;
    }

    /**
     * Returns the entry record of a DebitCard as a {@link String String[]}. <br/>
     * Returns an empty array if card has no entry record.
     * @return String[] representing the entry record.
     * @see DebitCard getEntryData()
     */
    public String[] getEntryDataArray() {
        return getEntryDataOrEmpty().split(",");
    }

    /**
     * Sets the entry record of a DebitCard. Make sure to confront to the data format of a specific company type.
     * Entry Records for various (default) company types:
     * <ul>
     *     <li><strong>Absolute Coordinate:</strong> COMPANY_NAME,STATION_X,STATION_Z</li>
     *     <li><strong>Dijkstra, FareTable:</strong> COMPANY_NAME,STATION_ID</li>
     *     <li><strong>Uniform:</strong> COMPANY_NAME</li>
     *     <li><strong>Zone:</strong> COMPANY_NAME,ZONE_ID</li>
     * </ul>
     * Custom company types may have different entry data format.
     * @param entryData Entry record (separated in ',') to set to the DebitCard.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void setEntryData(String entryData) {
        if (entryData == null) {
            entryData = "";
        }
        this.entryData = entryData;
        cardDataCache.set(ENTRY_DATA_KEY, PersistentDataType.STRING, entryData);
    }

    /**
     * Removes the entry record of a DebitCard.
     * @see DebitCard
     */
    public void removeEntryData() {
        entryData = null;
        cardDataCache.remove(ENTRY_DATA_KEY);
    }

    /**
     * Returns the <strong>name</strong> of the entry company of a DebitCard.
     * @return Name of the entry company
     */
    public String getCompany() {
        return company;
    }

    /**
     * Sets the <strong>name</strong> of the entry company of a DebitCard.
     * @param company The <strong>name</strong> of the entry company.
     *                Use {@link com.hasunemiku2015.metrofare.company.AbstractCompany#getName()} to get the name.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void setCompany(String company) {
        this.company = company;
        cardDataCache.set(COMPANY_KEY, PersistentDataType.STRING, company);
    }

    /**
     * Removes the entry company of a DebitCard.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void removeCompany() {
        company = null;
        cardDataCache.remove(COMPANY_KEY);
    }

    /**
     * Gets the owner of the DebitCard. In UUID String.
     * @return Owner of the DebitCard.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets the owner of the DebitCard. In UUID.
     * @return Owner of the DebitCard.
     * @see DebitCard#getOwner()
     */
    public UUID getOwnerUUID() {
        return UUID.fromString(owner);
    }

    /**
     * Commits all changes from the DebitCard object to the ItemStack.
     * <strong>MUST</strong> be called after any changes done to the DebitCard.
     * @see DebitCard
     */
    public void updateCard() {
        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.INSTANCE.getOwnerPrefix() + MFConfig.INSTANCE.getInput() + Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(owner))).getName());

        if (balance <= 0) {
            autoTopUp();
        }

        if (balance > 0) {
            lore.add(MFConfig.INSTANCE.getBalancePrefix() + ChatColor.GREEN + MFConfig.INSTANCE.getCurrencyUnit()  + balance / 1000.0);
        } else if (balance < 0) {
            lore.add(MFConfig.INSTANCE.getBalancePrefix() + ChatColor.RED + MFConfig.INSTANCE.getCurrencyUnit() + balance / 1000.0);
        } else {
            lore.add(MFConfig.INSTANCE.getBalancePrefix() + ChatColor.DARK_GRAY + MFConfig.INSTANCE.getCurrencyUnit() + "0");
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);
    }

    /**
     * Returns the balance of a DebitCard in terms of MetroFare's smallest denomination (0.001 unit).
     * @return 1000 times the balance in the DebitCard.
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Returns the balance of a DebitCard in floating-point number. (Not recommended).
     * @deprecated Use {@link DebitCard#getBalance()} for accuracy.
     * @return DebitCard balance in floating-point number.
     */
    @Deprecated
    public double getBalanceDecimal() {
        return balance / 1000.0;
    }

    /**
     * Sets the balance of a DebitCard in MetroFare's smallest denomination (0.001 unit).
     * @param balance 1000 times the new balance of DebitCard.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void setBalance(int balance) {
        this.balance = balance;
        cardDataCache.set(BALANCE_KEY, PersistentDataType.INTEGER, balance);
    }

    /**
     * @param balance New balance of DebitCard, round down to nearest 0.001.
     *                Cannot be larger than {@link Integer#MAX_VALUE} / 1000
     * @return True if the balance is successfully updated, false otherwise.
     * @deprecated Use {@link DebitCard#setBalance(int)} if possible.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     * @see DebitCard#setBalance(int)
     */
    @Deprecated
    public boolean setBalanceDecimal(double balance) {
        if (balance > Integer.MAX_VALUE / 1000.0) {
            return false;
        }

        int balanceData = (int) balance / 1000;
        this.balance = balanceData;
        cardDataCache.set(BALANCE_KEY, PersistentDataType.INTEGER, balanceData);
        return true;
    }

    /**
     * Returns the amount added every time auto top-up is activated, in MetroFare's smallest denomination (0.001 unit).
     * @return 1000 times the auto top-up amount.
     */
    public int getAddAmount() {
        return addAmount;
    }

    /**
     * Returns the amount added every time auto top-up is activated of a
     * DebitCard in floating-point number (Not recommended).
     * @deprecated Use {@link DebitCard#getAddAmount()} for accuracy.
     * @return DebitCard balance in floating-point number.
     */
    @Deprecated
    public double getAddAmountDecimal() {
        return addAmount / 1000.0;
    }

    /**
     * Sets the amount of money (in MetroFare's smallest denomination (0.001 unit))
     * to add to the DebitCard everytime auto top-up is called.
     * @param addAmount 1000 times the amount to add.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void setAddAmount(int addAmount) {
        this.addedAmount = addAmount;
        cardDataCache.set(ADD_AMOUNT_KEY, PersistentDataType.INTEGER, addAmount);
    }

    /**
     * Sets the amount of money to add to the DebitCard everytime auto top-up is called, to the nearest 0.001 unit.
     * @deprecated Use {@link DebitCard#setAddAmount(int)} if possible.
     * @param addAmount Amount to add, less than {@link Integer#MAX_VALUE} / 1000.0 .
     * @return True if the addAmount is set successfully, false otherwise.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    @Deprecated
    public boolean setAddAmountDecimal(double addAmount) {
        if (addAmount > Integer.MAX_VALUE / 1000.0) {
            return false;
        }
        setAddAmount((int) (addAmount / 1000.0));
        return true;
    }

    /**
     * Reset the amount of money added to DebitCard upon auto top-up to 0.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void removeAddAmount() {
        addAmount = 0;
        cardDataCache.remove(ADD_AMOUNT_KEY);
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(int DailyLimit) {
        this.dailyLimit = DailyLimit;
        cardDataCache.set(DAILY_LIMIT_KEY, PersistentDataType.INTEGER, DailyLimit);
    }

    public void removeDailyLimit() {
        dailyLimit = 0;
        cardDataCache.remove(DAILY_LIMIT_KEY);
    }

    /**
     * Adds a new payment record to the DebitCard. Remove the oldest record if records are reached.
     * @param company The company that runs the transaction. Maybe a company name or an arbitrary string.
     * @param isDeduct True if money is deducted from the DebitCard, false otherwise.
     * @param amount Amount of money added/deducted from the DebitCard.
     * @see DebitCard
     * @see DebitCard#isValid()
     * @see DebitCard#updateCard()
     */
    public void addPaymentRecord(String company, boolean isDeduct, int amount) {
        if (isDeduct) {
            record.addPaymentRecord(company, StringUtils.rightPad("-" + MFConfig.INSTANCE.getCurrencyUnit() + amount / 1000.0, 10));
        } else {
            record.addPaymentRecord(company, StringUtils.rightPad("+" + MFConfig.INSTANCE.getCurrencyUnit() + amount / 1000.0, 10));
        }

        cardDataCache.set(PAYMENT_RECORD_KEY, PersistentDataType.STRING, record.toString());
    }

    /**
     * Gets the payment record of a DebitCard, in a {@link List} of {@link String String[]}.
     * From oldest to newest transaction.<br/><br/>
     * String[] format:
     * <ol>
     *     <li>Name of the Company</li>
     *     <li>Amount added/deducted with sign. Example:</li>
     *     <ul>
     *         <li>+1000.0</li>
     *         <li>-500.0</li>
     *     </ul>
     * </ol>
     * @return List of String[] representing the entry records.
     */
    public List<String[]> getPaymentRecords() {
        return record.getPaymentRecords();
    }

    // ============================================================================================================== //
    //                                               "Private" Methods                                                //
    // ============================================================================================================== //
    public void setAddedAmount(int AddedAmount) {
        this.addedAmount = AddedAmount;
        cardDataCache.set(ADDED_AMOUNT_KEY, PersistentDataType.INTEGER, AddedAmount);
    }

    public void removeAddedAmount() {
        addedAmount = 0;
        cardDataCache.remove(ADDED_AMOUNT_KEY);
    }

    public long getLastAddedAuto() {
        return lastAddedAuto;
    }

    public void setLastAddedAuto(long LastAddedAuto) {
        this.lastAddedAuto = LastAddedAuto;
        cardDataCache.set(LAST_ADDED_AUTO_KEY, PersistentDataType.LONG, LastAddedAuto);
    }

    public void removeLastAddedAuto() {
        lastAddedAuto = 0;
        cardDataCache.remove(LAST_ADDED_AUTO_KEY);
    }

    private void autoTopUp() {
        boolean b = true;
        if (addedAmount + addAmount > dailyLimit) {
            b = false;
            if (System.currentTimeMillis() - lastAddedAuto >= 86400000) {
                setAddedAmount(0);
                b = true;
            }
        }
        if (VaultIntegration.vault && b) {
            if (lastAddedAuto != 0) {
                if (VaultIntegration.hasEnough(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(getOwner()))), addAmount / 1000.0)) {
                    VaultIntegration.deduct(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(getOwner()))), addAmount / 1000.0);
                    setBalance(balance + addAmount);
                    setAddedAmount(addedAmount + addAmount);
                    setLastAddedAuto(System.currentTimeMillis());
                }
            }
        }
    }

    // ============================================================================================================== //
    //                                                    Issue Card                                                  //
    // ============================================================================================================== //
    /**
     * Creates a new DebitCard item for a specified player.
     * @param player The player to issue a new DebitCard.
     * @return ItemStack of the newly issued DebitCard.
     */
    public static ItemStack newCard(Player player) {
        ItemStack card = new ItemStack(Material.NAME_TAG, 1);
        card.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemMeta itm = card.getItemMeta();
        assert itm != null;
        itm.setDisplayName(MFConfig.INSTANCE.getDebitCardName());
        itm.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.INSTANCE.getOwnerPrefix() + MFConfig.INSTANCE.getInput() + player.getName());
        lore.add(MFConfig.INSTANCE.getBalancePrefix() + ChatColor.DARK_GRAY + MFConfig.INSTANCE.getCurrencyUnit() + 0);
        itm.setLore(lore);

        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(UUID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
        pdc.set(VALIDITY_KEY, PersistentDataType.STRING, validity);
        pdc.set(OWNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
        pdc.set(BALANCE_KEY, PersistentDataType.INTEGER, 0);

        card.setItemMeta(itm);
        return card;
    }
}
