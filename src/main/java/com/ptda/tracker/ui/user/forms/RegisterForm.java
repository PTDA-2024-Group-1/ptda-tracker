package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

import static com.ptda.tracker.config.AppConfig.LOGO_PATH;

public class RegisterForm extends JPanel {
    private final MainFrame mainFrame;

    public RegisterForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setListeners();
    }

    private void setListeners() {
        showPasswordCheckbox.addActionListener(e -> {
            boolean showPassword = showPasswordCheckbox.isSelected();
            passwordField.setEchoChar(showPassword ? '\0' : '*');
            confirmPasswordField.setEchoChar(showPassword ? '\0' : '*');
        });
        registerButton.addActionListener(e -> register());
        goToLoginButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.LOGIN_FORM));
    }

    private void register() {
        JOptionPane.showMessageDialog(this,ARE_YOU_SURE_YOU_WANT_TO_REGISTER, CONFIRMATION, JOptionPane.WARNING_MESSAGE);

        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Fields validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, INVALID_EMAIL_FORMAT, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, PASSWORDS_DO_NOT_MATCH, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register user
        try {
            UserService userService = mainFrame.getContext().getBean(UserService.class);
            if (userService.getByEmail(email).isPresent()) {
                JOptionPane.showMessageDialog(this, EMAIL_ALREADY_REGISTERED, ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }
            User newUser = userService.register(name, email, password);
            if (newUser != null) {
                mainFrame.registerAndShowScreen(
                        ScreenNames.EMAIL_VERIFICATION_FORM,
                        new EmailVerificationForm(mainFrame, newUser, ScreenNames.LOGIN_FORM)
                );
                // Clear fields
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, REGISTRATION_FAILED, ERROR, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, AN_ERROR_OCCURRED + ex.getMessage(), ERROR, JOptionPane.ERROR_MESSAGE);
        }
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
        titleLabel.setForeground(new Color(0, 0, 0)); // Black color
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logo
        ImageIcon originalIcon = new ImageIcon(LOGO_PATH);
        Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Resize
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and logo
        topPanel.add(logoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Main panel with fields and buttons
        JPanel formPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Internal margins

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field with label
        JLabel nameLabel = new JLabel(NAME + ":");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(200, 30)); // Field size
        nameField.setToolTipText(ENTER_NAME);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(nameField, gbc);

        // Email field with label
        JLabel emailLabel = new JLabel(EMAIL + ":");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setToolTipText(ENTER_EMAIL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        // Password field with label
        JLabel passwordLabel = new JLabel(PASSWORD + ":");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setToolTipText(ENTER_PASSWORD);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(passwordField, gbc);

        // Confirm password field with label
        JLabel confirmPasswordLabel = new JLabel(CONFIRM_PASSWORD + ":");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        confirmPasswordField.setToolTipText(REPEAT_PASSWORD);
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(confirmPasswordField, gbc);

        // Show password checkbox
        showPasswordCheckbox = new JCheckBox(SHOW_PASSWORD);
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(showPasswordCheckbox, gbc);

        // Register button
        registerButton = new JButton(REGISTER);
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(registerButton, gbc);

        // Go to login button
        goToLoginButton = new JButton(GO_TO_LOGIN);
        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(goToLoginButton, gbc);

        add(formPanel, BorderLayout.CENTER);
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
            ENTER_PASSWORD = localeManager.getTranslation("enter_password"),
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
            ARE_YOU_SURE_YOU_WANT_TO_REGISTER = localeManager.getTranslation("are_you_sure_you_want_to_register"),
            CONFIRMATION = localeManager.getTranslation("confirmation");
}