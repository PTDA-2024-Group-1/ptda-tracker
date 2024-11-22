package com.ptda.tracker.services.email;

import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmail(String to, String subject, String text);

    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;

}
