package com.lira17.expensetrackerbot.util;

import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.common.Currency;
import com.lira17.expensetrackerbot.model.Category;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lira17.expensetrackerbot.util.KeyBoardUtil.getKeyboardRows;
import static com.lira17.expensetrackerbot.util.KeyBoardUtil.getKeyboardRowsWithIdForCategories;

public class MessageUtil {
    private static final String DASH = "-";
    private static final String EMPTY_STRING = "";

    private static final String DATE_PATTERN = "\\b(0?[1-9]|[12]\\d|3[01])/(0?[1-9]|1[0-2])/\\d{4}\\b";

    public static SendMessage generateSendMessage(String chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public static SendMessage generateSendMessageWithKeyboard(String chatId, String messageText, int messageId, String buttonType, List<String> buttonNames) {
        SendMessage sendMessage = generateSendMessage(chatId, messageText);

        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(getKeyboardRows(buttonNames, buttonType));

        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public static SendMessage generateSendMessageWithCategoriesKeyboard(String chatId, String messageText, int messageId, String buttonType, List<? extends Category> categories) {
        SendMessage sendMessage = generateSendMessage(chatId, messageText);

        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(getKeyboardRowsWithIdForCategories(categories, buttonType));

        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public static String getFormattedAmountWithCurrency(double amount, Currency currency) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(amount) + " " + currency;
    }

    public static String getFormattedDateFromMonthAndYear(int month, int year) {
        return Month.of(month) + " " + Year.of(year);
    }

    public static String getFormattedDateFromLocalDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

        return localDate.format(formatter);
    }

    public static String getChatIdAsString(Update update) {
        return update.hasCallbackQuery()
                ? String.valueOf(update.getCallbackQuery().getMessage().getChatId())
                : String.valueOf(update.getMessage().getChatId());
    }

    public static String getUserName(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getUserName()
                : update.getMessage().getFrom().getUserName();
    }

    public static User getUser(Update update) {
        return update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom()
                : update.getMessage().getFrom();
    }

    public static LocalDate getDateFromMessage(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String text = message.getText();
        LocalDate localDate = LocalDate.now();
        if (text.contains(DASH) && hasDate(text)) {
            String[] array = text.split(DASH);
            localDate = LocalDate.parse(array[0].trim(), formatter);
        }
        return localDate;
    }

    public static String getExpenseTitleFromMessage(Message message) {
        String text = message.getText();
        String[] array = text.split(DASH);
        String title = EMPTY_STRING;
        boolean hasDate = hasDate(text);

        if (hasDate && array.length > 1) {
            title = array[1];
        }
        if (!hasDate && array.length >= 1) {
            title = array[0];
        }

        return title;
    }

    public static String getExpenseDescriptionFromMessage(Message message) {
        String text = message.getText();
        String[] array = text.split(DASH);
        String description = EMPTY_STRING;
        boolean hasDate = hasDate(text);

        if (hasDate && array.length > 2) {
            description = array[2];
        }

        if (!hasDate && array.length == 2) {
            description = array[1];
        }

        return description;
    }

    public static String getTextFromCallBackQuery(CallbackQuery callBackQuery) {
        String[] data = callBackQuery.getData().split(":");
        return data[1];
    }

    public static boolean hasDate(String text) {
        Pattern datePattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = datePattern.matcher(text);
        return matcher.find();
    }

    public static boolean hasValidTextForStepHandler(String text) {
        boolean hasValidCategoryTitle = false;
        if (!text.isEmpty()) {
            hasValidCategoryTitle = Command.getAllNames().stream().noneMatch(text::equals);
        }
        return hasValidCategoryTitle;
    }

    public static boolean hasValidAmount(String text) {
        return isInteger(text) || isDouble(text);
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
