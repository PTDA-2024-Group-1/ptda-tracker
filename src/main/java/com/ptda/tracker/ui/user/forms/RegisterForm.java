package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.config.AppConfig;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.email.EmailService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.screens.NavigationScreen;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import com.ptda.tracker.theme.ThemeManager; 
import com.ptda.tracker.util.ImageResourceManager;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

public class RegisterForm extends JPanel {
    private final MainFrame mainFrame;
    private User newUser;
    private JLabel logoLabel; 

    public RegisterForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setListeners();
    }

    private void setListeners() {
        ThemeManager.getInstance().addThemeChangeListener(this::updateFormLogo);
        showPasswordCheckbox.addActionListener(e -> {
            boolean showPassword = showPasswordCheckbox.isSelected();
            passwordField.setEchoChar(showPassword ? '\0' : '*');
            confirmPasswordField.setEchoChar(showPassword ? '\0' : '*');
        });
        registerButton.addActionListener(e -> register());
        goToLoginButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.LOGIN_FORM));
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean verifyEmail = mainFrame.getContext().getBean(EmailService.class).isEmailVerificationEnabled();

        // Fields validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED,
                    ERROR, JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (verifyEmail && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                    INVALID_EMAIL_FORMAT, ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, PASSWORDS_DO_NOT_MATCH,
                    ERROR, JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (password.length() < AppConfig.MIN_PASSWORD_LENGTH) {
            JOptionPane.showMessageDialog(this,
                    MessageFormat.format(PASSWORD_MINIMAL_LENGHT, AppConfig.MIN_PASSWORD_LENGTH),
                    ERROR, JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                SURE_ALL_DATA_CORRECT, CONFIRMATION,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Register user
        try {
            UserService userService = mainFrame.getContext().getBean(UserService.class);
            if (userService.getByEmail(email).isPresent()) {
                JOptionPane.showMessageDialog(this,
                        EMAIL_ALREADY_REGISTERED, ERROR,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            newUser = userService.register(name, email, password);
            if (newUser != null) {
                if (verifyEmail) {
                    mainFrame.registerAndShowScreen(
                            ScreenNames.EMAIL_VERIFICATION_FORM,
                            new EmailVerificationForm(mainFrame, newUser, ScreenNames.LOGIN_FORM, this::onEmailVerificationSuccess)
                    );
                } else {
                    onEmailVerificationSuccess();
                }
                // Clear fields
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, REGISTRATION_FAILED,
                        ERROR, JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    AN_ERROR_OCCURRED + ": " + ex.getMessage(), ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onEmailVerificationSuccess() {
        UserService userService = mainFrame.getContext().getBean(UserService.class);
        newUser.setEmailVerified(true);
        userService.update(newUser);
        LoginForm.saveCredentials(newUser);
        UserSession.getInstance().setUser(newUser);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, new NavigationScreen(mainFrame));
        mainFrame.removeScreen(ScreenNames.LOGIN_FORM);
        mainFrame.removeScreen(ScreenNames.EMAIL_VERIFICATION_FORM);
        mainFrame.removeScreen(ScreenNames.REGISTER_FORM);
    }

    private void updateFormLogo() {
        boolean isDark = ThemeManager.getInstance().isDark();
        ImageIcon appLogo = ImageResourceManager.getThemeBasedIcon(isDark);
        Image scaledImage = appLogo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margins around the form

        // Top panel with title and logo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(REGISTER, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Font
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        logoLabel = new JLabel();
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and logo
        topPanel.add(logoLabel);
        updateFormLogo();

        add(topPanel, BorderLayout.NORTH);

        // Center panel for horizontal alignment
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Adjust GridBagConstraints for horizontal centering
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the left of their cells

        // Name field with label
        JLabel nameLabel = new JLabel(NAME + ":", SwingConstants.RIGHT);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Align label to the right
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30)); // Field size
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Align text field to center
        centerPanel.add(nameField, gbc);

        // Email field with label
        JLabel emailLabel = new JLabel(EMAIL + ":", SwingConstants.RIGHT);
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(emailField, gbc);

        // Password field with label
        JLabel passwordLabel = new JLabel(PASSWORD + ":", SwingConstants.RIGHT);
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(passwordField, gbc);

        // Confirm password field with label
        JLabel confirmPasswordLabel = new JLabel(CONFIRM_PASSWORD + ":", SwingConstants.RIGHT);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(confirmPasswordField, gbc);

        // Show password checkbox
        showPasswordCheckbox = new JCheckBox(SHOW_PASSWORD);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(showPasswordCheckbox, gbc);

        // Register button
        registerButton = new JButton(REGISTER);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(registerButton, gbc);

        // Go to login button
        goToLoginButton = new JButton(GO_TO_LOGIN);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(goToLoginButton, gbc);

        // Add the center panel to the form
        add(centerPanel, BorderLayout.CENTER);
    }

    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JCheckBox showPasswordCheckbox;
    private JButton registerButton, goToLoginButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            REGISTER = localeManager.getTranslation("register"),
            NAME = localeManager.getTranslation("name"),
            ENTER_NAME = localeManager.getTranslation("enter_name"),
            EMAIL = localeManager.getTranslation("email"),
            ENTER_EMAIL = localeManager.getTranslation("enter_email"),
            PASSWORD = localeManager.getTranslation("password"),
            ENTER_STRONG_PASSWORD = localeManager.getTranslation("enter_strong_password"),
            CONFIRM_PASSWORD = localeManager.getTranslation("confirm_password"),
            REPEAT_PASSWORD = localeManager.getTranslation("repeat_password"),
            SHOW_PASSWORD = localeManager.getTranslation("show_password"),
            GO_TO_LOGIN = localeManager.getTranslation("go_to_login"),
            ALL_FIELDS_REQUIRED = localeManager.getTranslation("all_fields_required"),
            ERROR = localeManager.getTranslation("error"),
            INVALID_EMAIL_FORMAT = localeManager.getTranslation("invalid_email_format"),
            PASSWORDS_DO_NOT_MATCH = localeManager.getTranslation("passwords_do_not_match"),
            EMAIL_ALREADY_REGISTERED = localeManager.getTranslation("email_already_registered"),
            REGISTRATION_FAILED = localeManager.getTranslation("registration_failed"),
            AN_ERROR_OCCURRED = localeManager.getTranslation("an_error_occurred"),
            SURE_ALL_DATA_CORRECT = localeManager.getTranslation("sure_all_data_correct"),
            CONFIRMATION = localeManager.getTranslation("confirmation"),
            PASSWORD_MINIMAL_LENGHT = localeManager.getTranslation("password_minimal_length");
}