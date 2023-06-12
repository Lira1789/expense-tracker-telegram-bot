package com.lira17.expensetrackerbot.session;

import com.lira17.expensetrackerbot.common.Step;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lira17.expensetrackerbot.common.Step.ADDED_EXPENSE_CATEGORY_TITLE;
import static com.lira17.expensetrackerbot.common.Step.ADDED_EXPENSE_TITLE_DESC_DATE;
import static com.lira17.expensetrackerbot.common.Step.ADDED_INCOME_CATEGORY_TITLE;
import static com.lira17.expensetrackerbot.common.Step.ADDED_INCOME_TITLE_DESC_DATE;
import static com.lira17.expensetrackerbot.common.Step.STARTED_ADDING_EXPENSE;
import static com.lira17.expensetrackerbot.common.Step.STARTED_ADDING_EXPENSE_CATEGORY;
import static com.lira17.expensetrackerbot.common.Step.STARTED_ADDING_INCOME;
import static com.lira17.expensetrackerbot.common.Step.STARTED_ADDING_INCOME_CATEGORY;

@Service
public class UserStepService {
    private final Map<String, String> userStepMap = new ConcurrentHashMap<>();

    private final Map<Step, Step> nextStepMap = new EnumMap<>(Step.class);

    public UserStepService() {
        nextStepMap.put(STARTED_ADDING_EXPENSE, ADDED_EXPENSE_TITLE_DESC_DATE);
        nextStepMap.put(STARTED_ADDING_INCOME, ADDED_INCOME_TITLE_DESC_DATE);
        nextStepMap.put(STARTED_ADDING_EXPENSE_CATEGORY, ADDED_EXPENSE_CATEGORY_TITLE);
        nextStepMap.put(STARTED_ADDING_INCOME_CATEGORY, ADDED_INCOME_CATEGORY_TITLE);
    }

    public void moveUserToNextStep(String chatId) {
        Step currentStep = Step.getByDescription(getUserStep(chatId)).orElseThrow();
        Step nextStep = nextStepMap.get(currentStep);
        addUserStep(chatId, nextStep.getDescription());
    }

    private void addUserStep(String chatId, String step) {
        userStepMap.put(chatId, step);
    }

    public void startExpenseCategoryCreationJourney(String chatId) {
        addUserStep(chatId, STARTED_ADDING_EXPENSE_CATEGORY.getDescription());
    }

    public void startIncomeCategoryCreationJourney(String chatId) {
        addUserStep(chatId, Step.STARTED_ADDING_INCOME_CATEGORY.getDescription());
    }

    public void startIncomeCreationJourney(String chatId) {
        addUserStep(chatId, Step.STARTED_ADDING_INCOME.getDescription());
    }

    public void startExpenseCreationJourney(String chatId) {
        addUserStep(chatId, STARTED_ADDING_EXPENSE.getDescription());
    }

    public boolean isUserStartedExpenseCreation(String chatId) {
        return STARTED_ADDING_EXPENSE.getDescription().equals(getUserStep(chatId));
    }

    public boolean isUserStartedIncomeCreation(String chatId) {
        return Step.STARTED_ADDING_INCOME.getDescription().equals(getUserStep(chatId));
    }

    public void clearUserSteps(String chatId) {
        userStepMap.remove(chatId);
    }

    public boolean userHasStep(String chatId) {
        return userStepMap.containsKey(chatId);
    }

    public String getUserStep(String chatId) {
        return userStepMap.get(chatId);
    }
}
