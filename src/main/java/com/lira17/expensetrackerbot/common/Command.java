package com.lira17.expensetrackerbot.common;

import java.util.Arrays;
import java.util.List;

public enum Command {

    INFO("/info"),
    ADD_EXPENSE("/add_expense"),
    ADD_EXPENSE_CATEGORY("/add_expense_category"),
    ADD_INCOME("/add_income"),
    ADD_INCOME_CATEGORY("/add_income_category"),
    LATEST_INCOMES("/latest_incomes"),
    LATEST_EXPENSES("/latest_expenses"),
    MONTH_BALANCE("/month_balance"),
    TOTAL_BALANCE("/total_balance");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<Command> getAll() {
        return Arrays.asList(Command.values());
    }

    public static List<String> getAllNames() {
        return getAll().stream().map(Command::getDescription).toList();
    }
}
