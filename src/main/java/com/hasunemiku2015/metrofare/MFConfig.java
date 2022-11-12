package com.hasunemiku2015.metrofare;

import lombok.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MFConfig {
    public static final MFConfig INSTANCE = new MFConfig();

    // ============================================================================================================== //
    //                                                General                                                         //
    // ============================================================================================================== //
     double defaultFare = ConfigHelper.getDouble("default_fare");
     int openTime = ConfigHelper.getInt("open_time");
     boolean vaultIntegrationEnabled = ConfigHelper.getBoolean("vault_integration");

    // ============================================================================================================== //
    //                                                Permissions                                                     //
    // ============================================================================================================== //
     boolean gatePermissionEnabled = ConfigHelper.getBoolean("permission.gate");
     boolean editorPermissionEnabled = ConfigHelper.getBoolean("permission.editor");
     boolean dataBasePermissionEnabled = ConfigHelper.getBoolean("permission.database");
     boolean ticketingPermissionEnabled = ConfigHelper.getBoolean("permission.ticketing");
     boolean fenceGatePermissionEnabled = ConfigHelper.getBoolean("permission.fence");

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
     String prefix = ConfigHelper.getString("prefix");

     ChatColor base = ChatColor.valueOf("theme.main");
     ChatColor error = ChatColor.valueOf("theme.error");
     ChatColor input = ChatColor.valueOf("theme.input_values");
     ChatColor output = ChatColor.valueOf("theme.results");

     String currencyUnit = INSTANCE.getCurrencyUnit(ConfigHelper.getInt("currency_unit"));
     String getCurrencyUnit(int value) {
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
     boolean entryGateEnabled = ConfigHelper.getBoolean("entry_gate.enable");
     String prefixIn = ConfigHelper.getString("entry_gate.prefix");
     String info1In = ConfigHelper.getColoredString("entry_gate.info_1");
     String info2In = ConfigHelper.getColoredString("entry_gate.info_2");
     String transient1In = ConfigHelper.getColoredString("entry_gate.transient_1");
     String transient2In = ConfigHelper.getColoredString("entry_gate.transient_2");

     String debitCardInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.card_invalid");
     String companyInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.company_invalid");
     String playerInvalidIn = ConfigHelper.getColoredString("entry_gate.debit_card.player_invalid");
     String cardEnteredIn = ConfigHelper.getColoredString("entry_gate.debit_card.card_entered");
     String insufficientIn = ConfigHelper.getColoredString("entry_gate.debit_card.insufficient");
    
     String ticketInvalidIn = ConfigHelper.getColoredString("entry_gate.ticket.ticket_invalid");
     String ticketEnteredIn = ConfigHelper.getColoredString("entry_gate.ticket.ticket_entered");
     String entryCompanyInvalidIn = ConfigHelper.getColoredString("entry_gate.ticket.entry_company_invalid");
     String stationInvalidIn = ConfigHelper.getColoredString("entry_gate.ticket.station_invalid");

     String chatIn = ConfigHelper.getColoredString("entry_gate.chat");
    
    // ============================================================================================================== //
    //                                                Exit Gate                                                       //
    // ============================================================================================================== //
     boolean exitGateEnabled = ConfigHelper.getBoolean("exit_gate.enabled");
     String prefixOut = ConfigHelper.getString("exit_gate.prefix");
     String info1Out = ConfigHelper.getColoredString("exit_gate.info_1");
     String info2Out = ConfigHelper.getColoredString("exit_gate.info_2");
     String transient1Out = ConfigHelper.getColoredString("exit_gate.transient_1");
     String transient2Out = ConfigHelper.getColoredString("exit_gate.transient_2");

     String debitCardInvalidOut = ConfigHelper.getColoredString("entry_gate.debit_card.card_invalid");
     String playerInvalidOut = ConfigHelper.getColoredString("entry_gate.debit_card.player_invalid");
     String cardNotEnteredOut = ConfigHelper.getColoredString("entry_gate.debit_card.card_not_entered");

     String ticketInvalidOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_invalid");
     String ticketNotEnteredOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_not_entered");
     String ticketWrongExitCompanyOut = ConfigHelper.getColoredString("exit_gate.ticket.wrong_exit_company");
     String ticketInvalidInterCompanyOut = ConfigHelper.getColoredString("exit_gate.ticket.invalid_inter-company");
     String ticketInsufficientFareOut = ConfigHelper.getColoredString("exit_gate.ticket.ticket_insufficient_fare");

     String chatOut = ConfigHelper.getColoredString("exit_gate.chat");
     String chatFateOut = ConfigHelper.getColoredString("exit_gate.chat_fare");
     String chatRemaining = ConfigHelper.getColoredString("exit_gate.chat_remaining");

    // ============================================================================================================== //
    //                                            One-Time Payment Machine                                            //
    // ============================================================================================================== //
     boolean otpEnabled = ConfigHelper.getBoolean("one_time_payment_machine.enable");
     String prefixOTP = ConfigHelper.getString("one_time_payment_machine.prefix");
     String info1OTP = ConfigHelper.getColoredString("one_time_payment_machine.info_1");
     String info2OTP = ConfigHelper.getColoredString("one_time_payment_machine.info_1");
     String transient1OTP = ConfigHelper.getColoredString("one_time_payment_machine.transient_1");
     String transient2OTP = ConfigHelper.getColoredString("one_time_payment_machine.transient_2");

     String chatFareOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat");
     String chatRemainingOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat_remaining");
     String insufficientOTP = ConfigHelper.getColoredString("one_time_payment_machine.chat_insufficient");

    // ============================================================================================================== //
    //                                              DebitCard Editor                                                  //
    // ============================================================================================================== //
     boolean dceEnabled = ConfigHelper.getBoolean("card_editor.enable");
     String nameDCE = ConfigHelper.getString("card_editor.name");

     String prefixDCE = ConfigHelper.getString("card_editor.prefix");
     String info1DCE = ConfigHelper.getColoredString("card_editor.info_1");
     String info2DCE = ConfigHelper.getColoredString("card_editor.info_2");
     String info3DCE = ConfigHelper.getColoredString("card_editor.info_3");

     String promptAddDCE = ConfigHelper.getColoredString("card_editor.prompt_add");
     String promptRemoveDCE = ConfigHelper.getColoredString("card_editor.prompt_bankin");
     String promptAutoAddAmountDCE = ConfigHelper.getColoredString("card_editor.prompt_auto_addamount");
     String promptAutoDailyLimitDCE = ConfigHelper.getColoredString("card_editor.prompt_auto_dailylimit");
     String successDCE = ConfigHelper.getColoredString("card_editor.success");
     String failDCE = ConfigHelper.getColoredString("card_editor.fail");
     String newBalanceDCE = ConfigHelper.getColoredString("card_editor.new_balance");

    // ============================================================================================================== //
    //                                              DebitCard Validator                                               //
    // ============================================================================================================== //
     boolean validatorEnabled = ConfigHelper.getBoolean("validator.enable");

     String validatorVanillaPrefix = ConfigHelper.getString("validator.vanilla.prefix");

     String validatorTrainCartsPrefix = ConfigHelper.getString("validator.train_carts.prefix");
     String validatorTrainCartsName = ConfigHelper.getString("validator.train_carts.name");
     String validatorTrainCartsDescription = ConfigHelper.getString("validator.train_carts.description");

     String validatorComplete = ConfigHelper.getColoredString("validator.complete");
     String validatorFail = ConfigHelper.getColoredString("validator.no_card");

    // ============================================================================================================== //
    //                                                 Transfer Gate                                                  //
    // ============================================================================================================== //
     boolean transferGateEnabled = ConfigHelper.getBoolean("transfer_gate.enable");
     String prefixTransfer = ConfigHelper.getString("transfer_gate.prefix");
     String info1Transfer = ConfigHelper.getColoredString("transfer_gate.info_1");
     String info2Transfer = ConfigHelper.getColoredString("transfer_gate.info_2");
     String info3Transfer = ConfigHelper.getColoredString("transfer_gate.info_3");
     String info4Transfer = ConfigHelper.getColoredString("transfer_gate.info_4");
     String transient1Transfer = ConfigHelper.getColoredString("transfer_gate.transient_1");
     String transient2Transfer = ConfigHelper.getColoredString("transfer_gate.transient_2");

     String chatTicketTransfer = ConfigHelper.getColoredString("transfer_gate.chat_ticket");
     String chatTicketErrorTransfer = ConfigHelper.getColoredString("transfer_gate.chat_ticket_error");

    // ============================================================================================================== //
    //                                                   DebitCard                                                    //
    // ============================================================================================================== //
     String debitCardName = ConfigHelper.getColoredString("debit_name");
     String ownerPrefix = ConfigHelper.getColoredString("owner_prefix");
     String balancePrefix = ConfigHelper.getColoredString("balance_prefix");

    // ============================================================================================================== //
    //                                                     Ticket                                                     //
    // ============================================================================================================== //
     String ticketName = ConfigHelper.getColoredString("ticket.name");
     String ticketPrefixIn = ConfigHelper.getColoredString("ticket.from");
     String ticketPrefixOut = ConfigHelper.getColoredString("ticket.to");
     String ticketPrefixFare = ConfigHelper.getColoredString("ticket.fare");

    // ============================================================================================================== //
    //                                                  Helper Class                                                  //
    // ============================================================================================================== //
    /**
     * Inner helper class for reading config files.
     * @see MFConfig
     */
    static class ConfigHelper {
         static final FileConfiguration config = MTFA.PLUGIN.getConfig();

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
