package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.TotalBalance;
import com.lira17.expensetrackerbot.service.TotalBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.TOTAL_BALANCE_NEGATIVE_MSG;
import static com.lira17.expensetrackerbot.common.Message.TOTAL_BALANCE_POSITIVE_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedAmountWithCurrency;

@Component
@Slf4j
public class TotalBalanceHandler implements CommandMessageHandler {

    @Autowired
    TotalBalanceService totalBalanceService;

    @Autowired
    ErrorHandler errorHandler;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);
        TotalBalance totalBalance;

        try {
            totalBalance = totalBalanceService.getTotalBalance();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        String totalBalanceMessageText = getTotalBalanceMessageText(totalBalance);

        return Collections.singletonList(generateSendMessage(chatId, totalBalanceMessageText));
    }

    private String getTotalBalanceMessageText(TotalBalance totalBalance) {
        String balanceAmount = getFormattedAmountWithCurrency(totalBalance.balanceAmount(), totalBalance.mainCurrency());

        String messageTemplate = totalBalance.isBalancePositive()
                ? TOTAL_BALANCE_POSITIVE_MSG
                : TOTAL_BALANCE_NEGATIVE_MSG;

        return String.format(messageTemplate, balanceAmount);
    }

    @Override
    public String getCommand() {
        return Command.TOTAL_BALANCE.getDescription();
    }
}
