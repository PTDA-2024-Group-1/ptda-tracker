package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.EmailVerification;
import com.ptda.tracker.repositories.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceHibernateImpl implements EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public Optional<EmailVerification> getByEmail(String email) {
        return emailVerificationRepository.findByEmailAndIsUsedIsFalse(email);
    }

    @Override
    @Transactional
    public EmailVerification create(String email) {
        return emailVerificationRepository.save(EmailVerification.builder()
                .email(email)
                .code(generateRandomCode(6))
                .isUsed(false)
                .build());
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int code = random.nextInt(max - min + 1) + min;
        return Integer.toString(code);
    }

    @Override
    @Transactional
    public EmailVerification activate(EmailVerification emailVerification) {
        emailVerification.setUsed(true);
        return emailVerificationRepository.save(emailVerification);
    }

    @Override
    @Transactional
    public boolean deleteAllExpired(long expirationTime) {
        emailVerificationRepository.deleteAllByIsUsedIsTrueAndCreatedAtBefore(expirationTime);
        return true;
    }
}
