package com.lira17.expensetrackerbot.facade;

import com.lira17.expensetrackerbot.common.CallBackQueryType;
import com.lira17.expensetrackerbot.handler.CallBackQueryHandler;
import com.lira17.expensetrackerbot.handler.CommandMessageHandler;
import com.lira17.expensetrackerbot.handler.InfoHandler;
import com.lira17.expensetrackerbot.handler.StepMessageHandler;
import com.lira17.expensetrackerbot.session.UserStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;

@Component
public class ExpenseTrackerBotFacade {

    @Autowired
    InfoHandler infoHandler;

    @Autowired
    UserStepService userStepService;

    @Autowired
    @Qualifier("commandMessageHandlerMap")
    Map<String, CommandMessageHandler> commandMessageHandlerMap;

    @Autowired
    @Qualifier("stepMessageHandlerMap")
    Map<String, StepMessageHandler> stepMessageHandlerMap;

    @Autowired
    @Qualifier("callBackHandlerMap")
    Map<String, CallBackQueryHandler> callBackHandlerMap;


    public List<SendMessage> handleUpdate(Update update) {
        String chatId = getChatIdAsString(update);

        if (update.hasCallbackQuery() && userStepService.userHasStep(chatId)) {
            CallBackQueryType callBackQueryType = CallBackQueryType.getCallBackQueryType(update.getCallbackQuery());
            var callBackQueryHandler = callBackHandlerMap.get(callBackQueryType.getName());
            return callBackQueryHandler.getSendMessageForCallBackQuery(update);
        } else if (userStepService.userHasStep(chatId)) {
            var stepMessageHandler = stepMessageHandlerMap.get(userStepService.getUserStep(chatId));
            return stepMessageHandler.getSendMessageForUserStep(update);
        } else if (update.hasMessage()) {
            var commandMessageHandler = commandMessageHandlerMap.getOrDefault(update.getMessage().getText(), infoHandler);
            return commandMessageHandler.getSendMessage(update);
        } else {
            return Collections.emptyList();
        }
    }
}
