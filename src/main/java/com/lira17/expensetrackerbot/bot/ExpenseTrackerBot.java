package com.lira17.expensetrackerbot.bot;

import com.lira17.expensetrackerbot.facade.ExpenseTrackerBotFacade;
import com.lira17.expensetrackerbot.handler.ErrorHandler;
import com.lira17.expensetrackerbot.session.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.NOT_AUTHORIZED_ERROR_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getUser;
import static com.lira17.expensetrackerbot.util.MessageUtil.getUserName;

@Component
@Slf4j
public class ExpenseTrackerBot extends TelegramLongPollingBot {

    @Value("${telegram.botName}")
    private String botUserName;

    @Value("${telegram.botToken}")
    private String botToken;

    @Autowired
    ExpenseTrackerBotFacade trackerBotFacade;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    UserService userService;


    @Override
    public void onUpdateReceived(Update update) {
        User user = getUser(update);
        String userName = getUserName(update);
        String chatId = getChatIdAsString(update);

        if (!userService.isAuthorizedUser(userName)) {
            log.warn("Non-authorized access " + userName);
            sendMessages(errorHandler.getCustomErrorMessageList(chatId, NOT_AUTHORIZED_ERROR_MSG));
        } else {
            userService.saveUSer(user);
            sendMessages(trackerBotFacade.handleUpdate(update));
        }
    }

    public void sendMessages(List<SendMessage> messages) {
        messages.forEach(this::executeAction);
    }

    private void executeAction(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
