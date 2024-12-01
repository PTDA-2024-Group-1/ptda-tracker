package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.screens.NavigationScreen;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.prefs.Preferences;

import static com.ptda.tracker.config.AppConfig.LOGO_PATH;

public class LoginForm extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        userService = mainFrame.getContext().getBean(UserService.class);

        initComponents();
        setListeners();
    }

    private void setListeners() {
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(ScreenNames.REGISTER_FORM, new RegisterForm(mainFrame));
        });
    }

    private void login() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validations
        int i = 0;
        if (email.isEmpty()) {
            showError(EMAIL_REQUIRED, usernameField);
            i++;
        }
        if (password.isEmpty()) {
            showError(PASSWORD_REQUIRED, passwordField);
            i++;
        }
        if (i > 0) return;

        // Login
        Optional<User> user = userService.login(email, password);
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, INVALID_CREDENTIALS, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (user.get().isEmailVerified()) {
            onAuthSuccess(user.get(), mainFrame);
            JOptionPane.showMessageDialog(this, WELCOME_BACK, MESSAGE, JOptionPane.INFORMATION_MESSAGE);
        } else {
            mainFrame.registerAndShowScreen(ScreenNames.EMAIL_VERIFICATION_FORM, new EmailVerificationForm(mainFrame, user.get(), mainFrame.getCurrentScreen()));
        }
    }

    private void showError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, ERROR, JOptionPane.ERROR_MESSAGE);
        component.requestFocusInWindow();
    }

    public static void onAuthSuccess(User user, MainFrame mainFrame) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("email", user.getEmail());
        preferences.put("password", user.getPassword());
        UserSession.getInstance().setUser(user);

        mainFrame.removeScreen(ScreenNames.LOGIN_FORM);
        mainFrame.removeScreen(ScreenNames.REGISTER_FORM);

        // Create the NavigationScreen
        NavigationScreen navigationScreen = new NavigationScreen(mainFrame);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, navigationScreen);
        mainFrame.setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and logotype
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logotype
        ImageIcon appLogo = new ImageIcon(LOGO_PATH);
        Image scaledImage = appLogo.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Resize to 100x100 pixels
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre o título e o logotipo
        topPanel.add(logoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        JLabel usernameLabel = new JLabel(EMAIL + ":");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 30)); // Tamanho do campo
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel(PASSWORD + ":");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30)); // Tamanho do campo
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Login
        loginButton = new JButton(LOGIN);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(loginButton, gbc);

        // Register
        registerButton = new JButton(GO_TO_REGISTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(registerButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            LOGIN = localeManager.getTranslation("login"),
            EMAIL = localeManager.getTranslation("email"),
            PASSWORD = localeManager.getTranslation("password"),
            GO_TO_REGISTER = localeManager.getTranslation("go_to_register"),
            EMAIL_REQUIRED = localeManager.getTranslation("email_required"),
            PASSWORD_REQUIRED = localeManager.getTranslation("password_required"),
            WELCOME_BACK = localeManager.getTranslation("welcome_back"),
            INVALID_CREDENTIALS = localeManager.getTranslation("invalid_credentials"),
            ERROR = localeManager.getTranslation("error"),
            MESSAGE = localeManager.getTranslation("message");
}