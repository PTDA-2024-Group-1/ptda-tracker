package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.EmailVerification;

import java.util.Optional;

public interface EmailVerificationService {

    Optional<EmailVerification> getByEmail(String email);

    EmailVerification create(String email);

    EmailVerification activate(EmailVerification emailVerification);

    boolean deleteAllExpired(long expirationTime);

}
