package com.lira17.expensetrackerbot.model;

import com.lira17.expensetrackerbot.common.ExpenseCategoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpenseCategory extends Category {
    private ExpenseCategoryType type;
}

