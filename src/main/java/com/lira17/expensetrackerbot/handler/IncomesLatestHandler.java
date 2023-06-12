package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Income;
import com.lira17.expensetrackerbot.service.IncomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.INCOME_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedAmountWithCurrency;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedDateFromLocalDate;

@Component
@Slf4j
public class IncomesLatestHandler implements CommandMessageHandler {

    @Autowired
    IncomeService incomeService;

    @Autowired
    ErrorHandler errorHandler;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);
        List<SendMessage> messages;

        try {
            messages = incomeService.getLastIncomes()
                    .stream()
                    .map(income -> generateSendMessage(chatId, getIncomeMessageText(income)))
                    .toList();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        return messages;
    }

    private String getIncomeMessageText(Income income) {
        String amount = getFormattedAmountWithCurrency(income.getAmount(), income.getCurrency());
        String date = getFormattedDateFromLocalDate(income.getDate());

        return String.format(INCOME_MSG, income.getTitle(), income.getDescription(), amount, date, income.getCategory().getTitle());
    }

    @Override
    public String getCommand() {
        return Command.LATEST_INCOMES.getDescription();
    }
}
