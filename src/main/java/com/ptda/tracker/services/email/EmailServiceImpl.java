package com.ptda.tracker.services.email;

import com.ptda.tracker.models.admin.GlobalVariableName;
import com.ptda.tracker.services.administration.GlobalVariableService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final GlobalVariableService globalVariableService;

    @Override
    public boolean isEmailVerificationEnabled() {
        String verifyEmail = globalVariableService.get(GlobalVariableName.VERIFY_EMAIL.toString());
        boolean enabled = verifyEmail != null && verifyEmail.equalsIgnoreCase("true");
        System.out.println("Email verification enabled: " + enabled);
        return enabled;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        //message.setFrom(new InternetAddress("envatoe985@gmail.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }

    /*
    public void sendEmailFromTemplate() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("sender@example.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, "recipient@example.com");
        message.setSubject("Test email from my SpringApplication");

        // Read the HTML template into a String variable
        String htmlTemplate = readFile("template.html");

        // Replace placeholders in the HTML template with dynamic values
        htmlTemplate = htmlTemplate.replace("${name}", "John Doe");
        htmlTemplate = htmlTemplate.replace("${message}", "Hello, this is a test email.");

        // Set the email's content to be the HTML template
        message.setContent(htmlTemplate, "text/html; charset=utf-8");

        mailSender.send(message);
    }
    */

}
