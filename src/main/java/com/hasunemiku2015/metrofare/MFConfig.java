package com.hasunemiku2015.metrofare;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MFConfig {
    //General
    private static String prefix;
    private static String currencyUnit;
    private static double defaultFare;
    private static int openTime;
    private static boolean enableVaultIntegration;

    //ChatColors
    private static ChatColor base;
    private static ChatColor error;
    private static ChatColor input;
    private static ChatColor output;

    //Entry Gate
    private static boolean entryGateEnabled;
    private static String prefixIn;
    private static String info1In;
    private static String info2In;
    private static String transientIn1;
    private static String transientIn2;
    private static String chatIn;
    private static String insufficientIn;

    //Exit Gate
    private static boolean exitGateEnabled;
    private static String prefixOut;
    private static String info1Out;
    private static String info2Out;
    private static String transientOut1;
    private static String transientOut2;
    private static String chatOut;
    private static String chatFareOut;
    private static String chatRemaining;

    // OTP Machine
    private static boolean otpEnabled;
    private static String prefixOTP;
    private static String info1OTP;
    private static String info2OTP;
    private static String transient1OTP;
    private static String transient2OTP;
    private static String chatFareOTP;
    private static String chatRemainingOTP;
    private static String insufficientOTP;

    // DCE
    private static boolean dceEnabled;
    private static String prefixDCE;
    private static String nameDCE;
    private static String info1DCE;
    private static String info2DCE;
    private static String info3DCE;
    private static String promptAddDCE;
    private static String promptRemoveDCE;
    private static String promptAutoAddAmountDCE;
    private static String promptAutoDailyLimitDCE;
    private static String successDCE;
    private static String newBalanceDCE;
    private static String failDCE;

    // Validator and TransferGate
    private static String validatorVanillaPrefix;
    private static String validatorTrainCartsPrefix;
    private static String validatorTrainCartsName;
    private static String validatorTrainCartsDescription;
    private static boolean validatorEnabled;
    private static String validatorComplete;
    private static String validatorFail;

    private static boolean transferGateEnabled;
    private static String prefixTransfer;
    private static String info1Transfer;
    private static String info2Transfer;
    private static String info3Transfer;
    private static String info4Transfer;
    private static String transient1Transfer;
    private static String transient2Transfer;
    private static String chatTicketTransfer;
    private static String chatTicketErrorTransfer;

    //Debit Card
    private static String debitCardName;
    private static String ownerPrefix;

    //Ticket
    private static String ticketName;
    private static String ticketPrefixIn;
    private static String ticketPrefixOut;
    private static String ticketPrefixFare;
    private static String balancePrefix;

    //Init
    public static void init() {
        prefix = MTFA.plugin.getConfig().getString("prefix");
        MTFA.plugin.saveDefaultConfig();

        currencyUnit = "$";
        switch(MTFA.plugin.getConfig().getInt("currency_unit")){
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

        defaultFare = MTFA.plugin.getConfig().getDouble("default_fare");
        openTime = MTFA.plugin.getConfig().getInt("open_time");
        enableVaultIntegration = MTFA.plugin.getConfig().getBoolean("vault_integration");

        base = ChatColor.valueOf(MTFA.plugin.getConfig().getString("theme.main"));
        error = ChatColor.valueOf(MTFA.plugin.getConfig().getString("theme.error"));
        input = ChatColor.valueOf(MTFA.plugin.getConfig().getString("theme.input_values"));
        output = ChatColor.valueOf(MTFA.plugin.getConfig().getString("theme.results"));

        entryGateEnabled = MTFA.plugin.getConfig().getBoolean("entry_gate.enable");
        prefixIn = MTFA.plugin.getConfig().getString("entry_gate.prefix");
        info1In = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.info_1")));
        info2In = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.info_2")));
        transientIn1 = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.transient_1")));
        transientIn2 = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.transient_2")));
        chatIn = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.chat")));
        insufficientIn = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("entry_gate.insufficient")));

        exitGateEnabled = MTFA.plugin.getConfig().getBoolean("exit_gate.enable");
        prefixOut = MTFA.plugin.getConfig().getString("exit_gate.prefix");
        info1Out = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.info_1")));
        info2Out = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.info_2")));
        transientOut1 = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.transient_1")));
        transientOut2 = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.transient_2")));
        chatOut = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.chat")));
        chatFareOut = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.chat_fare")));
        chatRemaining = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("exit_gate.chat_remaining")));

        otpEnabled = MTFA.plugin.getConfig().getBoolean("one_time_payment_machine.enable");
        prefixOTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.prefix")));
        info1OTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.info_1")));
        info2OTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.info_2")));
        transient1OTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.transient_1")));
        transient2OTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.transient_2")));
        chatFareOTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.chat")));
        chatRemainingOTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.chat_remaining")));
        insufficientOTP = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("one_time_payment_machine.insufficient")));

        dceEnabled = MTFA.plugin.getConfig().getBoolean("card_editor.enable");
        prefixDCE = MTFA.plugin.getConfig().getString("card_editor.prefix");
        nameDCE = MTFA.plugin.getConfig().getString("card_editor.name");
        info1DCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.info_1")));
        info2DCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.info_2")));
        info3DCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.info_3")));
        promptAddDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.prompt_add")));
        promptRemoveDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.prompt_bankin")));
        promptAutoAddAmountDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.prompt_auto_addamount")));
        promptAutoDailyLimitDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.prompt_auto_dailylimit")));
        successDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.success")));
        failDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.fail")));
        newBalanceDCE = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("card_editor.new_balance")));

        validatorEnabled = MTFA.plugin.getConfig().getBoolean("validator.enable");
        validatorVanillaPrefix = MTFA.plugin.getConfig().getString("validator.vanilla.prefix");
        validatorTrainCartsPrefix = MTFA.plugin.getConfig().getString("validator.train_carts.prefix");
        validatorTrainCartsName= MTFA.plugin.getConfig().getString("validator.train_carts.name");
        validatorTrainCartsDescription= MTFA.plugin.getConfig().getString("validator.train_carts.description");
        validatorComplete = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("validator.complete")));
        validatorFail = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("validator.no_card")));

        transferGateEnabled = MTFA.plugin.getConfig().getBoolean("transfer_gate.enable");
        prefixTransfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.prefix")));
        info1Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.info_1")));
        info2Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.info_2")));
        info3Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.info_3")));
        info4Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.info_4")));
        transient1Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.transient_1")));
        transient2Transfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.transient_2")));
        chatTicketTransfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.chat_ticket")));
        chatTicketErrorTransfer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("transfer_gate.chat_ticket_error")));

        debitCardName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("debit_name")));
        ownerPrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("owner_prefix")));
        balancePrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("balance_prefix")));

        ticketName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("ticket.name")));
        ticketPrefixIn = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("ticket.from")));
        ticketPrefixOut = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("ticket.to")));
        ticketPrefixFare = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MTFA.plugin.getConfig().getString("ticket.fare")));
    }

    //Getters
    public static String getPrefix() {
        return prefix;
    }
    public static String getCurrencyUnit() {
        return currencyUnit;
    }
    public static double getDefaultFare() {
        return defaultFare;
    }
    public static int getOpenTime() {
        return openTime;
    }
    public static boolean isEnableVaultIntegration() {
        return enableVaultIntegration;
    }

    public static ChatColor getBase() {
        return base;
    }
    public static ChatColor getError() {
        return error;
    }
    public static ChatColor getInput() {
        return input;
    }
    public static ChatColor getOutput() {
        return output;
    }

    public static boolean isEntryGateEnabled() {
        return entryGateEnabled;
    }
    public static String getPrefixIn() {
        return prefixIn;
    }
    public static String getInfo1In() {
        return info1In;
    }
    public static String getInfo2In() {
        return info2In;
    }
    public static String getTransientIn1() {
        return transientIn1;
    }
    public static String getTransientIn2() {
        return transientIn2;
    }
    public static String getChatIn() {
        return chatIn;
    }
    public static String getInsufficientIn() {
        return insufficientIn;
    }

    public static boolean isExitGateEnabled() {
        return exitGateEnabled;
    }
    public static String getPrefixOut() {
        return prefixOut;
    }
    public static String getInfo1Out() {
        return info1Out;
    }
    public static String getInfo2Out() {
        return info2Out;
    }
    public static String getInfo3Transfer() {
        return info3Transfer;
    }
    public static String getInfo4Transfer() {
        return info4Transfer;
    }
    public static String getTransientOut1() {
        return transientOut1;
    }
    public static String getTransientOut2() {
        return transientOut2;
    }
    public static String getChatOut() {
        return chatOut;
    }
    public static String getChatFareOut() {
        return chatFareOut;
    }
    public static String getChatRemaining() {
        return chatRemaining;
    }

    public static boolean isOtpEnabled() {
        return otpEnabled;
    }
    public static String getPrefixOTP() {
        return prefixOTP;
    }
    public static String getInfo1OTP() {
        return info1OTP;
    }
    public static String getInfo2OTP() {
        return info2OTP;
    }
    public static String getTransient1OTP() {
        return transient1OTP;
    }
    public static String getTransient2OTP() {
        return transient2OTP;
    }
    public static String getChatFareOTP() {
        return chatFareOTP;
    }
    public static String getChatRemainingOTP() {
        return chatRemainingOTP;
    }
    public static String getInsufficientOTP() {
        return insufficientOTP;
    }

    public static boolean isDceEnabled() {
        return dceEnabled;
    }
    public static String getPrefixDCE() {
        return prefixDCE;
    }
    public static String getNameDCE() {
        return nameDCE;
    }
    public static String getInfo1DCE() {
        return info1DCE;
    }
    public static String getInfo2DCE() {
        return info2DCE;
    }
    public static String getInfo3DCE() {
        return info3DCE;
    }
    public static String getPromptAddDCE() {
        return promptAddDCE;
    }
    public static String getPromptRemoveDCE() {
        return promptRemoveDCE;
    }
    public static String getPromptAutoAddAmountDCE() {
        return promptAutoAddAmountDCE;
    }
    public static String getPromptAutoDailyLimitDCE() {
        return promptAutoDailyLimitDCE;
    }
    public static String getSuccessDCE() {
        return successDCE;
    }
    public static String getNewBalanceDCE() {
        return newBalanceDCE;
    }
    public static String getFailDCE() {
        return failDCE;
    }

    public static boolean isValidatorEnabled() {
        return validatorEnabled;
    }
    public static String getValidatorVanillaPrefix() {
        return validatorVanillaPrefix;
    }
    public static String getValidatorTrainCartsPrefix() {
        return validatorTrainCartsPrefix;
    }
    public static String getValidatorTrainCartsName() {
        return validatorTrainCartsName;
    }
    public static String getValidatorTrainCartsDescription() {
        return validatorTrainCartsDescription;
    }
    public static String getValidatorComplete() {
        return validatorComplete;
    }
    public static String getValidatorFail() {
        return validatorFail;
    }

    public static boolean isTransferGateEnabled() {
        return transferGateEnabled;
    }
    public static String getPrefixTransfer() {
        return prefixTransfer;
    }
    public static String getInfo1Transfer() {
        return info1Transfer;
    }
    public static String getInfo2Transfer() {
        return info2Transfer;
    }
    public static String getTransient1Transfer() {
        return transient1Transfer;
    }
    public static String getTransient2Transfer() {
        return transient2Transfer;
    }
    public static String getChatTicketTransfer() {
        return chatTicketTransfer;
    }
    public static String getChatTicketErrorTransfer() {
        return chatTicketErrorTransfer;
    }

    public static String getDebitCardName() {
        return debitCardName;
    }
    public static String getOwnerPrefix() {
        return ownerPrefix;
    }
    public static String getBalancePrefix() {
        return balancePrefix;
    }

    public static String getTicketName() {
        return ticketName;
    }
    public static String getTicketPrefixIn() {
        return ticketPrefixIn;
    }
    public static String getTicketPrefixOut() {
        return ticketPrefixOut;
    }
    public static String getTicketPrefixFare() {
        return ticketPrefixFare;
    }

    //Permission Checkers
    public static boolean hasBuildGatePermission(Player player) {
        return MTFA.plugin.getConfig().getBoolean("permission.gate") || player.hasPermission("mtfa.buildgate");
    }
    public static boolean hasBuildEditorPermission(Player player) {
        return MTFA.plugin.getConfig().getBoolean("permission.editor") || player.hasPermission("mtfa.buildeditor");
    }
    public static boolean hasDataTablePermission(Player player){
        return MTFA.plugin.getConfig().getBoolean("permission.database") || player.hasPermission("mtfa.database");
    }
    public static boolean noTicketingPermission(Player player) {
        return !MTFA.plugin.getConfig().getBoolean("permission.ticketing") && !player.hasPermission("mtfa.ticketing");
    }
    public static boolean hasFenceGatePermission(Player player) {
        return MTFA.plugin.getConfig().getBoolean("permission.fence") || player.hasPermission("mtfa.fence");
    }
}
