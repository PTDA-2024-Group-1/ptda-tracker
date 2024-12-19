package com.ptda.tracker.services;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.UserRepository;
import com.ptda.tracker.services.user.UserServiceHibernateImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceTest {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private UserServiceHibernateImpl userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceHibernateImpl(userRepository);
    }

    @Test
    void testRegister() {
        User registeredUser = userService.register("Test User", "test@example.com", "password");

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getName()).isEqualTo("Test User");
        assertThat(registeredUser.getEmail()).isEqualTo("test@example.com");
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void testLogin() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        Optional<User> loggedInUser = userService.login("test@example.com", "password");

        assertThat(loggedInUser).isPresent();
        assertThat(loggedInUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testGetById() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userService.getById(user.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testGetAllUsers() {
        User user1 = User.builder()
                .name("Test User 1")
                .email("test1@example.com")
                .password("password")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .name("Test User 2")
                .email("test2@example.com")
                .password("password")
                .build();
        userRepository.save(user2);

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(users.get(1).getEmail()).isEqualTo("test2@example.com");
    }

    @Test
    void testDeleteById() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        boolean isDeleted = userService.deleteById(user.getId());

        assertThat(isDeleted).isTrue();
        assertThat(userRepository.findById(user.getId())).isNotPresent();
    }
}