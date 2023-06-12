package com.lira17.expensetrackerbot;

import com.lira17.expensetrackerbot.bot.ExpenseTrackerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ExpenseTrackerBotApplication implements CommandLineRunner {

    @Autowired
    ExpenseTrackerBot expenseTrackerBot;

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(expenseTrackerBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
