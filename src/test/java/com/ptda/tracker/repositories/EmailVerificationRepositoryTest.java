package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailVerificationRepositoryTest {

    private final EmailVerificationRepository emailVerificationRepository;

    @Test
    void testSaveAndFindByEmailAndIsUsedIsFalse() {
        EmailVerification emailVerification = EmailVerification.builder()
                .email("test@example.com")
                .code("test-code")
                .isUsed(false)
                .build();
        emailVerificationRepository.save(emailVerification);

        Optional<EmailVerification> retrieved = emailVerificationRepository.findByEmailAndIsUsedIsFalse("test@example.com");
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getCode()).isEqualTo("test-code");
    }

    @Test
    void testGetAllByEmail() {
        EmailVerification emailVerification1 = EmailVerification.builder()
                .email("test@example.com")
                .code("test-code-1")
                .build();
        emailVerificationRepository.save(emailVerification1);

        EmailVerification emailVerification2 = EmailVerification.builder()
                .email("test@example.com")
                .code("test-code-2")
                .build();
        emailVerificationRepository.save(emailVerification2);

        Collection<EmailVerification> verifications = emailVerificationRepository.getAllByEmail("test@example.com");
        assertThat(verifications).hasSize(2);
    }

    @Test
    void testMultipleVerificationsForSameEmail() {
        EmailVerification verification1 = EmailVerification.builder()
                .email("test@example.com")
                .code("test-code-1")
                .isUsed(false)
                .build();
        emailVerificationRepository.save(verification1);

        EmailVerification verification2 = EmailVerification.builder()
                .email("test@example.com")
                .code("test-code-2")
                .isUsed(false)
                .build();
        emailVerificationRepository.save(verification2);

        Collection<EmailVerification> verifications = emailVerificationRepository.getAllByEmail("test@example.com");
        assertThat(verifications).hasSize(2);
        assertThat(verifications).extracting("code").containsExactlyInAnyOrder("test-code-1", "test-code-2");
    }

    @Test
    void testFindByNonExistingEmail() {
        Optional<EmailVerification> result = emailVerificationRepository.findByEmailAndIsUsedIsFalse("nonexistent@example.com");
        assertThat(result).isNotPresent();
    }
}