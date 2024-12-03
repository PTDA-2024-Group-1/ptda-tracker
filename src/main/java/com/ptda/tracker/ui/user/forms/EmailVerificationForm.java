package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.user.EmailVerification;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.email.EmailService;
import com.ptda.tracker.services.user.EmailVerificationService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class EmailVerificationForm extends JPanel {
    private final MainFrame mainFrame;
    private final EmailVerificationService emailVerificationService;
    private final User user;
    private final String returnScreen;
    private final Runnable onVerificationSuccess;

    public EmailVerificationForm(MainFrame mainFrame, User user, String returnScreen, Runnable onVerificationSuccess) {
        if (user == null) {
            throw new IllegalArgumentException("newUser passed to EmailVerificationForm cannot be null");
        }
        this.mainFrame = mainFrame;
        this.emailVerificationService = mainFrame.getContext().getBean(EmailVerificationService.class);
        this.user = user;
        this.returnScreen = returnScreen;
        this.onVerificationSuccess = onVerificationSuccess;
        new Thread(this::sendVerificationCode).start();
        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> {
            mainFrame.showScreen(returnScreen);
        });
        verifyButton.addActionListener(e -> {
            if (verifyEmail()) {
                LoginForm.saveCredentials(user);
                UserSession.getInstance().setUser(user);
                onVerificationSuccess.run();
                JOptionPane.showMessageDialog(
                        this,
                        EMAIL_VERIFIED_SUCCESSFULLY,
                        SUCCESS,
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private void sendVerificationCode() {
        EmailVerification emailVerification = emailVerificationService.create(user.getEmail());
        EmailService emailService = mainFrame.getContext().getBean(EmailService.class);
        String subject = "Divi - " + EMAIL_VERIFICATION;
        String message = VERIFICATION_CODE_MESSAGE + ": " + emailVerification.getCode();
        emailService.sendEmail(user.getEmail(), subject, message);
    }

    private boolean verifyEmail() {
        String verificationCode = verificationCodeField.getText();
        Optional<EmailVerification> emailVerification = emailVerificationService.
                getByEmail(user.getEmail());
        if (emailVerification.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    NO_EMAIL_VERIFICATION_FOUND,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (!String.valueOf(emailVerification.get().getCode()).equals(verificationCode)) {
            JOptionPane.showMessageDialog(
                    this,
                    VERIFICATION_CODE_INCORRECT,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (emailVerification.get().getCreatedAt() + 5 * 60000 > System.currentTimeMillis()) {
            JOptionPane.showMessageDialog(
                    this,
                    VERIFICATION_CODE_EXPIRED,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (emailVerification.get().isUsed()) {
            JOptionPane.showMessageDialog(
                    this,
                    VERIFICATION_CODE_ALREADY_USED,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        if (emailVerification.get().getCode().equals(verificationCode)) {
            emailVerificationService.activate(emailVerification.get());
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    INVALID_VERIFICATION_CODE,
                    ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JLabel instructionLabel = new JLabel(ENTER_VERIFICATION_CODE_SENT + ": " + user.getEmail());
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and top
        topPanel.add(instructionLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and form

        add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Internal margins

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Verification code field with label
        JLabel verificationCodeLabel = new JLabel(VERIFICATION_CODE + ":");
        verificationCodeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(verificationCodeLabel, gbc);

        verificationCodeField = new JTextField();
        verificationCodeField.setFont(new Font("Arial", Font.PLAIN, 14));
        verificationCodeField.setPreferredSize(new Dimension(200, 30));
        verificationCodeField.setToolTipText(ENTER_VERIFICATION_CODE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(verificationCodeField, gbc);

        // Verify button
        verifyButton = new JButton(VERIFY);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(verifyButton, gbc);

        // Back button
        cancelButton = new JButton(CANCEL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(cancelButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private JTextField verificationCodeField;
    private JButton verifyButton, cancelButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            ENTER_VERIFICATION_CODE_SENT = localeManager.getTranslation("enter_verification_code_sent"),
            VERIFY = localeManager.getTranslation("verify"),
            CANCEL = localeManager.getTranslation("cancel"),
            REGISTRATION_SUCCESSFUL = localeManager.getTranslation("registration_successful"),
            SUCCESS = localeManager.getTranslation("success"),
            EMAIL_VERIFICATION = localeManager.getTranslation("email_verification"),
            ENTER_VERIFICATION_CODE = localeManager.getTranslation("enter_verification_code"),
            VERIFICATION_CODE = localeManager.getTranslation("verification_code"),
            VERIFICATION_CODE_MESSAGE = localeManager.getTranslation("verification_code_message"),
            VERIFICATION_CODE_INCORRECT = localeManager.getTranslation("verification_code_incorrect"),
            NO_EMAIL_VERIFICATION_FOUND = localeManager.getTranslation("no_email_verification_found"),
            ERROR = localeManager.getTranslation("error"),
            VERIFICATION_CODE_ALREADY_USED = localeManager.getTranslation("verification_code_already_used"),
            VERIFICATION_CODE_EXPIRED = localeManager.getTranslation("verification_code_expired"),
            INVALID_VERIFICATION_CODE = localeManager.getTranslation("invalid_verification_code"),
            EMAIL_VERIFIED_SUCCESSFULLY = localeManager.getTranslation("email_verified_successfully");
}