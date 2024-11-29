package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndIsUsedIsFalse(String email);
    boolean deleteAllByIsUsedIsTrueAndCreatedAtBefore(long expirationTime);
    Collection<EmailVerification> getAllByEmail(String email);
}
