package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.TotalBalance;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class TotalBalanceService extends BasicApiService {

    private static final String TOTAL_BALANCE_API = "total-balance";

    public TotalBalance getTotalBalance() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriForApi(TOTAL_BALANCE_API);

        ResponseEntity<TotalBalance> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, TotalBalance.class, null);

        return responseEntity.getBody();
    }
}
