package com.lira17.expensetrackerbot.model;

import com.lira17.expensetrackerbot.common.IncomeCategoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IncomeCategory extends Category {
    IncomeCategoryType type;
}

