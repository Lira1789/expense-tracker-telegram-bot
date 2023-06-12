package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Expense;
import com.lira17.expensetrackerbot.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.EXPENSE_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedAmountWithCurrency;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedDateFromLocalDate;

@Component
@Slf4j
public class ExpensesLatestHandler implements CommandMessageHandler {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    ErrorHandler errorHandler;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);
        List<SendMessage> messages;
        try {
            messages = expenseService.getLastExpenses()
                    .stream()
                    .map(expense -> generateSendMessage(chatId, getExpenseMessageText(expense)))
                    .toList();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        return messages;
    }

    private String getExpenseMessageText(Expense expense) {
        String amount = getFormattedAmountWithCurrency(expense.getAmount(), expense.getCurrency());
        String date = getFormattedDateFromLocalDate(expense.getDate());

        return String.format(EXPENSE_MSG, expense.getTitle(), expense.getDescription(), amount, date, expense.getCategory().getTitle());
    }

    @Override
    public String getCommand() {
        return Command.LATEST_EXPENSES.getDescription();
    }
}
