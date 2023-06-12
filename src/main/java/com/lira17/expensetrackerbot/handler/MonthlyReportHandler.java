package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Report;
import com.lira17.expensetrackerbot.service.MonthlyReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.REPORT_NEGATIVE_MSG;
import static com.lira17.expensetrackerbot.common.Message.REPORT_POSITIVE_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedAmountWithCurrency;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedDateFromMonthAndYear;

@Component
@Slf4j
public class MonthlyReportHandler implements CommandMessageHandler {

    @Autowired
    MonthlyReportService monthlyReportService;

    @Autowired
    ErrorHandler errorHandler;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);
        Report report;

        try {
            report = monthlyReportService.getReportForCurrentMonth();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        String reportMessageText = getReportMessageText(report);

        return Collections.singletonList(generateSendMessage(chatId, reportMessageText));
    }

    private String getReportMessageText(Report report) {
        String totalExpense = getFormattedAmountWithCurrency(report.getTotalExpense(), report.getMainCurrency());
        String totalIncome = getFormattedAmountWithCurrency(report.getTotalIncome(), report.getMainCurrency());
        String difference = getFormattedAmountWithCurrency(report.getDifference(), report.getMainCurrency());
        String date = getFormattedDateFromMonthAndYear(report.getMonth(), report.getYear());

        String messageTemplate = report.isBalancePositive()
                ? REPORT_POSITIVE_MSG
                : REPORT_NEGATIVE_MSG;

        return String.format(messageTemplate, date, totalExpense, totalIncome, difference);
    }

    @Override
    public String getCommand() {
        return Command.MONTH_BALANCE.getDescription();
    }
}
