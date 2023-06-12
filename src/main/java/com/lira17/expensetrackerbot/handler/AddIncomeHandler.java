package com.lira17.expensetrackerbot.handler;

import com.lira17.expensetrackerbot.common.CallBackQueryType;
import com.lira17.expensetrackerbot.common.Command;
import com.lira17.expensetrackerbot.common.Currency;
import com.lira17.expensetrackerbot.common.Step;
import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Income;
import com.lira17.expensetrackerbot.model.IncomeCategory;
import com.lira17.expensetrackerbot.request.IncomeRequest;
import com.lira17.expensetrackerbot.service.IncomeCategoryService;
import com.lira17.expensetrackerbot.service.IncomeService;
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
import static com.lira17.expensetrackerbot.common.Message.INCOME;
import static com.lira17.expensetrackerbot.common.Message.INCOME_ADD_AMOUNT_MSG;
import static com.lira17.expensetrackerbot.common.Message.INCOME_ADD_TITLE_DESCRIPTION_DATE_MSG;
import static com.lira17.expensetrackerbot.common.Message.INCOME_SELECT_CATEGORY_MSG;
import static com.lira17.expensetrackerbot.common.Message.INCOME_SELECT_CURRENCY_MSG;
import static com.lira17.expensetrackerbot.common.Message.INVALID_INCOME_AMOUNT_ERROR_MSG;
import static com.lira17.expensetrackerbot.common.Message.INVALID_INCOME_DATE_TITLE_DESC_ERROR_MSG;
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
public class AddIncomeHandler implements CommandMessageHandler, StepMessageHandler, CallBackQueryHandler {
    @Autowired
    IncomeService incomeService;

    @Autowired
    IncomeCategoryService incomeCategoryService;

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    UserService userService;

    @Autowired
    UserStepService userStepService;

    @Autowired
    IncomeRequest incomeRequest;

    @Override
    public List<SendMessage> getSendMessage(Update update) {
        String chatId = getChatIdAsString(update);

        userStepService.startIncomeCreationJourney(chatId);

        return Collections.singletonList(generateSendMessage(chatId, INCOME_ADD_TITLE_DESCRIPTION_DATE_MSG));
    }

    @Override
    public String getCommand() {
        return Command.ADD_INCOME.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForUserStep(Update update) {
        String chatId = getChatIdAsString(update);
        String text = update.getMessage().getText();

        if (!hasValidTextForStepHandler(text)) {
            clearUserSession(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_INCOME_DATE_TITLE_DESC_ERROR_MSG);
        }

        if (!userStepService.isUserStartedIncomeCreation(chatId) && !hasValidAmount(text)) {
            clearUserSession(chatId);
            return errorHandler.getCustomErrorMessageList(chatId, INVALID_INCOME_AMOUNT_ERROR_MSG);
        }

        SendMessage sendMessage = userStepService.isUserStartedIncomeCreation(chatId)
                ? getMessageForIncomeCreationStep(update, chatId)
                : getMessageForAmountStep(update, chatId);

        return Collections.singletonList(sendMessage);
    }

    private SendMessage getMessageForIncomeCreationStep(Update update, String chatId) {
        incomeRequest.createRequest(update, chatId);
        userStepService.moveUserToNextStep(chatId);
        return generateSendMessage(chatId, INCOME_ADD_AMOUNT_MSG);
    }

    private SendMessage getMessageForAmountStep(Update update, String chatId) {
        incomeRequest.addAmountToRequest(update, chatId);
        int messageId = update.getMessage().getMessageId();
        return generateSendMessageWithKeyboard(chatId, INCOME_SELECT_CURRENCY_MSG, messageId, CallBackQueryType.INCOME_CURRENCY.getName(), Currency.getAllNames());
    }

    @Override
    public String getStep() {
        return Step.STARTED_ADDING_INCOME.getDescription();
    }

    public String getOneMoreStep() {
        return Step.ADDED_INCOME_TITLE_DESC_DATE.getDescription();
    }

    @Override
    public List<SendMessage> getSendMessageForCallBackQuery(Update update) {
        String chatId = getChatIdAsString(update);
        CallBackQueryType callBackQueryType = CallBackQueryType.getCallBackQueryType(update.getCallbackQuery());

        return callBackQueryType.equals(CallBackQueryType.INCOME_CURRENCY)
                ? getMessageForIncomeCurrencyFallback(update, chatId)
                : getMessageForCategoryFallback(update, chatId);
    }

    private List<SendMessage> getMessageForIncomeCurrencyFallback(Update update, String chatId) {
        incomeRequest.addCurrency(update, chatId);

        List<IncomeCategory> incomeCategories;

        try {
            incomeCategories = incomeCategoryService.getAllIncomesCategories();
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        var sendMessage = generateSendMessageWithCategoriesKeyboard(chatId, INCOME_SELECT_CATEGORY_MSG, messageId, CallBackQueryType.INCOME_CATEGORY.getName(), incomeCategories);

        return Collections.singletonList(sendMessage);
    }

    private List<SendMessage> getMessageForCategoryFallback(Update update, String chatId) {
        incomeRequest.addCategory(update, chatId);
        var incomeRequest = this.incomeRequest.getRequest(chatId);
        Income createdIncome;

        try {
            createdIncome = incomeService.createIncome(incomeRequest);
        } catch (ExpenseTrackerApiException e) {
            log.error(e.getMessage());
            clearUserSession(chatId);
            return errorHandler.getGenericErrorMessageList(chatId);
        }

        var sendMessage = generateSendMessage(chatId, getIncomeMessageText((createdIncome)));
        var sendMessageForOtherUser = getSendMessageForOtherUser(update, createdIncome);

        clearUserSession(chatId);

        return List.of(sendMessage, sendMessageForOtherUser);
    }

    private String getIncomeMessageText(Income income) {
        String amount = getFormattedAmountWithCurrency(income.getAmount(), income.getCurrency());
        String date = getFormattedDateFromLocalDate(income.getDate());

        return String.format(BALANCE_ENTITY_CREATED_MSG, INCOME, income.getTitle(), income.getDescription(), amount, date, income.getCategory().getTitle());
    }

    private SendMessage getSendMessageForOtherUser(Update update, Income createdIncome) {
        String userName = getUserName(update);
        User userPair = userService.getUserPair(userName);
        return generateSendMessage(String.valueOf(userPair.getId()), getIncomeMessageTextForOtherUser(createdIncome, userName, userPair.getUserName()));
    }

    private String getIncomeMessageTextForOtherUser(Income income, String username, String partnerUserName) {
        String amount = getFormattedAmountWithCurrency(income.getAmount(), income.getCurrency());
        String date = getFormattedDateFromLocalDate(income.getDate());

        return String.format(BALANCE_ENTITY_CREATED_NOTIFICATION_MSG, partnerUserName, username, INCOME, income.getTitle(), income.getDescription(), amount, date, income.getCategory().getTitle());
    }

    private void clearUserSession(String chatId) {
        incomeRequest.clearRequest(chatId);
        userStepService.clearUserSteps(chatId);
    }

    @Override
    public String getCallBackQueryType() {
        return CallBackQueryType.INCOME_CURRENCY.getName();
    }

    public String getOneMoreCallBackQueryType() {
        return CallBackQueryType.INCOME_CATEGORY.getName();
    }
}
