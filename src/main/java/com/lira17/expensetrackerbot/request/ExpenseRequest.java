package com.lira17.expensetrackerbot.request;

import com.lira17.expensetrackerbot.common.Currency;
import com.lira17.expensetrackerbot.model.Expense;
import com.lira17.expensetrackerbot.model.ExpenseCategory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lira17.expensetrackerbot.util.MessageUtil.getDateFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getExpenseDescriptionFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getExpenseTitleFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getTextFromCallBackQuery;

@Component
public class ExpenseRequest {

    private static final Map<String, Expense> EXPENSE_REQUEST_MAP = new ConcurrentHashMap<>();

    public void createRequest(Update update, String chatId) {
        var expense = new Expense();
        expense.setTitle(getExpenseTitleFromMessage(update.getMessage()));
        expense.setDescription(getExpenseDescriptionFromMessage(update.getMessage()));
        expense.setDate(getDateFromMessage(update.getMessage()));
        EXPENSE_REQUEST_MAP.put(chatId, expense);
    }

    public void addAmountToRequest(Update update, String chatId) {
        var expense = EXPENSE_REQUEST_MAP.get(chatId);
        expense.setAmount(Double.parseDouble(update.getMessage().getText()));
    }

    public void addCurrency(Update update, String chatId) {
        var expense = EXPENSE_REQUEST_MAP.get(chatId);
        expense.setCurrency(Currency.getCurrency(update.getCallbackQuery()));
    }

    public void addCategory(Update update, String chatId) {
        var expense = EXPENSE_REQUEST_MAP.get(chatId);
        String categoryId = getTextFromCallBackQuery(update.getCallbackQuery());
        var expenseCategory = new ExpenseCategory();
        expenseCategory.setId(Long.parseLong(categoryId));
        expense.setCategory(expenseCategory);
    }

    public Expense getRequest(String chatId) {
        return EXPENSE_REQUEST_MAP.get(chatId);
    }

    public void clearRequest(String chatId) {
        EXPENSE_REQUEST_MAP.remove(chatId);
    }
}
