package com.hasunemiku2015.metrofare;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class MetroConfiguration {
    public static MetroConfiguration INSTANCE = new MetroConfiguration();

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

     boolean createCompanyPermissionEnabled = ConfigHelper.getBoolean("permission.company.create");

     boolean adminCompanyPermissionEnabled = ConfigHelper.getBoolean("permission.company.admin");

    public boolean hasBuildGatePermission(Player player) {
        return gatePermissionEnabled || player.hasPermission("metro_fare.build_gate");
    }
    public boolean hasBuildEditorPermission(Player player) {
        return editorPermissionEnabled || player.hasPermission("metro_fare.build_editor");
    }
    public boolean hasDataTablePermission(Player player) {
        return dataBasePermissionEnabled || player.hasPermission("metro_fare.database");
    }
    public boolean noTicketingPermission(Player player) {
        return !ticketingPermissionEnabled && !player.hasPermission("metro_fare.ticketing");
    }
    public boolean hasFenceGatePermission(Player player) {
        return fenceGatePermissionEnabled || player.hasPermission("metro_fare.fence");
    }

    public boolean hasCreateCompanyPermission(Player player) {
        return createCompanyPermissionEnabled || player.hasPermission("metro_fare.create_company");
    }

    public boolean hasAdminCompanyPermission(Player player) {
        return adminCompanyPermissionEnabled || player.hasPermission("metro_fare.admin_company");
    }

    // ============================================================================================================== //
    //                                                Theme                                                           //
    // ============================================================================================================== //
     String prefix = ConfigHelper.getString("prefix");

     ChatColor base = ConfigHelper.getChatColor("theme.main");
     ChatColor error = ConfigHelper.getChatColor("theme.error");
     ChatColor input = ConfigHelper.getChatColor("theme.input_values");
     ChatColor output = ConfigHelper.getChatColor("theme.results");

     String currencyUnit = getCurrencyUnit(ConfigHelper.getInt("currency_unit"));
     private static String getCurrencyUnit(int value) {
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
     boolean exitGateEnabled = ConfigHelper.getBoolean("exit_gate.enable");
     String prefixOut = ConfigHelper.getString("exit_gate.prefix");
     String info1Out = ConfigHelper.getColoredString("exit_gate.info_1");
     String info2Out = ConfigHelper.getColoredString("exit_gate.info_2");
     String transient1Out = ConfigHelper.getColoredString("exit_gate.transient_1");
     String transient2Out = ConfigHelper.getColoredString("exit_gate.transient_2");

     String debitCardInvalidOut = ConfigHelper.getColoredString("exit_gate.debit_card.card_invalid");
     String playerInvalidOut = ConfigHelper.getColoredString("exit_gate.debit_card.player_invalid");
     String cardNotEnteredOut = ConfigHelper.getColoredString("exit_gate.debit_card.card_not_entered");

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
     String info2OTP = ConfigHelper.getColoredString("one_time_payment_machine.info_2");
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
    //                                            Single Use Ticket Machine                                           //
    // ============================================================================================================== //
     boolean stmEnabled = ConfigHelper.getBoolean("single_use_ticket_machine.enable"); 

     String prefixSTM = ConfigHelper.getColoredString("single_use_ticket_machine.prefix");
     String info1STM = ConfigHelper.getColoredString("single_use_ticket_machine.info_1"); 
     String info2STM = ConfigHelper.getColoredString("single_use_ticket_machine.info_2"); 
     
     String promptStationCodeSTM = ConfigHelper.getColoredString("single_use_ticket_machine.prompt_station_code"); 
     String promptInvalidStationCodeSTM = ConfigHelper.getColoredString("single_use_ticket_machine.prompt_invalid_station_code"); 
     String failSTM = ConfigHelper.getColoredString("single_use_ticket_machine.fail"); 

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
     * @see MetroConfiguration
     */
    static class ConfigHelper {
         static final FileConfiguration config = MetroFare.PLUGIN.getConfig();

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

        /**
         * Returns a ChatColor from the config.yml.
         * @param configKey Specified key to read the value from.
         * @return ChatColor value of the specified key, defaults to RESET if null;
         */
        @NotNull
        protected static ChatColor getChatColor(String configKey) {
            String chatColorString = config.getString(configKey);
            return chatColorString == null ? ChatColor.RESET : ChatColor.valueOf(chatColorString);
        }
    }

    // ============================================================================================================== //
    //                                                     Getter                                                     //
    // ============================================================================================================== //

    public double getDefaultFare() {
        return defaultFare;
    }

    public int getOpenTime() {
        return openTime;
    }

    public boolean isVaultIntegrationEnabled() {
        return vaultIntegrationEnabled;
    }

    public boolean isGatePermissionEnabled() {
        return gatePermissionEnabled;
    }

    public boolean isEditorPermissionEnabled() {
        return editorPermissionEnabled;
    }

    public boolean isDataBasePermissionEnabled() {
        return dataBasePermissionEnabled;
    }

    public boolean isTicketingPermissionEnabled() {
        return ticketingPermissionEnabled;
    }

    public boolean isFenceGatePermissionEnabled() {
        return fenceGatePermissionEnabled;
    }

    public boolean isCreateCompanyPermissionEnabled() {
        return createCompanyPermissionEnabled;
    }

    public boolean isAdminCompanyPermissionEnabled() {
        return adminCompanyPermissionEnabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getBase() {
        return base;
    }

    public ChatColor getError() {
        return error;
    }

    public ChatColor getInput() {
        return input;
    }

    public ChatColor getOutput() {
        return output;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public boolean isEntryGateEnabled() {
        return entryGateEnabled;
    }

    public String getPrefixIn() {
        return prefixIn;
    }

    public String getInfo1In() {
        return info1In;
    }

    public String getInfo2In() {
        return info2In;
    }

    public String getTransient1In() {
        return transient1In;
    }

    public String getTransient2In() {
        return transient2In;
    }

    public String getDebitCardInvalidIn() {
        return debitCardInvalidIn;
    }

    public String getCompanyInvalidIn() {
        return companyInvalidIn;
    }

    public String getPlayerInvalidIn() {
        return playerInvalidIn;
    }

    public String getCardEnteredIn() {
        return cardEnteredIn;
    }

    public String getInsufficientIn() {
        return insufficientIn;
    }

    public String getTicketInvalidIn() {
        return ticketInvalidIn;
    }

    public String getTicketEnteredIn() {
        return ticketEnteredIn;
    }

    public String getEntryCompanyInvalidIn() {
        return entryCompanyInvalidIn;
    }

    public String getStationInvalidIn() {
        return stationInvalidIn;
    }

    public String getChatIn() {
        return chatIn;
    }

    public boolean isExitGateEnabled() {
        return exitGateEnabled;
    }

    public String getPrefixOut() {
        return prefixOut;
    }

    public String getInfo1Out() {
        return info1Out;
    }

    public String getInfo2Out() {
        return info2Out;
    }

    public String getTransient1Out() {
        return transient1Out;
    }

    public String getTransient2Out() {
        return transient2Out;
    }

    public String getDebitCardInvalidOut() {
        return debitCardInvalidOut;
    }

    public String getPlayerInvalidOut() {
        return playerInvalidOut;
    }

    public String getCardNotEnteredOut() {
        return cardNotEnteredOut;
    }

    public String getTicketInvalidOut() {
        return ticketInvalidOut;
    }

    public String getTicketNotEnteredOut() {
        return ticketNotEnteredOut;
    }

    public String getTicketWrongExitCompanyOut() {
        return ticketWrongExitCompanyOut;
    }

    public String getTicketInvalidInterCompanyOut() {
        return ticketInvalidInterCompanyOut;
    }

    public String getTicketInsufficientFareOut() {
        return ticketInsufficientFareOut;
    }

    public String getChatOut() {
        return chatOut;
    }

    public String getChatFateOut() {
        return chatFateOut;
    }

    public String getChatRemaining() {
        return chatRemaining;
    }

    public boolean isOtpEnabled() {
        return otpEnabled;
    }

    public String getPrefixOTP() {
        return prefixOTP;
    }

    public String getInfo1OTP() {
        return info1OTP;
    }

    public String getInfo2OTP() {
        return info2OTP;
    }

    public String getTransient1OTP() {
        return transient1OTP;
    }

    public String getTransient2OTP() {
        return transient2OTP;
    }

    public String getChatFareOTP() {
        return chatFareOTP;
    }

    public String getChatRemainingOTP() {
        return chatRemainingOTP;
    }

    public String getInsufficientOTP() {
        return insufficientOTP;
    }

    public boolean isDceEnabled() {
        return dceEnabled;
    }

    public String getNameDCE() {
        return nameDCE;
    }

    public String getPrefixDCE() {
        return prefixDCE;
    }

    public String getInfo1DCE() {
        return info1DCE;
    }

    public String getInfo2DCE() {
        return info2DCE;
    }

    public String getInfo3DCE() {
        return info3DCE;
    }

    public String getPromptAddDCE() {
        return promptAddDCE;
    }

    public String getPromptRemoveDCE() {
        return promptRemoveDCE;
    }

    public String getPromptAutoAddAmountDCE() {
        return promptAutoAddAmountDCE;
    }

    public String getPromptAutoDailyLimitDCE() {
        return promptAutoDailyLimitDCE;
    }

    public String getSuccessDCE() {
        return successDCE;
    }

    public String getFailDCE() {
        return failDCE;
    }

    public String getNewBalanceDCE() {
        return newBalanceDCE;
    }

    public boolean isStmEnabled () {
        return stmEnabled; 
    }

    public String getPrefixSTM () {
        return prefixSTM; 
    }

    public String getInfo1STM () {
        return info1STM; 
    }

    public String getInfo2STM () {
        return info2STM; 
    }

    public String getPromptStationCodeSTM () {
        return promptStationCodeSTM; 
    }

    public String getPromptInvalidStationCodeSTM () {
        return promptInvalidStationCodeSTM; 
    }

    public String getFailSTM () {
        return failSTM; 
    }

    public boolean isValidatorEnabled() {
        return validatorEnabled;
    }

    public String getValidatorVanillaPrefix() {
        return validatorVanillaPrefix;
    }

    public String getValidatorTrainCartsPrefix() {
        return validatorTrainCartsPrefix;
    }

    public String getValidatorTrainCartsName() {
        return validatorTrainCartsName;
    }

    public String getValidatorTrainCartsDescription() {
        return validatorTrainCartsDescription;
    }

    public String getValidatorComplete() {
        return validatorComplete;
    }

    public String getValidatorFail() {
        return validatorFail;
    }

    public boolean isTransferGateEnabled() {
        return transferGateEnabled;
    }

    public String getPrefixTransfer() {
        return prefixTransfer;
    }

    public String getInfo1Transfer() {
        return info1Transfer;
    }

    public String getInfo2Transfer() {
        return info2Transfer;
    }

    public String getInfo3Transfer() {
        return info3Transfer;
    }

    public String getInfo4Transfer() {
        return info4Transfer;
    }

    public String getTransient1Transfer() {
        return transient1Transfer;
    }

    public String getTransient2Transfer() {
        return transient2Transfer;
    }

    public String getChatTicketTransfer() {
        return chatTicketTransfer;
    }

    public String getChatTicketErrorTransfer() {
        return chatTicketErrorTransfer;
    }

    public String getDebitCardName() {
        return debitCardName;
    }

    public String getOwnerPrefix() {
        return ownerPrefix;
    }

    public String getBalancePrefix() {
        return balancePrefix;
    }

    public String getTicketName() {
        return ticketName;
    }

    public String getTicketPrefixIn() {
        return ticketPrefixIn;
    }

    public String getTicketPrefixOut() {
        return ticketPrefixOut;
    }

    public String getTicketPrefixFare() {
        return ticketPrefixFare;
    }
}
