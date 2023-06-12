package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.CallBackQueryType;
import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.common.ExpenseCategoryType;
import com.lira17.expensetrackerbot.common.Step;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.ExpenseCategory;
import com.lira17.expensetrackerbot.request.ExpenseCategoryRequest;
import com.lira17.expensetrackerbot.service.ExpenseCategoryService;
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
import static com.lira17.expensetrackerbot.common.Message.EXPENSE;
import static com.lira17.expensetrackerbot.common.Message.INVALID_EXPENSE_CATEGORY_NAME_ERROR_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessageWithKeyboard;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.hasValidTextForStepHandler;

@Component
@Slf4j
public class AddExpenseCategoryHandler implements CommandMessageHandler, StepMessageHandler, CallBackQueryHandler {

    @Autowired
    ExpenseCategoryService expenseCategoryService;

    @Autowired
    UserStepService userStepService;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    ExpenseCategoryRequest expenseCategoryRequest;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);

        userStepService.startExpenseCategoryCreationJourney(chatId);

        return Collections.singletonList(generateSendMessage(chatId, CATEGORY_ADD_TITLE_MSG));
    }

    @Override
    public String getCommand() {
        return Command.ADD_EXPENSE_CATEGORY.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForUserStep(Update update) {
        String chatId = getChatIdAsString(update);
        String text = update.getMessage().getText();

        if (!hasValidTextForStepHandler(text)) {
            userStepService.clearUserSteps(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_EXPENSE_CATEGORY_NAME_ERROR_MSG);
        }

        expenseCategoryRequest.createRequest(text, chatId);

        int messageId = update.getMessage().getMessageId();

        SendMessage sendMessage = generateSendMessageWithKeyboard(chatId, CATEGORY_ADD_TYPE_MSG, messageId, CallBackQueryType.EXPENSE_CATEGORY_TYPE.getName(), ExpenseCategoryType.getAllNames());
        userStepService.moveUserToNextStep(chatId);

        return Collections.singletonList(sendMessage);
    }

    @Override
    public String getStep() {
        return Step.STARTED_ADDING_EXPENSE_CATEGORY.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForCallBackQuery(Update update) {
        String chatId = getChatIdAsString(update);

        expenseCategoryRequest.addCategoryType(update, chatId);
        var categoryRequest = expenseCategoryRequest.getRequest(chatId);

        ExpenseCategory createdCategory;

        try {
            createdCategory = expenseCategoryService.createCategory(categoryRequest);
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        String expenseCategoryMessageText = getExpenseCategoryMessageText(createdCategory);
        clearUserSession(chatId);
        return Collections.singletonList(generateSendMessage(chatId, expenseCategoryMessageText));
    }

    private String getExpenseCategoryMessageText(ExpenseCategory expenseCategory) {
        return String.format(CATEGORY_CREATED_MSG, EXPENSE, expenseCategory.getTitle(), expenseCategory.getType().getName());
    }

    private void clearUserSession(String chatId) {
        expenseCategoryRequest.clearRequest(chatId);
        userStepService.clearUserSteps(chatId);
    }

    @Override
    public String getCallBackQueryType() {
        return CallBackQueryType.EXPENSE_CATEGORY_TYPE.getName();
    }

}
