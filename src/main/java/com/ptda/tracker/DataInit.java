package com.ptda.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.administration.AdminService;
import com.ptda.tracker.services.administration.RoleManagementService;
import com.ptda.tracker.services.assistance.AssistantService;
import com.ptda.tracker.services.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInit {
    private final UserService userService;
    private final AssistantService assistantService;
    private final AdminService adminService;
    private final RoleManagementService roleManagementService;

    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");

        // Optional<User> user = userService.getByEmail("ratmir.m2004@gmail.com");
        // roleManagementService.promoteUserToAssistant(user.get());

        /* if (tierLimitService.getAll().isEmpty()) {
            System.out.println("No daily limits found, creating default daily limits...");
            createDefaultDailyLimits();
        }
         */
    }

    private void createUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                    .name("User " + i)
                    .email("user" + System.currentTimeMillis() + "@gmail.com")
                    .password("password")
                    .build();
            users.add(user);
        }
        userService.create(users);
    }
}