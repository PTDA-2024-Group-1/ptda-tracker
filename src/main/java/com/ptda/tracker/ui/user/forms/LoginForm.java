package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.screens.NavigationScreen;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import com.ptda.tracker.util.ThemeManager;
import com.ptda.tracker.util.ImageResourceManager;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.prefs.Preferences;

import static com.ptda.tracker.config.AppConfig.LOGO_PATH;

public class LoginForm extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;
    private User user;
    private JLabel logoLabel; 

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        userService = mainFrame.getContext().getBean(UserService.class);

        initComponents();
        setListeners();

        updateFormLogo();

        ThemeManager.getInstance().addThemeChangeListener(this::updateFormLogo);
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

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Login
        Optional<User> userOptional = userService.login(email, password);
        System.out.println(userOptional);
        if (userOptional.isEmpty()) {
            JOptionPane.showMessageDialog(this, INVALID_CREDENTIALS, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.user = userOptional.get();
        if (user.isEmailVerified()) {
            saveCredentials(user);
            UserSession.getInstance().setUser(user);
            mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, new NavigationScreen(mainFrame));
            JOptionPane.showMessageDialog(this, WELCOME_BACK, MESSAGE, JOptionPane.INFORMATION_MESSAGE);
        } else {
            onEmailVerificationSuccess();
            //mainFrame.registerAndShowScreen(ScreenNames.EMAIL_VERIFICATION_FORM, new EmailVerificationForm(mainFrame, user, mainFrame.getCurrentScreen(), this::onEmailVerificationSuccess));
        }
    }

    private void onEmailVerificationSuccess() {
        user.setEmailVerified(true);
        user = userService.update(user);
        saveCredentials(user);
        UserSession.getInstance().setUser(user);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, new NavigationScreen(mainFrame));
        mainFrame.removeScreen(ScreenNames.LOGIN_FORM);
        mainFrame.removeScreen(ScreenNames.EMAIL_VERIFICATION_FORM);
        mainFrame.removeScreen(ScreenNames.REGISTER_FORM);
    }

    public static void saveCredentials(User user) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("email", user.getEmail());
        preferences.put("password", user.getPassword());
    }

    private void updateFormLogo() {
        boolean isDark = ThemeManager.getInstance().isDark();
        ImageIcon appLogo = ImageResourceManager.getThemeBasedIcon(isDark);
        Image scaledImage = appLogo.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and logotype
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(LOGIN, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        logoLabel = new JLabel();
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and logo
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

    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton loginButton, registerButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            LOGIN = localeManager.getTranslation("login"),
            EMAIL = localeManager.getTranslation("email"),
            PASSWORD = localeManager.getTranslation("password"),
            GO_TO_REGISTER = localeManager.getTranslation("go_to_register"),
            EMAIL_REQUIRED = localeManager.getTranslation("email_required"),
            PASSWORD_REQUIRED = localeManager.getTranslation("password_required"),
            ALL_FIELDS_REQUIRED = localeManager.getTranslation("all_fields_required"),
            WELCOME_BACK = localeManager.getTranslation("welcome_back"),
            INVALID_CREDENTIALS = localeManager.getTranslation("invalid_credentials"),
            ERROR = localeManager.getTranslation("error"),
            MESSAGE = localeManager.getTranslation("message");
}