package com.lira17.expensetrackerbot.configuration;

import com.lira17.expensetrackerbot.handler.AddExpenseCategoryHandler;
import com.lira17.expensetrackerbot.handler.AddExpenseHandler;
import com.lira17.expensetrackerbot.handler.AddIncomeCategoryHandler;
import com.lira17.expensetrackerbot.handler.AddIncomeHandler;
import com.lira17.expensetrackerbot.handler.CallBackQueryHandler;
import com.lira17.expensetrackerbot.handler.CommandMessageHandler;
import com.lira17.expensetrackerbot.handler.ExpensesLatestHandler;
import com.lira17.expensetrackerbot.handler.IncomesLatestHandler;
import com.lira17.expensetrackerbot.handler.InfoHandler;
import com.lira17.expensetrackerbot.handler.MonthlyReportHandler;
import com.lira17.expensetrackerbot.handler.StepMessageHandler;
import com.lira17.expensetrackerbot.handler.TotalBalanceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public Map<String, CommandMessageHandler> commandMessageHandlerMap(
            TotalBalanceHandler totalBalanceHandler,
            MonthlyReportHandler monthlyReportHandler,
            IncomesLatestHandler incomesLatestHandler,
            ExpensesLatestHandler expensesLatestHandler,
            AddExpenseCategoryHandler addExpenseCategoryHandler,
            InfoHandler infoHandler,
            AddIncomeCategoryHandler addIncomeCategoryHandler,
            AddExpenseHandler addExpenseHandler,
            AddIncomeHandler addIncomeHandler) {

        Map<String, CommandMessageHandler> map = new HashMap<>();
        map.put(totalBalanceHandler.getCommand(), totalBalanceHandler);
        map.put(monthlyReportHandler.getCommand(), monthlyReportHandler);
        map.put(incomesLatestHandler.getCommand(), incomesLatestHandler);
        map.put(expensesLatestHandler.getCommand(), expensesLatestHandler);
        map.put(addExpenseCategoryHandler.getCommand(), addExpenseCategoryHandler);
        map.put(infoHandler.getCommand(), infoHandler);
        map.put(addIncomeCategoryHandler.getCommand(), addIncomeCategoryHandler);
        map.put(addExpenseHandler.getCommand(), addExpenseHandler);
        map.put(addIncomeHandler.getCommand(), addIncomeHandler);
        return map;
    }

    @Bean
    public Map<String, StepMessageHandler> stepMessageHandlerMap(
            AddExpenseCategoryHandler addExpenseCategoryHandler,
            AddIncomeCategoryHandler addIncomeCategoryHandler,
            AddExpenseHandler addExpenseHandler,
            AddIncomeHandler addIncomeHandler) {

        Map<String, StepMessageHandler> map = new HashMap<>();
        map.put(addExpenseCategoryHandler.getStep(), addExpenseCategoryHandler);
        map.put(addIncomeCategoryHandler.getStep(), addIncomeCategoryHandler);
        map.put(addExpenseHandler.getStep(), addExpenseHandler);
        map.put(addExpenseHandler.getOneMoreStep(), addExpenseHandler);
        map.put(addIncomeHandler.getStep(), addIncomeHandler);
        map.put(addIncomeHandler.getOneMoreStep(), addIncomeHandler);
        return map;
    }

    @Bean
    public Map<String, CallBackQueryHandler> callBackHandlerMap(
            AddExpenseCategoryHandler addExpenseCategoryHandler,
            AddIncomeCategoryHandler addIncomeCategoryHandler,
            AddExpenseHandler addExpenseHandler,
            AddIncomeHandler addIncomeHandler) {

        Map<String, CallBackQueryHandler> map = new HashMap<>();
        map.put(addExpenseCategoryHandler.getCallBackQueryType(), addExpenseCategoryHandler);
        map.put(addIncomeCategoryHandler.getCallBackQueryType(), addIncomeCategoryHandler);
        map.put(addExpenseHandler.getCallBackQueryType(), addExpenseHandler);
        map.put(addExpenseHandler.getOneMoreCallBackQueryType(), addExpenseHandler);
        map.put(addIncomeHandler.getCallBackQueryType(), addIncomeHandler);
        map.put(addIncomeHandler.getOneMoreCallBackQueryType(), addIncomeHandler);
        return map;
    }
}
