package com.lira17.expensetrackerbot.util;

import com.lira17.expensetrackerbot.model.Category;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyBoardUtil {

    public static InlineKeyboardButton getKeyboardButton(String buttonText, String buttonType) {
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(buttonText);
        keyboardButton.setCallbackData(buttonType + ":" + buttonText);
        return keyboardButton;
    }

    public static List<List<InlineKeyboardButton>> getKeyboardRows(List<String> buttons, String buttonType) {
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        buttons.forEach(button -> {
            InlineKeyboardButton keyboardButton = getKeyboardButton(button, buttonType);
            keyboard.add(Collections.singletonList(keyboardButton));
        });

        return keyboard;
    }

    public static List<List<InlineKeyboardButton>> getKeyboardRowsWithIdForCategories(List<? extends Category> categories, String buttonType) {
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        categories.forEach(category -> {
            InlineKeyboardButton keyboardButton = getKeyboardButtonWithId(category.getTitle(), category.getId(), buttonType);
            keyboard.add(Collections.singletonList(keyboardButton));
        });

        return keyboard;
    }

    public static InlineKeyboardButton getKeyboardButtonWithId(String buttonText, long id, String buttonType) {
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(buttonText);
        keyboardButton.setCallbackData(buttonType + ":" + id);
        return keyboardButton;
    }
}
