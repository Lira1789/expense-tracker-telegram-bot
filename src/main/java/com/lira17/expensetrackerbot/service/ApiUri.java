package com.lira17.expensetrackerbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ApiUri {
    @Value("${expense.tracker.api.url}")
    protected String expenseTrackerApiUrl;

    public URI getUriForApi(String api) {
        return UriComponentsBuilder
                .fromUriString(expenseTrackerApiUrl + api)
                .build()
                .toUri();
    }

    public URI getUriWithQueryParamsForApi(String api, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder
                .fromUriString(expenseTrackerApiUrl + api)
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
