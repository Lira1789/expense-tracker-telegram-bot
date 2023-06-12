package com.lira17.expensetrackerbot.common;

import java.util.Arrays;
import java.util.Optional;

public enum Step {
    STARTED_ADDING_EXPENSE_CATEGORY("started_adding_expense_category"),
    ADDED_EXPENSE_CATEGORY_TITLE("added_expense_category_title"),
    STARTED_ADDING_INCOME_CATEGORY("started_adding_income_category"),
    ADDED_INCOME_CATEGORY_TITLE("added_income_category_title"),
    STARTED_ADDING_EXPENSE("started_adding_expense"),
    ADDED_EXPENSE_TITLE_DESC_DATE("added_expense_title_desc_date"),
    STARTED_ADDING_INCOME("started_adding_income"),
    ADDED_INCOME_TITLE_DESC_DATE("added_income_title_desc_date");


    private final String description;

    Step(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<Step> getByDescription(String description) {
        return Arrays.stream(Step.values()).filter(step -> step.getDescription().equals(description)).findFirst();
    }
}

