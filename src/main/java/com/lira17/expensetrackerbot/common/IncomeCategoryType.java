package com.lira17.expensetrackerbot.common;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum IncomeCategoryType {
    REGULAR("REGULAR"),
    ONE_TIME("ONE_TIME");

    private final String name;

    IncomeCategoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static List<IncomeCategoryType> getAll() {
        return Arrays.asList(IncomeCategoryType.values());
    }

    public static List<String> getAllNames() {
        return getAll().stream().map(IncomeCategoryType::getName).toList();
    }

    public static Optional<IncomeCategoryType> getByName(String name) {
        return getAll().stream().filter(place -> place.getName().equals(name)).findFirst();
    }

    public static IncomeCategoryType getIncomeCategoryType(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(":");
        return getByName(data[1]).orElseThrow(() -> new RuntimeException("Wrong IncomeCategoryType"));

    }
}
