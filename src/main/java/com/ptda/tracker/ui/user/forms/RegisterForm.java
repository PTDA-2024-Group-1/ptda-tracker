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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.MessageFormat;

public class RegisterForm extends JPanel {
    private final MainFrame mainFrame;
    private User newUser;
    private final boolean verifyEmail;
    private JLabel logoLabel;
    private int minLogoSize = 150;
    private int maxLogoSize = 300;

    public RegisterForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.verifyEmail = mainFrame.getContext().getBean(EmailService.class).isEmailVerificationEnabled();
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
        styleButton(goToLoginButton);
        styleButton(registerButton);
    }

    private void styleButton(JButton button) {
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    }

    private void setListeners() {
        showPasswordCheckbox.addActionListener(e -> {
            boolean showPassword = showPasswordCheckbox.isSelected();
            passwordField.setEchoChar(showPassword ? '\0' : '*');
            confirmPasswordField.setEchoChar(showPassword ? '\0' : '*');
        });
        registerButton.addActionListener(e -> register());
        goToLoginButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.LOGIN_FORM));
        ThemeManager.getInstance().addThemeChangeListener(() -> updateLogoSize());
    }

    private void register() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

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
        newUser.setEmailVerified(verifyEmail);
        userService.update(newUser);
        LoginForm.saveCredentials(newUser);
        UserSession.getInstance().setUser(newUser);
        mainFrame.registerAndShowScreen(ScreenNames.NAVIGATION_SCREEN, new NavigationScreen(mainFrame));
        mainFrame.removeScreen(ScreenNames.LOGIN_FORM);
        mainFrame.removeScreen(ScreenNames.EMAIL_VERIFICATION_FORM);
        mainFrame.removeScreen(ScreenNames.REGISTER_FORM);
    }

    private void updateFormLogo(int size) {
        boolean isDark = ThemeManager.getInstance().isDark();
        ImageIcon appLogo = ImageResourceManager.getThemeBasedIcon(isDark);
        Image scaledImage = appLogo.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
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
        JLabel titleLabel = new JLabel(REGISTER, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formGbc.gridx = 1;
        formGbc.gridy = 0;
        formGbc.gridwidth = 1;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(titleLabel, formGbc);

        // Campo de nome
        JLabel nameLabel = new JLabel(NAME + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.gridwidth = 1;
        formGbc.insets = new Insets(5, 5, 5, 5);
        rightPanel.add(nameLabel, formGbc);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(nameField, formGbc);

        // Campo de email
        JLabel emailLabel = new JLabel(EMAIL + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        rightPanel.add(emailLabel, formGbc);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(emailField, formGbc);

        // Campo de senha
        JLabel passwordLabel = new JLabel(PASSWORD + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        rightPanel.add(passwordLabel, formGbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(passwordField, formGbc);

        // Campo de confirmação de senha
        JLabel confirmPasswordLabel = new JLabel(CONFIRM_PASSWORD + ":", SwingConstants.RIGHT);
        formGbc.gridx = 0;
        formGbc.gridy = 4;
        rightPanel.add(confirmPasswordLabel, formGbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 30)); // Aumenta o tamanho do campo de texto
        formGbc.gridx = 1;
        rightPanel.add(confirmPasswordField, formGbc);

        // Checkbox para mostrar senha
        showPasswordCheckbox = new JCheckBox(SHOW_PASSWORD);
        formGbc.gridx = 1;
        formGbc.gridy = 5;
        rightPanel.add(showPasswordCheckbox, formGbc);

        // Botão de registro
        registerButton = new JButton(REGISTER);
        formGbc.gridx = 1;
        formGbc.gridy = 6;
        formGbc.insets = new Insets(15, 5, 5, 5);
        rightPanel.add(registerButton, formGbc);

        // Botão para ir ao login
        goToLoginButton = new JButton(GO_TO_LOGIN);
        formGbc.gridy = 7;
        formGbc.insets = new Insets(5, 5, 5, 5);
        rightPanel.add(goToLoginButton, formGbc);

        gbc.gridx = 2;
        gbc.weightx = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        updateLogoSize();
    }

    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JCheckBox showPasswordCheckbox;
    private JButton registerButton, goToLoginButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            REGISTER = localeManager.getTranslation("register"),
            NAME = localeManager.getTranslation("name"),
            EMAIL = localeManager.getTranslation("email"),
            PASSWORD = localeManager.getTranslation("password"),
            CONFIRM_PASSWORD = localeManager.getTranslation("confirm_password"),
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