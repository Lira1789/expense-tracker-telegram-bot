package com.lira17.expensetrackerbot.session;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SessionService {
    private static final String TOKEN_KEY = "token";
    private final Map<String, String> sessionMap = new HashMap<>();

    public void saveToken(String jwtToken) {
        sessionMap.put(TOKEN_KEY, jwtToken);
    }

    public String getToken() {
        return sessionMap.get(TOKEN_KEY);
    }

    public boolean isTokenExists() {
        return sessionMap.containsKey(TOKEN_KEY);
    }
}
