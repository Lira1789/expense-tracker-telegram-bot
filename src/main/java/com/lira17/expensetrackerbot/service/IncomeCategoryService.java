package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.IncomeCategory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class IncomeCategoryService extends BasicApiService {

    private static final String INCOME_CATEGORIES_API = "income-categories";

    public List<IncomeCategory> getAllIncomesCategories() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(INCOME_CATEGORIES_API);

        ResponseEntity<IncomeCategory[]> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, IncomeCategory[].class, null);

        return List.of(responseEntity.getBody());

    }

    public IncomeCategory createCategory(IncomeCategory incomeCategory) throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(INCOME_CATEGORIES_API);

        ResponseEntity<IncomeCategory> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.POST, IncomeCategory.class, incomeCategory);

        return responseEntity.getBody();
    }
}
