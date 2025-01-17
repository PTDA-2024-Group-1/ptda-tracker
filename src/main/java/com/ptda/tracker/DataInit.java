package com.ptda.tracker;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.administration.AdminService;
import com.ptda.tracker.services.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit {

    private final AdminService adminService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");
        if (adminService.getAll().isEmpty()) {
            System.out.println("Creating admin...");
            createAdmin();
        }
    }

    private void createAdmin() {
        Admin admin = new Admin();
        admin.setName("admin");
        admin.setEmail("admin@divi.pt");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
        admin.setUserType("ADMIN");
        adminService.create(admin);

        userService.register("system", "system@divi.pt", "system");
    }
}