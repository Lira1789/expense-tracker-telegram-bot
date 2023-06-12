package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Income;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;

@Service
public class IncomeService extends BasicApiService {

    private static final String INCOMES_API = "incomes";

    public List<Income> getLastIncomes() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriWithQueryParamsForApi(INCOMES_API, getQueryParams());

        ResponseEntity<Income[]> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, Income[].class, null);

        return List.of(responseEntity.getBody());

    }

    public Income createIncome(Income income) throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(INCOMES_API);

        ResponseEntity<Income> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.POST, Income.class, income);

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
