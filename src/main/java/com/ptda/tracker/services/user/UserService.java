package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.Tier;
import com.ptda.tracker.models.user.User;

import java.util.Optional;

public interface UserService {

    User register(String name, String email, String password);

    boolean login(String email, String password);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    boolean changePassword(String email, String oldPassword, String newPassword);

    boolean updateTier(User user, Tier tier);

    boolean delete(User user);

}
