package com.lira17.expensetrackerbot.common;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum Currency {
    EUR("EUR"),
    RSD("RSD"),
    USD("USD"),
    RUB("RUB");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<Currency> getAll() {
        return Arrays.asList(Currency.values());
    }

    public static List<String> getAllNames() {
        return getAll().stream().map(Currency::getName).toList();
    }

    public static Optional<Currency> getByName(String name) {
        return getAll().stream().filter(place -> place.getName().equals(name)).findFirst();
    }

    public static Currency getCurrency(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        return getByName(data[1]).orElseThrow(() -> new RuntimeException("Wrong Currency"));

    }
}
