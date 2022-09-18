package com.hasunemiku2015.metrofare.Ticketing.Types;

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

public class DebitCard {
    private static final String validity = "0NuXDgTWuZgqMdTm";

    //Card Itself
    private final ItemStack stack;
    private final ItemMeta meta;
    private PersistentDataContainer cardDataCache;

    //Card Data

    private final boolean Valid;
    private String EntryData;
    private String Company;
    private String Owner;
    private int Balance;

    private long LastAddedAuto;
    private int AddAmount;
    private int AddedAmount;
    private int DailyLimit;

    private PaymentRecord Record;

    //NameSpacedKeys
    private static final NamespacedKey UUIDKey = new NamespacedKey(MTFA.plugin, "UUID");
    private static final NamespacedKey CompanyKey = new NamespacedKey(MTFA.plugin, "Company");
    private static final NamespacedKey EntryDataKey = new NamespacedKey(MTFA.plugin, "EntryData");
    private static final NamespacedKey OwnerKey = new NamespacedKey(MTFA.plugin, "Owner");
    private static final NamespacedKey BalanceKey = new NamespacedKey(MTFA.plugin, "Balance");
    private static final NamespacedKey ValidityKey = new NamespacedKey(MTFA.plugin, "Valid");

    private static final NamespacedKey LastAddedAutoKey = new NamespacedKey(MTFA.plugin, "LastAddedAuto");
    private static final NamespacedKey AddAmountKey = new NamespacedKey(MTFA.plugin, "AddAmount");
    private static final NamespacedKey AddedAmountKey = new NamespacedKey(MTFA.plugin, "AddedAmount");
    private static final NamespacedKey DailyLimitKey = new NamespacedKey(MTFA.plugin, "DailyLimit");

    private static final NamespacedKey PaymentRecordKey = new NamespacedKey(MTFA.plugin, "PaymentRecord");

    //Constructor
    public DebitCard(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
        if (meta == null) {
            Valid = false;
        } else {
            cardDataCache = meta.getPersistentDataContainer();
            EntryData = cardDataCache.has(EntryDataKey, PersistentDataType.STRING) ? cardDataCache.get(EntryDataKey, PersistentDataType.STRING) : null;
            Company = cardDataCache.has(CompanyKey, PersistentDataType.STRING) ? cardDataCache.get(CompanyKey, PersistentDataType.STRING) : null;
            Owner = cardDataCache.has(OwnerKey, PersistentDataType.STRING) ? cardDataCache.get(OwnerKey, PersistentDataType.STRING) : null;
            Balance = cardDataCache.has(BalanceKey, PersistentDataType.INTEGER) ? cardDataCache.get(BalanceKey, PersistentDataType.INTEGER) : 0;

            LastAddedAuto = cardDataCache.has(LastAddedAutoKey, PersistentDataType.LONG) ? cardDataCache.get(LastAddedAutoKey, PersistentDataType.LONG) : 0;
            AddAmount = cardDataCache.has(AddAmountKey, PersistentDataType.INTEGER) ? cardDataCache.get(AddAmountKey, PersistentDataType.INTEGER) : 0;
            AddedAmount = cardDataCache.has(AddedAmountKey, PersistentDataType.INTEGER) ? cardDataCache.get(AddedAmountKey, PersistentDataType.INTEGER) : 0;
            DailyLimit = cardDataCache.has(DailyLimitKey, PersistentDataType.INTEGER) ? cardDataCache.get(DailyLimitKey, PersistentDataType.INTEGER) : 0;

            Record = cardDataCache.has(PaymentRecordKey, PersistentDataType.STRING) ? new PaymentRecord(cardDataCache.get(PaymentRecordKey, PersistentDataType.STRING)) : PaymentRecord.newInstance();

            String validityCheck = cardDataCache.has(ValidityKey, PersistentDataType.STRING) ? cardDataCache.get(ValidityKey, PersistentDataType.STRING) : "";
            Valid = validity.equals(validityCheck);
        }
    }


    //Getter and Setter
    public boolean isValid() {
        return Valid;
    }

    public boolean hasEntered() {
        return EntryData != null && Company != null;
    }

    public String getEntryData() {
        return EntryData;
    }

    public void setEntryData(String EntryData) {
        if (EntryData == null) {
            EntryData = "";
        }
        this.EntryData = EntryData;
        cardDataCache.set(EntryDataKey, PersistentDataType.STRING, EntryData);
    }

    public void removeEntryData() {
        EntryData = null;
        cardDataCache.remove(EntryDataKey);
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String Company) {
        this.Company = Company;
        cardDataCache.set(CompanyKey, PersistentDataType.STRING, Company);
    }

    public void removeCompany() {
        Company = null;
        cardDataCache.remove(CompanyKey);
    }

    public String getOwner() {
        return Owner;
    }

