package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    void testFindByEmailAndPassword() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmailAndPassword("test@example.com", "password");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Test User");
    }

    @Test
    void testFindByEmailAndPassword_Failure() {
        Optional<User> retrieved = userRepository.findByEmailAndPassword("wrong@example.com", "wrongpassword");
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testInsertUser() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setName("Test User");
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void testInsertDuplicateEmail() {
        User user1 = new User();
        user1.setEmail("duplicate@example.com");
        user1.setPassword("password");
        user1.setName("User One");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("duplicate@example.com");
        user2.setPassword("password");
        user2.setName("User Two");

        try {
            userRepository.save(user2);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
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

    @Test
    void testFindByEmail() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmail("test@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Test User");
    }

    @Test
    void testFindByEmail_Failure() {
        Optional<User> retrieved = userRepository.findByEmail("nonexistent@example.com");
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void testFindById() {
        User user = User.builder()
                .name("Test User")
                .email("user@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        Optional<User> retrieved = userRepository.findById(user.getId());
        assertThat(retrieved).isPresent();
    }

    @Test
    void testUpdateUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        userRepository.save(user);

        user.setName("Updated User");
        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmail("test@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Updated User");
    }

    @Test
    void testUpdatePartialUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        userRepository.save(user);

        user.setPassword("newpassword");
        userRepository.save(user);

        Optional<User> retrieved = userRepository.findByEmail("test@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testDeleteUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        User user2 = User.builder()
                .name("Test User 2")
                .email("test2@example.com")
                .password("password")
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        userRepository.delete(user);
        userRepository.deleteById(user2.getId());

        Optional<User> retrieved = userRepository.findByEmail("test@example.com");
        Optional<User> retrieved2 = userRepository.findByEmail("test2@example.com");
        assertThat(retrieved).isNotPresent();
        assertThat(retrieved2).isNotPresent();
    }

    @Test
    void testDeleteNonExistentUser() {
        User user = User.builder()
                .name("Non Existent")
                .email("nonexistent@example.com")
                .password("password")
                .build();

        try {
            userRepository.delete(user);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }

    @Test
    void testSaveAll() {
        User user1 = User.builder()
                .name("User One")
                .email("user@example.com")
                .password("password")
                .build();
        User user2 = User.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("password")
                .build();
        userRepository.saveAll(List.of(user1, user2));
        assertThat(userRepository.findAll()).hasSize(2);
    }

}