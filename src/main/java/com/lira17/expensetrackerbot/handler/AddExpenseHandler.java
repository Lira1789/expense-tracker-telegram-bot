package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.CallBackQueryType;
import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.common.Currency;
import com.lira17.expensetrackerbot.common.Step;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Expense;
import com.lira17.expensetrackerbot.model.ExpenseCategory;
import com.lira17.expensetrackerbot.request.ExpenseRequest;
import com.lira17.expensetrackerbot.service.ExpenseCategoryService;
import com.lira17.expensetrackerbot.service.ExpenseService;
import com.lira17.expensetrackerbot.session.UserService;
import com.lira17.expensetrackerbot.session.UserStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Collections;
import java.util.List;

import static com.lira17.expensetrackerbot.common.Message.BALANCE_ENTITY_CREATED_MSG;
import static com.lira17.expensetrackerbot.common.Message.BALANCE_ENTITY_CREATED_NOTIFICATION_MSG;
import static com.lira17.expensetrackerbot.common.Message.EXPENSE;
import static com.lira17.expensetrackerbot.common.Message.EXPENSE_ADD_AMOUNT_MSG;
import static com.lira17.expensetrackerbot.common.Message.EXPENSE_ADD_TITLE_DESCRIPTION_DATE_MSG;
import static com.lira17.expensetrackerbot.common.Message.EXPENSE_SELECT_CATEGORY_MSG;
import static com.lira17.expensetrackerbot.common.Message.EXPENSE_SELECT_CURRENCY_MSG;
import static com.lira17.expensetrackerbot.common.Message.INVALID_EXPENSE_AMOUNT_ERROR_MSG;
import static com.lira17.expensetrackerbot.common.Message.INVALID_EXPENSE_DATE_TITLE_DESC_ERROR_MSG;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessage;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessageWithCategoriesKeyboard;
import static com.lira17.expensetrackerbot.util.MessageUtil.generateSendMessageWithKeyboard;
import static com.lira17.expensetrackerbot.util.MessageUtil.getChatIdAsString;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedAmountWithCurrency;
import static com.lira17.expensetrackerbot.util.MessageUtil.getFormattedDateFromLocalDate;
import static com.lira17.expensetrackerbot.util.MessageUtil.getUserName;
import static com.lira17.expensetrackerbot.util.MessageUtil.hasValidAmount;
import static com.lira17.expensetrackerbot.util.MessageUtil.hasValidTextForStepHandler;

@Component
@Slf4j
public class AddExpenseHandler implements CommandMessageHandler, StepMessageHandler, CallBackQueryHandler {

    @Autowired
    ExpenseService expenseService;

    @Autowired
    ExpenseCategoryService expenseCategoryService;

    @Autowired
    UserStepService userStepService;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    UserService userService;

