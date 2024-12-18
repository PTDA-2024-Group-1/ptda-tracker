package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void testInsertUser() {
        // Given
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setName("Test User");
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void testExistsByEmail() {
        String email = "test@rmail.com";
        assertFalse(userRepository.existsByEmail(email));
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setName("Test User");
        userRepository.save(user);
        assertTrue(userRepository.existsByEmail(email));
    }
}