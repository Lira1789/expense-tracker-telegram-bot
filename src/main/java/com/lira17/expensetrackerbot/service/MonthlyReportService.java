package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.Report;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.time.LocalDate;

@Service
public class MonthlyReportService extends BasicApiService {

    private static final String REPORT_API = "report/month";

    public Report getReportForCurrentMonth() throws ExpenseTrackerApiException {
        URI uri = apiUri.getUriWithQueryParamsForApi(REPORT_API, getQueryParams());

        ResponseEntity<Report> responseEntity = sendAuthenticatedRequest(uri, HttpMethod.GET, Report.class, null);

        return responseEntity.getBody();
    }

    private MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("year", getCurrentYear());
        map.add("month", getCurrentMonth());
        return map;
    }

    private String getCurrentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    private String getCurrentMonth() {
        return String.valueOf(LocalDate.now().getMonth().getValue());
    }
}
