package com.lira17.expensetrackerbot.request;

import com.lira17.expensetrackerbot.common.ExpenseCategoryType;
import com.lira17.expensetrackerbot.model.ExpenseCategory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExpenseCategoryRequest {

    private static final Map<String, ExpenseCategory> EXPENSE_CATEGORY_REQUEST_MAP = new ConcurrentHashMap<>();

    public void createRequest(String text, String chatId) {
        var expenseCategory = new ExpenseCategory();
        expenseCategory.setTitle(text);
        EXPENSE_CATEGORY_REQUEST_MAP.put(chatId, expenseCategory);
    }

    public void addCategoryType(Update update, String chatId) {
        var expenseCategoryType = ExpenseCategoryType.getExpenseCategoryType(update.getCallbackQuery());
        var expenseCategory = EXPENSE_CATEGORY_REQUEST_MAP.get(chatId);
        expenseCategory.setType(expenseCategoryType);
    }

    public ExpenseCategory getRequest(String chatId) {
        return EXPENSE_CATEGORY_REQUEST_MAP.get(chatId);
    }

    public void clearRequest(String chatId) {
        EXPENSE_CATEGORY_REQUEST_MAP.remove(chatId);
    }
}
