package com.ptda.tracker.util;

import com.ptda.tracker.models.user.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSession {

    private static UserSession instance;
    private User user;

    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void clear() {
        user = null;
    }
}