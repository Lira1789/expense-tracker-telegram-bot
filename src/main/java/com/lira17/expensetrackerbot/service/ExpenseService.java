package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Expense;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;

@Service
public class ExpenseService extends BasicApiService {

    private static final String EXPENSE_API = "expenses";

    public List<Expense> getLastExpenses() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriWithQueryParamsForApi(EXPENSE_API, getQueryParams());

        ResponseEntity<Expense[]> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, Expense[].class, null);

        return List.of(responseEntity.getBody());

    }

    public Expense createExpense(Expense expense) throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(EXPENSE_API);

        ResponseEntity<Expense> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.POST, Expense.class, expense);

        return responseEntity.getBody();
    }

    private MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("page", "0");
        map.add("size", "20");
        map.add("sort", "date,desc");
        map.add("sort", "createdAt,desc");
        return map;
    }
}
