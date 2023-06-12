package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.ExpenseCategory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class ExpenseCategoryService extends BasicApiService {

    private static final String EXPENSE_CATEGORIES_API = "expense-categories";

    public List<ExpenseCategory> getAllExpenseCategories() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(EXPENSE_CATEGORIES_API);

        ResponseEntity<ExpenseCategory[]> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, ExpenseCategory[].class, null);

        return List.of(responseEntity.getBody());

    }

    public ExpenseCategory createCategory(ExpenseCategory expenseCategory) throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(EXPENSE_CATEGORIES_API);

        ResponseEntity<ExpenseCategory> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.POST, ExpenseCategory.class, expenseCategory);

        return responseEntity.getBody();
    }
}
