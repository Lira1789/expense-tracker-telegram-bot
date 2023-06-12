package com.lira17.expensetrackerbot.model;


import com.lira17.expensetrackerbot.common.Currency;

public record TotalBalance(double balanceAmount, Currency mainCurrency, boolean isBalancePositive) {
}
