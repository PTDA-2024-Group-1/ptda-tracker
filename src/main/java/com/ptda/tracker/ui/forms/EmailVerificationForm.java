package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.EmailVerification;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.email.EmailService;
import com.ptda.tracker.services.user.EmailVerificationService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class EmailVerificationForm extends JPanel {
    private final MainFrame mainFrame;
    private final EmailVerificationService emailVerificationService;
    private final User newUser;
    private JTextField verificationCodeField;
    private JButton verifyButton, backButton;

    public EmailVerificationForm(MainFrame mainFrame, User newUser) {
        new Thread(this::sendVerificationCode).start();
        this.mainFrame = mainFrame;
        this.emailVerificationService = mainFrame.getContext().getBean(EmailVerificationService.class);
        this.newUser = newUser;
        initUI();
        setListeners();
    }

    private void initUI() {
        JLabel instructionLabel = new JLabel("Enter the verification code sent to your email:");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        verificationCodeField = new JTextField();
        verifyButton = new JButton("Verify");
        backButton = new JButton("Back");

        // Set up the layout
        setLayout(new GridLayout(4, 1, 10, 10));
        add(instructionLabel);
        add(verificationCodeField);
        add(verifyButton);
        add(backButton);
    }

    private void setListeners() {
        backButton.addActionListener(e -> {
            mainFrame.showScreen(ScreenNames.REGISTER_FORM);
        });
        verifyButton.addActionListener(e -> {
            if (verifyEmail()) {
                LoginForm.onAuthSuccess(newUser, mainFrame);
                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private void sendVerificationCode() {
        EmailVerification emailVerification = emailVerificationService.create(newUser.getEmail());
        EmailService emailService = mainFrame.getContext().getBean(EmailService.class);
        String subject = "Email Verification";
        String message = "Your verification code is: " + emailVerification.getCode();
        emailService.sendEmail(newUser.getEmail(), subject, message);
    }

    private boolean verifyEmail() {
        String verificationCode = verificationCodeField.getText();
        EmailVerification emailVerification = emailVerificationService.
                getByEmail(newUser.getEmail()).orElse(null);
        if (emailVerification == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Verification code not found. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (emailVerification.isUsed()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Verification code already used. Please request a new one.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (!emailVerification.getCode().equals(verificationCode)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid verification code. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        UserService userService = mainFrame.getContext().getBean(UserService.class);
        newUser.setEmailVerified(true);
        userService.update(newUser);
        emailVerificationService.activate(emailVerification);
        JOptionPane.showMessageDialog(
                this,
                "Email verified successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
        return true;
    }
}
