package com.lira17.expensetrackerbot.request;

import com.lira17.expensetrackerbot.common.Currency;
import com.lira17.expensetrackerbot.model.Income;
import com.lira17.expensetrackerbot.model.IncomeCategory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lira17.expensetrackerbot.util.MessageUtil.getDateFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getExpenseDescriptionFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getExpenseTitleFromMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getTextFromCallBackQuery;

@Component
public class IncomeRequest {

    private static final Map<String, Income> INCOME_REQUEST_MAP = new ConcurrentHashMap<>();

    public void createRequest(Update update, String chatId) {
        var income = new Income();
        income.setTitle(getExpenseTitleFromMessage(update.getMessage()));
        income.setDescription(getExpenseDescriptionFromMessage(update.getMessage()));
        income.setDate(getDateFromMessage(update.getMessage()));
        INCOME_REQUEST_MAP.put(chatId, income);
    }

    public void addAmountToRequest(Update update, String chatId) {
        var income = INCOME_REQUEST_MAP.get(chatId);
        income.setAmount(Double.parseDouble(update.getMessage().getText()));
    }

    public void addCurrency(Update update, String chatId) {
        var income = INCOME_REQUEST_MAP.get(chatId);
        income.setCurrency(Currency.getCurrency(update.getCallbackQuery()));
    }

    public void addCategory(Update update, String chatId) {
        var income = INCOME_REQUEST_MAP.get(chatId);
        String categoryId = getTextFromCallBackQuery(update.getCallbackQuery());
        var incomecategory = new IncomeCategory();
        incomecategory.setId(Long.parseLong(categoryId));
        income.setCategory(incomecategory);
    }

    public Income getRequest(String chatId) {
        return INCOME_REQUEST_MAP.get(chatId);
    }

    public void clearRequest(String chatId) {
        INCOME_REQUEST_MAP.remove(chatId);
    }
}
