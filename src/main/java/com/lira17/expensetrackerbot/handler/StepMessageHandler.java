package com.lira17.expensetrackerbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface StepMessageHandler {

    List<SendMessage> getSendMessageForUserStep(Update update);

    String getStep();
}
