package com.lira17.expensetrackerbot.common;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ExpenseCategoryType {
    ESSENTIAL("ESSENTIAL"),
    FUN("FUN"),
    DEVELOPMENT("SELF-DEVELOPMENT"),
    SAVINGS("SAVINGS");

    private final String name;

    ExpenseCategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<ExpenseCategoryType> getAll() {
        return Arrays.asList(ExpenseCategoryType.values());
    }

    public static List<String> getAllNames() {
        return getAll().stream().map(ExpenseCategoryType::getName).toList();
    }

    public static Optional<ExpenseCategoryType> getByName(String name) {
        return getAll().stream().filter(place -> place.getName().equals(name)).findFirst();
    }

    public static ExpenseCategoryType getExpenseCategoryType(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        return getByName(data[1]).orElseThrow(() -> new RuntimeException("Wrong ExpenseCategoryType"));

    }
}