    public void updateCard() {
        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.getOwnerPrefix() + MFConfig.getInput() + Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Owner))).getName());

        if (Balance <= 0) {
            autoTopUp();
        }

        if (Balance > 0) {
            lore.add(MFConfig.getBalancePrefix() + ChatColor.GREEN + MFConfig.getCurrencyUnit()  + Balance / 1000.0);
        } else if (Balance < 0) {
            lore.add(MFConfig.getBalancePrefix() + ChatColor.RED + MFConfig.getCurrencyUnit() + Balance / 1000.0);
        } else {
            lore.add(MFConfig.getBalancePrefix() + ChatColor.DARK_GRAY + MFConfig.getCurrencyUnit() + "0");
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int Balance) {
        this.Balance = Balance;
        cardDataCache.set(BalanceKey, PersistentDataType.INTEGER, Balance);
    }

    public int getAddAmount() {
        return AddAmount;
    }

    public void setAddAmount(int AddAmount) {
        this.AddedAmount = AddAmount;
        cardDataCache.set(AddAmountKey, PersistentDataType.INTEGER, AddAmount);
    }

    public void removeAddAmount() {
        AddAmount = 0;
        cardDataCache.remove(AddAmountKey);
    }

    public void setAddedAmount(int AddedAmount) {
        this.AddedAmount = AddedAmount;
        cardDataCache.set(AddedAmountKey, PersistentDataType.INTEGER, AddedAmount);
    }

    public void removeAddedAmount() {
        AddedAmount = 0;
        cardDataCache.remove(AddedAmountKey);
    }

    public int getDailyLimit() {
        return DailyLimit;
    }

    public void setDailyLimit(int DailyLimit) {
        this.DailyLimit = DailyLimit;
        cardDataCache.set(DailyLimitKey, PersistentDataType.INTEGER, DailyLimit);
    }

    public void removeDailyLimit() {
        DailyLimit = 0;
        cardDataCache.remove(DailyLimitKey);
    }

    public long getLastAddedAuto() {
        return LastAddedAuto;
    }

    public void setLastAddedAuto(long LastAddedAuto) {
        this.LastAddedAuto = LastAddedAuto;
        cardDataCache.set(LastAddedAutoKey, PersistentDataType.LONG, LastAddedAuto);
    }

    public void removeLastAddedAuto() {
        LastAddedAuto = 0;
        cardDataCache.remove(LastAddedAutoKey);
    }

    public void addPaymentRecord(String Company, boolean isDeduct, int amount) {
        if (isDeduct) {
            Record.addPaymentRecord(Company, StringUtils.rightPad("-" + MFConfig.getCurrencyUnit() + amount / 1000.0, 10));
        } else {
            Record.addPaymentRecord(Company, StringUtils.rightPad("+" + MFConfig.getCurrencyUnit() + amount / 1000.0, 10));
        }

        cardDataCache.set(PaymentRecordKey, PersistentDataType.STRING, Record.toString());
    }

    public List<String[]> getPaymentRecords() {
        return Record.getPaymentRecords();
    }

    //New Card
    public static ItemStack newCard(Player p) {
        ItemStack card = new ItemStack(Material.NAME_TAG, 1);
        card.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemMeta itm = card.getItemMeta();
        assert itm != null;
        itm.setDisplayName(MFConfig.getDebitCardName());
        itm.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add(MFConfig.getOwnerPrefix() + MFConfig.getInput() + p.getName());
        lore.add(MFConfig.getBalancePrefix() + ChatColor.DARK_GRAY + MFConfig.getCurrencyUnit() + 0);
        itm.setLore(lore);

        PersistentDataContainer pdc = itm.getPersistentDataContainer();
        pdc.set(UUIDKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        pdc.set(ValidityKey, PersistentDataType.STRING, validity);
        pdc.set(OwnerKey, PersistentDataType.STRING, p.getUniqueId().toString());
        pdc.set(BalanceKey, PersistentDataType.INTEGER, 0);

        card.setItemMeta(itm);
        return card;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean autoTopUp() {
        boolean b = true;
        if (AddedAmount + AddAmount > DailyLimit) {
            b = false;
            if (System.currentTimeMillis() - LastAddedAuto >= 86400000) {
                setAddedAmount(0);
                b = true;
            }
        }
        if (VaultIntegration.vault && b) {
            if (LastAddedAuto != 0) {
                if (VaultIntegration.hasEnough(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(getOwner()))), AddAmount / 1000.0)) {
                    VaultIntegration.deduct(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(getOwner()))), AddAmount / 1000.0);
                    setBalance(Balance + AddAmount);
                    setAddedAmount(AddedAmount + AddAmount);
                    setLastAddedAuto(System.currentTimeMillis());
                    return true;
                }
            }
        }
        return false;
    }
}
