package com.lira17.expensetrackerbot.model;

import com.lira17.expensetrackerbot.common.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Income {
    private long id;
    private String title;
    private String description;
    private LocalDate date;
    private double amount;
    private Currency currency;
    private IncomeCategory category;
}
