package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.Tier;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceHibernateImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(String name, String email, String password) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        return userRepository.save(user);
    }

    @Override
    public boolean login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(oldPassword, userOptional.get().getPassword())) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTier(User user, Tier tier) {
        user.setTier(tier);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean delete(User user) {
        userRepository.delete(user);
        return true;
    }
}