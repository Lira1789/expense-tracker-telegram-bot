package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.INFO_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getUserName;

@Component
public class InfoHandler implements CommandMessageHandler {

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);

        String infoMessageText = getInfoMessageText(update);

        return Collections.singletonList(generateSendMessage(chatId, infoMessageText));
    }

    private String getInfoMessageText(Update update) {
        String userName = getUserName(update);
        return String.format(INFO_MSG, userName);
    }

    @Override
    public String getCommand() {
        return Command.INFO.getDescription();
    }
}
