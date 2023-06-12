package com.lira17.expensetrackerbot.common;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CallBackQueryType {

    EXPENSE_CATEGORY_TYPE("expense-category-type"),

    INCOME_CATEGORY_TYPE("income-category-type"),
    EXPENSE_CURRENCY("expense-currency"),
    EXPENSE_CATEGORY("expense-category"),
    INCOME_CURRENCY("income-currency"),
    INCOME_CATEGORY("income-category"),
    ADD_EXPENSE("/add_expense"),
    ADD_EXPENSE_CATEGORY("/add_expense_category"),
    ADD_INCOME("/add_income"),
    ADD_INCOME_CATEGORY("/add_income_category"),
    LATEST_INCOMES("/latest-incomes"),
    LATEST_EXPENSES("/latest-expenses"),
    MONTH_BALANCE("/month_balance"),
    TOTAL_BALANCE("/total_balance");

    private final String name;

    CallBackQueryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<CallBackQueryType> getAll() {
        return Arrays.asList(CallBackQueryType.values());
    }

    public static Optional<CallBackQueryType> getByName(String name) {
        return getAll().stream().filter(place -> place.getName().equals(name)).findFirst();
    }

    public static CallBackQueryType getCallBackQueryType(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        return getByName(data[0]).orElseThrow(() -> new RuntimeException("Wrong CallBackQueryType"));

    }

}
