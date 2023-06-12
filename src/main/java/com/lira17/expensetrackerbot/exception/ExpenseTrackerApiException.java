package com.lira17.expensetrackerbot.exception;

public class ExpenseTrackerApiException extends RuntimeException {

    private static final String MESSAGE = " Exception during API request ";

    public ExpenseTrackerApiException() {
        super(MESSAGE);
    }

    public ExpenseTrackerApiException(String apiUrl, String exceptionMessage) {
        super(apiUrl + MESSAGE + exceptionMessage);
    }
}
