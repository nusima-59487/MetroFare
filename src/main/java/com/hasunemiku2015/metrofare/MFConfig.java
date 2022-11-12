package com.hasunemiku2015.metrofare;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MFConfig {
    public static final MFConfig INSTANCE = new MFConfig();

    // ============================================================================================================== //
    //                                                General                                                         //
    // ============================================================================================================== //
    private double defaultFare = ConfigHelper.getDouble("default_fare");
    private int openTime = ConfigHelper.getInt("open_time");
    private boolean vaultIntegrationEnabled = ConfigHelper.getBoolean("vault_integration");

    // ============================================================================================================== //
    //                                                Permissions                                                     //
    // ============================================================================================================== //
    private boolean gatePermissionEnabled = ConfigHelper.getBoolean("permission.gate");
    private boolean editorPermissionEnabled = ConfigHelper.getBoolean("permission.editor");
    private boolean dataBasePermissionEnabled = ConfigHelper.getBoolean("permission.database");
    private boolean ticketingPermissionEnabled = ConfigHelper.getBoolean("permission.ticketing");
    private boolean fenceGatePermissionEnabled = ConfigHelper.getBoolean("permission.fence");

    public boolean hasBuildGatePermission(Player player) {
        return gatePermissionEnabled || player.hasPermission("mtfa.buildgate");
    }
    public boolean hasBuildEditorPermission(Player player) {
        return editorPermissionEnabled || player.hasPermission("mtfa.buildeditor");
    }
    public boolean hasDataTablePermission(Player player){
        return dataBasePermissionEnabled || player.hasPermission("mtfa.database");
    }
    public boolean noTicketingPermission(Player player) {
        return !ticketingPermissionEnabled && !player.hasPermission("mtfa.ticketing");
    }
    public boolean hasFenceGatePermission(Player player) {
        return fenceGatePermissionEnabled || player.hasPermission("mtfa.fence");
    }

    // ============================================================================================================== //
    //                                                Theme                                                           //
    // ============================================================================================================== //
    private String prefix = ConfigHelper.getString("prefix");

    private ChatColor base = ChatColor.valueOf("theme.main");
    private ChatColor error = ChatColor.valueOf("theme.error");
    private ChatColor input = ChatColor.valueOf("theme.input_values");
    private ChatColor output = ChatColor.valueOf("theme.results");

    private String currencyUnit = INSTANCE.getCurrencyUnit(ConfigHelper.getInt("currency_unit"));
    private String getCurrencyUnit(int value) {
        String currencyUnit = "$";
        switch(value){
            case 0: currencyUnit = "$"; break;
            case 1: currencyUnit = "£"; break;
            case 2: currencyUnit = "€"; break;
            case 3: currencyUnit = "¥"; break;
            case 4: currencyUnit = "₩"; break;
            case 5: currencyUnit = "฿"; break;
            case 6: currencyUnit = "₫"; break;
            case 7: currencyUnit = "₽"; break;
            case 8: currencyUnit = "₣"; break;
            case 9: currencyUnit = "ரூ"; break;
            case 10: currencyUnit = "RM"; break;
            case 11: currencyUnit = "₱"; break;
        }
        return currencyUnit;
    }

    // ============================================================================================================== //
    //                                               Entry Gate                                                       //
    // ============================================================================================================== //
    private boolean entryGateEnabled = ConfigHelper.getBoolean("entry_gate.enable");
    private String prefixIn = ConfigHelper.getString("entry_gate.prefix");
    private String info1In = ConfigHelper.getColoredString("entry_gate.info_1");
    private String info2In = ConfigHelper.getColoredString("entry_gate.info_2");
    private String transient1In = ConfigHelper.getColoredString("entry_gate.transient_1");
    private String transient2In = ConfigHelper.getColoredString("entry_gate.transient_2");

    private String debitCardInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.card_invalid");
    private String companyInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.company_invalid");
    private String playerInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.player_invalid");
    private String cardEnteredIn = ConfigHelper.getColoredString("entry_gate.debit_card.card_entered");
    private String insufficientIn = ConfigHelper.getColoredString("entry_gate.debit_card.insufficient");
    
    private String ticketInvalidIn = ConfigHelper.getColoredString("entry_gate.ticket.ticket_invalid");
    private String ticketEnteredIn = ConfigHelper.getColoredString("entry_gate.ticket.ticket_entered");
    private String entryCompanyInvalid = ConfigHelper.getColoredString("entry_gate.ticket.entry_company_invalid");
    private String stationInvalid = ConfigHelper.getColoredString("entry_gate.ticket.station_invalid");

    private String chatIn = ConfigHelper.getColoredString("entry_gate.chat");
    
    // ============================================================================================================== //
    //                                                Exit Gate                                                       //
    // ============================================================================================================== //
    private boolean exitGateEnabled = ConfigHelper.getBoolean("exit_gate.enabled");
    private String prefixOut = ConfigHelper.getString("exit_gate.prefix");
    private String info1Out = ConfigHelper.getColoredString("exit_gate.info_1");
    private String info2Out = ConfigHelper.getColoredString("exit_gate.info_2");
    private String transient1Out = ConfigHelper.getColoredString("exit_gate.transient_1");
    private String transient2Out = ConfigHelper.getColoredString("exit_gate.transient_2");

    private String debitCardInvalidOut = ConfigHelper.getColoredString("entry_gate.debit_card.card_invalid");
    private String playerInvalidOut = ConfigHelper.getColoredString("entry_gate.debit_card.player_invalid");
    private String cardEnteredOut = ConfigHelper.getColoredString("entry_gate.debit_card.card_not_entered");

    private String ticketInvalidOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_invalid");
    private String ticketNotEnteredOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_not_entered");
    private String ticketWrongExitCompanyOut = ConfigHelper.getColoredString("exit_gate.ticket.wrong_exit_company");
    private String ticketInvalidInterCompanyOut = ConfigHelper.getColoredString("exit_gate.ticket.invalid_inter-company");
    private String ticketInsufficientFareOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_insufficient_fare");

    private String chatOut = ConfigHelper.getColoredString("exit_gate.chat");
    private String chatFateOut = ConfigHelper.getColoredString("exit_gate.chat_fare");
    private String chatRemaining = ConfigHelper.getColoredString("exit_gate.chat_remaining");

    // ============================================================================================================== //
    //                                            One-Time Payment Machine                                            //
    // ============================================================================================================== //
    private boolean otpEnabled = ConfigHelper.getBoolean("one_time_payment_machine.enable");
    private String prefixOTP = ConfigHelper.getString("one_time_payment_machine.prefix");
    private String info1OTP = ConfigHelper.getColoredString("one_time_payment_machine.info_1");
    private String info2OTP = ConfigHelper.getColoredString("one_time_payment_machine.info_1");
    private String transient1OTP = ConfigHelper.getColoredString("one_time_payment_machine.transient_1");
    private String transient2OTP = ConfigHelper.getColoredString("one_time_payment_machine.transient_2");

    private String chatFareOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat");
    private String chatRemainingOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat_remaining");
    private String insufficientOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat_insufficient");

    // ============================================================================================================== //
    //                                              DebitCard Editor                                                  //
    // ============================================================================================================== //
    private boolean dceEnabled = ConfigHelper.getBoolean("card_editor.enable");
    private String nameDCE = ConfigHelper.getString("card_editor.name");

    private String prefixDCE = ConfigHelper.getString("card_editor.prefix");
    private String info1DCE = ConfigHelper.getColoredString("card_editor.info_1");
    private String info2DCE = ConfigHelper.getColoredString("card_editor.info_2");
    private String info3DCE = ConfigHelper.getColoredString("card_editor.info_3");

    private String promptAddDCE = ConfigHelper.getColoredString("card_editor.prompt_add");
    private String promptRemoveDCE = ConfigHelper.getColoredString("card_editor.prompt_bankin");
    private String promptAutoAddAmountDCE = ConfigHelper.getColoredString("card_editor.prompt_auto_addamount");
    private String promptAutoDailyLimitDCE = ConfigHelper.getColoredString("card_editor.prompt_auto_dailylimit");
    private String successDCE = ConfigHelper.getColoredString("card_editor.success");
    private String failDCE = ConfigHelper.getColoredString("card_editor.fail");
    private String newBalanceDCE = ConfigHelper.getColoredString("card_editor.new_balance");

    // ============================================================================================================== //
    //                                              DebitCard Validator                                               //
    // ============================================================================================================== //
    private boolean validatorEnabled = ConfigHelper.getBoolean("validator.enable");

    private String validatorVanillaPrefix = ConfigHelper.getString("validator.vanilla.prefix");

    private String validatorTrainCartsPrefix = ConfigHelper.getString("validator.train_carts.prefix");
    private String validatorTrainCartsName = ConfigHelper.getString("validator.train_carts.name");
    private String validatorTrainCartsDescription = ConfigHelper.getString("validator.train_carts.description");

    private String validatorComplete = ConfigHelper.getColoredString("validator.complete");
    private String validatorFail = ConfigHelper.getColoredString("validator.no_card");

    // ============================================================================================================== //
    //                                                 Transfer Gate                                                  //
    // ============================================================================================================== //
    private boolean transferGateEnabled = ConfigHelper.getBoolean("transfer_gate.enable");
    private String prefixTransfer = ConfigHelper.getString("transfer_gate.prefix");
    private String info1Transfer = ConfigHelper.getColoredString("transfer_gate.info_1");
    private String info2Transfer = ConfigHelper.getColoredString("transfer_gate.info_2");
    private String info3Transfer = ConfigHelper.getColoredString("transfer_gate.info_3");
    private String info4Transfer = ConfigHelper.getColoredString("transfer_gate.info_4");
    private String transient1Transfer = ConfigHelper.getColoredString("transfer_gate.transient_1");
    private String transient2Transfer = ConfigHelper.getColoredString("transfer_gate.transient_2");

    private String chatTicketTransfer = ConfigHelper.getColoredString("transfer_gate.chat_ticket");
    private String chatTicketErrorTransfer = ConfigHelper.getColoredString("transfer_gate.chat_ticket_error");

    // ============================================================================================================== //
    //                                                   DebitCard                                                    //
    // ============================================================================================================== //
    private String debitCardName = ConfigHelper.getColoredString("debit_name");
    private String ownerPrefix = ConfigHelper.getColoredString("owner_prefix");
    private String balancePrefix = ConfigHelper.getColoredString("balance_prefix");

    // ============================================================================================================== //
    //                                                     Ticket                                                     //
    // ============================================================================================================== //
    private String ticketName = ConfigHelper.getColoredString("ticket.name");
    private String ticketPrefixIn = ConfigHelper.getColoredString("ticket.from");
    private String ticketPrefixOut = ConfigHelper.getColoredString("ticket.to");
    private String ticketPrefixFare = ConfigHelper.getColoredString("ticket.fare");

    // ============================================================================================================== //
    //                                                  Helper Class                                                  //
    // ============================================================================================================== //
    /**
     * Inner helper class for reading config files.
     * @see MFConfig
     */
    static class ConfigHelper {
        private static final FileConfiguration config = MTFA.plugin.getConfig();

        /**
         * Returns a boolean value from the config.yml.
         * @param configKey Specified key to read the value from.
         * @return Boolean value of the specified key, defaults to 0 if null.
         */
        protected static boolean getBoolean(String configKey) {
            return config.getBoolean(configKey);
        }

        /**
         * Returns an int value from the config.yml.
         * @param configKey Specified key to read the value from.
         * @return Boolean value of the specified key, defaults to 0 if null.
         */
        protected static int getInt(String configKey) {
            return config.getInt(configKey);
        }

        /**
         * Returns a double value from the config.yml.
         * @param configKey Specified key to read the value from.
         * @return Boolean value of the specified key, defaults to 0 if null.
         */
        protected static double getDouble(String configKey) {
            return config.getDouble(configKey);
        }

        /**
         * Returns a string from the config.yml
         * @param configKey Specified key to read the value from.
         * @return String value of the specified key, defaults to empty string if null.
         */
        @NonNull
        protected static String getString(String configKey) {
            String val = config.getString(configKey);
            return val == null ? "" : val;
        }

        /**
         * Returns a colored string (Color Character: '&') from the config.yml
         * @param configKey Specified key to read the value from.
         * @return String value of the specified key, defaults to empty string if null.
         */
        @NotNull
        protected static String getColoredString(String configKey) {
            return ChatColor.translateAlternateColorCodes('&', getString(configKey));
        }
    }
}
