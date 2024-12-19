package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceHibernateImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public User register(String name, String email, String password) {
        if (name == null || email == null || password == null) {
            return null;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return null;
        }
        User user = User.builder()
                .userType("USER")
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> create(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public Optional<User> login(String email, String password) {
        if (password == null || email == null) {
            System.out.println("Email or password is null.");
            return Optional.empty();
        }
        if (!userRepository.existsByEmail(email)) {
            System.out.println("User with email " + email + " does not exist.");
            return Optional.empty();
        }
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }
        System.out.println("Invalid password.");
        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }


    @Override
    public User changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(oldPassword, userOptional.get().getPassword())) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return user;
        }
        throw new IllegalArgumentException("Invalid email or password.");
    }

    @Override
    public boolean deleteById(Long id) {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public int countByUserType(String userType) {
        return (int) userRepository.countByUserType(userType);
    }
}