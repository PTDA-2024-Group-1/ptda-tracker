package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.TrackerApplication;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import java.util.prefs.Preferences;

public class LoginForm extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;
    private User user;
    private JLabel logoLabel;
    private final Color primaryColor = new Color(51, 153, 255);
    private int minLogoSize = 150;
    private int maxLogoSize = 300;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        userService = mainFrame.getContext().getBean(UserService.class);

        initComponents();
        setListeners();
        styleComponents();

        // Adicionar listener para redimensionamento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLogoSize();
            }
        });
    }

    private void updateLogoSize() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int size = Math.min(Math.min(panelWidth / 3, panelHeight - 100), maxLogoSize);
        size = Math.max(size, minLogoSize);
        updateFormLogo(size);
    }

    private void styleComponents() {
        styleButton(loginButton);
        styleButton(registerButton);
    }

    private void styleButton(JButton button) {
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    }

    private void setListeners() {
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(ScreenNames.REGISTER_FORM, new RegisterForm(mainFrame));
        });
        ThemeManager.getInstance().addThemeChangeListener(() -> updateLogoSize());
    }

    private void login() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<User> userOptional = userService.login(email, password);
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
            boolean verifyEmail = mainFrame.getContext().getBean(EmailService.class).isEmailVerificationEnabled();
            if (verifyEmail) {
                mainFrame.registerAndShowScreen(
                        ScreenNames.EMAIL_VERIFICATION_FORM,
                        new EmailVerificationForm(mainFrame, user, mainFrame.getCurrentScreen(), this::onEmailVerificationSuccess)
                );
            } else {
                onEmailVerificationSuccess();
            }
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

    private void updateFormLogo(int size) {
        boolean isDark = ThemeManager.getInstance().isDark();
        ImageIcon appLogo = ImageResourceManager.getThemeBasedIcon(isDark);
        Image scaledImage = appLogo.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Painel esquerdo com logo
        JPanel leftPanel = new JPanel(new GridBagLayout());
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(logoLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(leftPanel, gbc);

        // Separador vertical
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(separator, gbc);

        // Painel direito com form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel titleLabel = new JLabel(LOGIN, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formGbc.gridx = 1;
        formGbc.gridy = 0;
        formGbc.gridwidth = 1;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(titleLabel, formGbc);

        // Campo de email
        JLabel emailLabel = new JLabel(EMAIL + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.gridwidth = 1;
        formGbc.insets = new Insets(5, 5, 5, 5);
        rightPanel.add(emailLabel, formGbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(usernameField, formGbc);

        // Campo de senha
        JLabel passwordLabel = new JLabel(PASSWORD + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        rightPanel.add(passwordLabel, formGbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(passwordField, formGbc);

        // Botão de login
        loginButton = new JButton(LOGIN);
        formGbc.gridx = 1;
        formGbc.gridy = 3;
        formGbc.insets = new Insets(15, 5, 5, 5);
        rightPanel.add(loginButton, formGbc);

        // Botão de registro
        registerButton = new JButton(GO_TO_REGISTER);
        formGbc.gridy = 4;
        formGbc.insets = new Insets(5, 5, 5, 5);
        rightPanel.add(registerButton, formGbc);

        gbc.gridx = 2;
        gbc.weightx = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        updateLogoSize();
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
            ALL_FIELDS_REQUIRED = localeManager.getTranslation("all_fields_required"),
            WELCOME_BACK = localeManager.getTranslation("welcome_back"),
            INVALID_CREDENTIALS = localeManager.getTranslation("invalid_credentials"),
            ERROR = localeManager.getTranslation("error"),
            MESSAGE = localeManager.getTranslation("message");
}