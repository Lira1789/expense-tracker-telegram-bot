package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.CallBackQueryType;
import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.common.IncomeCategoryType;
import com.lira17.expensetrackerbot.common.Step;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.IncomeCategory;
import com.lira17.expensetrackerbot.request.IncomeCategoryRequest;
import com.lira17.expensetrackerbot.service.IncomeCategoryService;
import com.lira17.expensetrackerbot.session.UserStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.CATEGORY_ADD_TITLE_MSG;
import static com.lira17.expensetrackerbot.common.Message.CATEGORY_ADD_TYPE_MSG;
import static com.lira17.expensetrackerbot.common.Message.CATEGORY_CREATED_MSG;
import static com.lira17.expensetrackerbot.common.Message.INCOME;
import static com.lira17.expensetrackerbot.common.Message.INVALID_INCOME_CATEGORY_NAME_ERROR_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessageWithKeyboard;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.hasValidTextForStepHandler;

@Component
@Slf4j
public class AddIncomeCategoryHandler implements CommandMessageHandler, StepMessageHandler, CallBackQueryHandler {

    @Autowired
    IncomeCategoryService incomeCategoryService;

    @Autowired
    UserStepService userStepService;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    IncomeCategoryRequest incomeCategoryRequest;

    @Override
    public List<SendMessage> getSendMessage(Update update) {

        String chatId = getChatIdAsString(update);

        SendMessage sendMessage = generateSendMessage(chatId, CATEGORY_ADD_TITLE_MSG);
        userStepService.startIncomeCategoryCreationJourney(chatId);

        return Collections.singletonList(sendMessage);
    }

    @Override
    public String getCommand() {
        return Command.ADD_INCOME_CATEGORY.getDescription();
    }


    @Override
    public List<SendMessage> getSendMessageForUserStep(Update update) {
        String chatId = getChatIdAsString(update);
        String text = update.getMessage().getText();

        if (!hasValidTextForStepHandler(text)) {
            userStepService.clearUserSteps(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_INCOME_CATEGORY_NAME_ERROR_MSG);
        }

        incomeCategoryRequest.createRequest(text, chatId);

        int messageId = update.getMessage().getMessageId();

        SendMessage sendMessage = generateSendMessageWithKeyboard(chatId, CATEGORY_ADD_TYPE_MSG, messageId, CallBackQueryType.INCOME_CATEGORY_TYPE.getName(), IncomeCategoryType.getAllNames());
        userStepService.moveUserToNextStep(chatId);

        return Collections.singletonList(sendMessage);
    }

    @Override
    public String getStep() {
        return Step.STARTED_ADDING_INCOME_CATEGORY.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForCallBackQuery(Update update) {
        String chatId = getChatIdAsString(update);

        incomeCategoryRequest.addCategoryType(update, chatId);
        var categoryRequest = incomeCategoryRequest.getRequest(chatId);
        IncomeCategory createdCategory;

        try {
            createdCategory = incomeCategoryService.createCategory(categoryRequest);
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        clearUserSession(chatId);

        String incomeCategoryMessageText = getIncomeCategoryMessageText(createdCategory);

        return Collections.singletonList(generateSendMessage(chatId, incomeCategoryMessageText));
    }

    private String getIncomeCategoryMessageText(IncomeCategory incomeCategory) {
        return String.format(CATEGORY_CREATED_MSG, INCOME, incomeCategory.getTitle(), incomeCategory.getType().getName());
    }

    private void clearUserSession(String chatId) {
        incomeCategoryRequest.clearRequest(chatId);
        userStepService.clearUserSteps(chatId);
    }

    @Override
    public String getCallBackQueryType() {
        return CallBackQueryType.INCOME_CATEGORY_TYPE.getName();
    }
}
