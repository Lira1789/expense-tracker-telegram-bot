package com.lira17.expensetrackerbot.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserService {

    private static final Map<String, User> USERS = new HashMap<>();

    @Value("${user1.username}")
    private String userOneUserName;

    @Value("${user2.username}")
    private String userTwoUserName;

    public boolean isAuthorizedUser(String userName) {
        return userOneUserName.equals(userName) || userTwoUserName.equals(userName);
    }

    public void saveUSer(User user) {
        String userName = user.getUserName();
        if (!USERS.containsKey(userName)) {
            USERS.put(userName, user);
        }
    }

    public User getUserPair(String userName) {
        if (userOneUserName.equals(userName)) {
            return USERS.getOrDefault(userTwoUserName, new User());
        }
        return USERS.getOrDefault(userOneUserName, new User());
    }
}
