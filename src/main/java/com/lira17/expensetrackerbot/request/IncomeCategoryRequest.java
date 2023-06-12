package com.lira17.expensetrackerbot.request;

import com.lira17.expensetrackerbot.common.IncomeCategoryType;
import com.lira17.expensetrackerbot.model.IncomeCategory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IncomeCategoryRequest {

    private static final Map<String, IncomeCategory> INCOME_CATEGORY_REQUEST_MAP = new ConcurrentHashMap<>();

    public void createRequest(String text, String chatId) {
        var incomeCategory = new IncomeCategory();
        incomeCategory.setTitle(text);
        INCOME_CATEGORY_REQUEST_MAP.put(chatId, incomeCategory);
    }

    public void addCategoryType(Update update, String chatId) {
        var incomeCategoryType = IncomeCategoryType.getIncomeCategoryType(update.getCallbackQuery());
        var incomeCategory = INCOME_CATEGORY_REQUEST_MAP.get(chatId);
        incomeCategory.setType(incomeCategoryType);
    }

    public IncomeCategory getRequest(String chatId) {
        return INCOME_CATEGORY_REQUEST_MAP.get(chatId);
    }

    public void clearRequest(String chatId) {
        INCOME_CATEGORY_REQUEST_MAP.remove(chatId);
    }
}
