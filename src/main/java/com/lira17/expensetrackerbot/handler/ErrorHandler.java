package com.lira17.expensetrackerbot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.GENERIC_ERROR_MSG;

@Component
public class ErrorHandler {

    public List<SendMessage> getCustomErrorMessageList(String chaiId, String message) {
        return Collections.singletonList(new SendMessage(chaiId, message));
    }

    public List<SendMessage> getGenericErrorMessageList(String chaiId) {
        return Collections.singletonList(new SendMessage(chaiId, GENERIC_ERROR_MSG));
    }
}