    @Autowired
    ExpenseRequest expenseRequest;

    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);

        userStepService.startExpenseCreationJourney(chatId);

        return Collections.singletonList(generateSendMessage(chatId, EXPENSE_ADD_TITLE_DESCRIPTION_DATE_MSG));
    }

    @Override
    public String getCommand() {
        return Command.ADD_EXPENSE.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForUserStep(Update update) {
        String chatId = getChatIdAsString(update);
        String text = update.getMessage().getText();

        if (!hasValidTextForStepHandler(text)) {
            clearUserSession(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_EXPENSE_DATE_TITLE_DESC_ERROR_MSG);
        }

        if (!userStepService.isUserStartedExpenseCreation(chatId) && !hasValidAmount(text)) {
            clearUserSession(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_EXPENSE_AMOUNT_ERROR_MSG);
        }

        SendMessage sendMessage = userStepService.isUserStartedExpenseCreation(chatId)
                ? getMessageForExpenseCreationStep(update, chatId)
                : getMessageForAmountStep(update, chatId);

        return Collections.singletonList(sendMessage);
    }

    private SendMessage getMessageForExpenseCreationStep(Update update, String chatId) {
        expenseRequest.createRequest(update, chatId);
        userStepService.moveUserToNextStep(chatId);
        return generateSendMessage(chatId, EXPENSE_ADD_AMOUNT_MSG);
    }

    private SendMessage getMessageForAmountStep(Update update, String chatId) {
        expenseRequest.addAmountToRequest(update, chatId);
        int messageId = update.getMessage().getMessageId();
        return generateSendMessageWithKeyboard(chatId, EXPENSE_SELECT_CURRENCY_MSG, messageId, CallBackQueryType.EXPENSE_CURRENCY.getName(), Currency.getAllNames());
    }

    @Override
    public String getStep() {
        return Step.STARTED_ADDING_EXPENSE.getDescription();
    }

    public String getOneMoreStep() {
        return Step.ADDED_EXPENSE_TITLE_DESC_DATE.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForCallBackQuery(Update update) {
        String chatId = getChatIdAsString(update);
        CallBackQueryType callBackQueryType = CallBackQueryType.getCallBackQueryType(update.getCallbackQuery());

        return callBackQueryType.equals(CallBackQueryType.EXPENSE_CURRENCY)
                ? getMessageForExpenseCurrencyCallback(update, chatId)
                : getMessageForCategoryCallback(update, chatId);
    }

    private List<SendMessage> getMessageForExpenseCurrencyCallback(Update update, String chatId) {
        expenseRequest.addCurrency(update, chatId);

        List<ExpenseCategory> expenseCategories;

        try {
            expenseCategories = expenseCategoryService.getAllExpenseCategories();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        SendMessage sendMessage =
                generateSendMessageWithCategoriesKeyboard(chatId, EXPENSE_SELECT_CATEGORY_MSG, messageId, CallBackQueryType.EXPENSE_CATEGORY.getName(), expenseCategories);

        return Collections.singletonList(sendMessage);
    }

    private List<SendMessage> getMessageForCategoryCallback(Update update, String chatId) {
        expenseRequest.addCategory(update, chatId);
        Expense expense = expenseRequest.getRequest(chatId);

        Expense createdExpense;

        try {
            createdExpense = expenseService.createExpense(expense);
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        SendMessage sendMessage = generateSendMessage(chatId, getExpenseMessageText(createdExpense));
        SendMessage sendMessageForOtherUser = getSendMessageForOtherUser(update, createdExpense);

        clearUserSession(chatId);
        return List.of(sendMessage, sendMessageForOtherUser);
    }


    private String getExpenseMessageText(Expense expense) {
        String amount = getFormattedAmountWithCurrency(expense.getAmount(), expense.getCurrency());
        String date = getFormattedDateFromLocalDate(expense.getDate());

        return String.format(BALANCE_ENTITY_CREATED_MSG, EXPENSE, expense.getTitle(), expense.getDescription(), amount, date, expense.getCategory().getTitle());
    }

    private SendMessage getSendMessageForOtherUser(Update update, Expense createdExpense) {
        String userName = getUserName(update);
        User userPair = userService.getUserPair(userName);
        return generateSendMessage(String.valueOf(userPair.getId()), getExpenseMessageTextForOtherUser(createdExpense, userName, userPair.getUserName()));
    }

    private String getExpenseMessageTextForOtherUser(Expense expense, String username, String partnerUserName) {
        String amount = getFormattedAmountWithCurrency(expense.getAmount(), expense.getCurrency());
        String date = getFormattedDateFromLocalDate(expense.getDate());

        return String.format(BALANCE_ENTITY_CREATED_NOTIFICATION_MSG, partnerUserName, username, EXPENSE, expense.getTitle(), expense.getDescription(), amount, date, expense.getCategory().getTitle());
    }

    private void clearUserSession(String chatId) {
        expenseRequest.clearRequest(chatId);
        userStepService.clearUserSteps(chatId);
    }

    @Override
    public String getCallBackQueryType() {
        return CallBackQueryType.EXPENSE_CURRENCY.getName();
    }

    public String getOneMoreCallBackQueryType() {
        return CallBackQueryType.EXPENSE_CATEGORY.getName();
    }
}
