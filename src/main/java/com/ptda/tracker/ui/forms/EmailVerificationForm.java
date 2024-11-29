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

    private static final String
            TITLE = "Enter the verification code sent to your email:",
            VERIFY = "Verify",
            BACK = "Back",
            REGISTRATION_SUCCESSFUL = "Registration successful",
            SUCCESS = "Success",
            EMAIL_VERIFICATION = "Email Verification",
            VERIFICATION_CODE_MESSAGE = "Your verification code is: ",
            VERIFICATION_CODE_NOT_FOUND = "Verification code not found. Please try again.",
            ERROR = "Error",
            VERIFICATION_CODE_ALREADY_USED = "Verification code already used. Please request a new one.",
            INVALID_VERIFICATION_CODE = "Invalid verification code. Please try again.",
            EMAIL_VERIFIED_SUCCESSFULLY = "Email verified successfully";

    public EmailVerificationForm(MainFrame mainFrame, User newUser) {
        new Thread(this::sendVerificationCode).start();
        this.mainFrame = mainFrame;
        this.emailVerificationService = mainFrame.getContext().getBean(EmailVerificationService.class);
        this.newUser = newUser;
        initUI();
        setListeners();
    }

    private void initUI() {
        JLabel instructionLabel = new JLabel(TITLE);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        verificationCodeField = new JTextField();
        verifyButton = new JButton(VERIFY);
        backButton = new JButton(BACK);

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
                        REGISTRATION_SUCCESSFUL,
                        SUCCESS,
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private void sendVerificationCode() {
        EmailVerification emailVerification = emailVerificationService.create(newUser.getEmail());
        EmailService emailService = mainFrame.getContext().getBean(EmailService.class);
        String subject = EMAIL_VERIFICATION;
        String message = VERIFICATION_CODE_MESSAGE + emailVerification.getCode();
        emailService.sendEmail(newUser.getEmail(), subject, message);
    }

    private boolean verifyEmail() {
        String verificationCode = verificationCodeField.getText();
        EmailVerification emailVerification = emailVerificationService.
                getByEmail(newUser.getEmail()).orElse(null);
        if (emailVerification == null) {
            JOptionPane.showMessageDialog(
                    this,
                    VERIFICATION_CODE_NOT_FOUND,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (emailVerification.isUsed()) {
            JOptionPane.showMessageDialog(
                    this,
                    VERIFICATION_CODE_ALREADY_USED,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (!emailVerification.getCode().equals(verificationCode)) {
            JOptionPane.showMessageDialog(
                    this,
                    INVALID_VERIFICATION_CODE,
                    ERROR,
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
                EMAIL_VERIFIED_SUCCESSFULLY,
                SUCCESS,
                JOptionPane.INFORMATION_MESSAGE
        );
        return true;
    }
}