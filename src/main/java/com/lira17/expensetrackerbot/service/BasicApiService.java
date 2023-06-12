package com.lira17.expensetrackerbot.service;

import com.lira17.expensetrackerbot.exception.ExpenseTrackerApiException;
import com.lira17.expensetrackerbot.model.AuthenticationRequest;
import com.lira17.expensetrackerbot.model.JwtToken;
import com.lira17.expensetrackerbot.session.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
public class BasicApiService {
    private static final String AUTHENTICATE_API = "users/authenticate";

    @Value("${expense.tracker.api.user}")
    private String expenseTrackerApiUser;

    @Value("${expense.tracker.api.user}")
    private String expenseTrackerApiPassword;

    @Autowired
    SessionService sessionService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ApiUri apiUri;

    protected <T> ResponseEntity<T> sendAuthenticatedRequest(URI uri, HttpMethod httpMethod, Class<T> responseType, T requestBody) throws ExpenseTrackerApiException {

        if (!sessionService.isTokenExists()) {
            authenticate();
        }

        HttpEntity<T> request = new HttpEntity<>(requestBody, getAuthorizationHeader());
        ResponseEntity<T> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(uri, httpMethod, request, responseType);
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (isNotAuthorizedError(e)) {
                authenticate();
                sendAuthenticatedRequest(uri, httpMethod, responseType, requestBody);
            } else {
                throw new ExpenseTrackerApiException(uri.toString(), e.getMessage());
            }
        } catch (Exception e) {
            throw new ExpenseTrackerApiException(uri.toString(), e.getMessage());
        }

        return responseEntity;
    }

    private void authenticate() {
        URI uri = apiUri.getUriForApi(AUTHENTICATE_API);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(expenseTrackerApiUser, expenseTrackerApiPassword);

        HttpEntity<AuthenticationRequest> request = new HttpEntity<>(authenticationRequest);
        JwtToken jwtToken = restTemplate.postForObject(uri, request, JwtToken.class);

        sessionService.saveToken(jwtToken.jwtToken());
    }

    private HttpHeaders getAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sessionService.getToken());
        return headers;
    }

    private boolean isNotAuthorizedError(HttpClientErrorException exception) {
        return exception.getStatusCode().equals(HttpStatusCode.valueOf(401));
    }
}
