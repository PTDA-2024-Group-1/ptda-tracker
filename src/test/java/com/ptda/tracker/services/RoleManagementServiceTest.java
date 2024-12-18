package com.ptda.tracker.services;

import com.ptda.tracker.models.admin.Admin;
import com.ptda.tracker.models.assistance.Assistant;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.UserRepository;
import com.ptda.tracker.services.administration.RoleManagementServiceHibernateImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleManagementServiceTest {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private RoleManagementServiceHibernateImpl roleManagementService;

    @BeforeEach
    void setUp() {
        roleManagementService = new RoleManagementServiceHibernateImpl(entityManager);
    }

//    @Test
//    void testPromoteUserToAdmin() {
//        User user = new User();
//        user.setName("Test User");
//        user.setEmail("user@example.com");
//        user.setPassword("password");
//        user.setUserType("USER");
//        userRepository.save(user);
//
//        Admin promotedAdmin = roleManagementService.promoteUserToAdmin(user);
//
//        assertThat(promotedAdmin).isNotNull();
//        assertThat(promotedAdmin.getEmail()).isEqualTo("user@example.com");
//    }


    @Test
    void testPromoteUserToAdminAlreadyAdmin() {
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        userRepository.save(admin);

        assertThrows(IllegalArgumentException.class, () -> roleManagementService.promoteUserToAdmin(admin));
    }

    @Test
    void testDemoteAdminToUser() {
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        userRepository.save(admin);

        User demotedUser = roleManagementService.demoteAdminToUser(admin);

        assertThat(demotedUser).isNotNull();
        assertThat(demotedUser.getEmail()).isEqualTo("admin@example.com");
    }

//    @Test
//    void testPromoteAssistantToAdmin() {
//        Assistant assistant = new Assistant();
//        assistant.setName("Test Assistant");
//        assistant.setEmail("assistant@example.com");
//        assistant.setPassword("password");
//        assistant.setUserType("ASSISTANT");
//        userRepository.save(assistant);
//
//        Admin promotedAdmin = roleManagementService.promoteAssistant(assistant);
//
//        assertThat(promotedAdmin).isNotNull();
//        assertThat(promotedAdmin.getEmail()).isEqualTo("assistant@example.com");
//    }


    @Test
    void testDemoteAdminToAssistant() {
        Admin admin = new Admin();
        admin.setName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        userRepository.save(admin);

        Assistant demotedAssistant = roleManagementService.demoteAdminToAssistant(admin);

        assertThat(demotedAssistant).isNotNull();
        assertThat(demotedAssistant.getEmail()).isEqualTo("admin@example.com");
    }

    @Test
    void testDemoteAssistantToUser() {
        Assistant assistant = new Assistant();
        assistant.setName("Test Assistant");
        assistant.setEmail("assistant@example.com");
        assistant.setPassword("password");
        userRepository.save(assistant);

        User demotedUser = roleManagementService.demoteAssistant(assistant);

        assertThat(demotedUser).isNotNull();
        assertThat(demotedUser.getEmail()).isEqualTo("assistant@example.com");
    }
}