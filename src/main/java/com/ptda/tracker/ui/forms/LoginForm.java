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
    private JButton loginButton;

    private static final String HOME_SCREEN = ScreenNames.HOME_SCREEN;
    private static final String BUDGETS_SCREEN = ScreenNames.BUDGETS_SCREEN;
    private static final String PROFILE_SCREEN = ScreenNames.PROFILE_SCREEN;
    private static final String NAVIGATION_SCREEN = ScreenNames.NAVIGATION_SCREEN;
    private static final String REGISTER_SCREEN = ScreenNames.REGISTER_FORM;
    private static final String LOGIN_SCREEN = ScreenNames.LOGIN_FORM;

    public LoginForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        ApplicationContext context = mainFrame.getContext();
        userService = context.getBean(UserService.class);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margens ao redor do formulário

        // Painel superior com título e logotipo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Fonte menor
        titleLabel.setForeground(new Color(0, 0, 0)); // Cor preta
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logotipo
        ImageIcon originalIcon = new ImageIcon("src/main/java/com/ptda/tracker/ui/images/divi.png"); // Caminho para o logotipo original
        Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Redimensionar para 100x100 pixels
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre o título e o logotipo
        topPanel.add(logoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Painel principal com campos e botões
        JPanel formPanel = new JPanel(new GridBagLayout()); // Usar GridBagLayout para controle
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Margens internas

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo de username com label
        JLabel usernameLabel = new JLabel("Email:");
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

        // Campo de password com label
        JLabel passwordLabel = new JLabel("Password:");
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

        // Botão de login
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(56, 56, 56)); // Cor de fundo
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(loginButton, gbc);

        // ActionListener do botão de login
        loginButton.addActionListener(e -> login());

        // Botão de registro com ActionListener preservado
        JButton registerButton = new JButton("Go to Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(56, 56, 56)); // Cor de fundo
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(registerButton, gbc);

        // ActionListener do botão de registro
        registerButton.addActionListener(e -> {
            mainFrame.registerScreen(ScreenNames.REGISTER_FORM, new RegisterForm(mainFrame));
            mainFrame.showScreen(ScreenNames.REGISTER_FORM);
        });

        add(formPanel, BorderLayout.CENTER);
    }

    private void login() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validações nos campos de entrada
        if (email.isEmpty()) {
            showError("O campo Email não pode estar vazio.", usernameField);
            return;
        }
        if (password.isEmpty()) {
            showError("O campo Password não pode estar vazio.", passwordField);
            return;
        }

        // Lógica de login
        Optional<User> user = userService.login(email, password);
        if (user.isPresent()) {
            onAuthSuccess(user.get(), mainFrame);
            showMessage("Login realizado com sucesso!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMessage("Email ou password incorretos. Tente novamente.", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Exibe mensagem de erro e foca no campo problemático
    private void showError(String message, JComponent component) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
        component.requestFocusInWindow();
    }

    // Exibe mensagem genérica
    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, "Mensagem", messageType);
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
