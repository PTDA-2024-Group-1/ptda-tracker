package com.ptda.tracker.ui.forms;

import com.ptda.tracker.TrackerApplication;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.screens.NavigationScreen;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.prefs.Preferences;

public class LoginForm extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    private static final String LOGO_PATH = "src/main/resources/images/divi.png";

    private static final String
            REGISTER_SCREEN = ScreenNames.REGISTER_FORM,
            LOGIN_SCREEN = ScreenNames.LOGIN_FORM;

    private static final String
            LOGIN = "Login",
            EMAIL = "Email",
            PASSWORD = "Password",
            GO_TO_REGISTER = "Go to Register",
            EMAIL_CANNOT_BE_EMPTY = "Email cannot be empty",
            PASSWORD_CANNOT_BE_EMPTY = "Password cannot be empty",
            LOGIN_SUCCESSFUL = "Logged in successfully!",
            EMAIL_OR_PASSWORD_INCORRECT = "Email or password is incorrect. Please try again.",
            ERROR = "Error",
            MESSAGE = "Message";

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        ApplicationContext context = mainFrame.getContext();
        userService = context.getBean(UserService.class);

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and logotype
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 0, 0)); // Black
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logotype
        ImageIcon originalIcon = new ImageIcon(LOGO_PATH);
        Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Resize to 100x100 pixels
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
            showError(EMAIL_CANNOT_BE_EMPTY, usernameField);
            i++;
        }
        if (password.isEmpty()) {
            showError(PASSWORD_CANNOT_BE_EMPTY, passwordField);
            i++;
        }
        if (i > 0) return;

        // Login
        Optional<User> user = userService.login(email, password);
        if (user.isPresent()) {
            onAuthSuccess(user.get(), mainFrame);
            showMessage(LOGIN_SUCCESSFUL, JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage(EMAIL_OR_PASSWORD_INCORRECT, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, ERROR, JOptionPane.ERROR_MESSAGE);
        component.requestFocusInWindow();
    }

    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, MESSAGE, messageType);
    }

    public static void onAuthSuccess(User user, MainFrame mainFrame) {
        Preferences preferences = Preferences.userNodeForPackage(TrackerApplication.class);
        preferences.put("email", user.getEmail());
        preferences.put("password", user.getPassword());
        UserSession.getInstance().setUser(user);

        mainFrame.removeScreen(REGISTER_SCREEN);
        mainFrame.removeScreen(LOGIN_SCREEN);

        // Create the NavigationScreen
        NavigationScreen navigationScreen = new NavigationScreen(mainFrame);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, navigationScreen);
        mainFrame.setVisible(true);
    }
}