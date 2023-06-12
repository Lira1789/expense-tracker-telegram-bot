package com.lira17.expensetrackerbot.model;

import com.lira17.expensetrackerbot.common.Currency;
import lombok.Data;

@Data
public class Report {
    private int year;
    private int month;
    private double totalIncome;
    private double totalExpense;
    private boolean isBalancePositive;
    private double difference;
    private Currency mainCurrency;
}
