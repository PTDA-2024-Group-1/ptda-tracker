package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;

    public RegisterForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        ApplicationContext context = mainFrame.getContext();
        userService = context.getBean(UserService.class);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margens ao redor do formulário

        // Painel superior com título e logotipo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Título
        JLabel titleLabel = new JLabel("Register", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Fonte
        titleLabel.setForeground(new Color(0, 0, 0)); // Cor preta
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        // Logotipo
        ImageIcon originalIcon = new ImageIcon("src/main/java/com/ptda/tracker/ui/images/divi.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH); // Redimensionar
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espaço entre título e logotipo
        topPanel.add(logoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Painel principal com campos e botões
        JPanel formPanel = new JPanel(new GridBagLayout()); // Usar GridBagLayout
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Margens internas

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo de Nome com label
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(200, 30)); // Tamanho do campo
        nameField.setToolTipText("Enter your full name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(nameField, gbc);

        // Campo de Email com label
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setToolTipText("Enter a valid email address");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        // Campo de Password com label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setToolTipText("Enter a strong password (min. 8 characters)");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(passwordField, gbc);

        // Campo de Confirmar Password com label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        confirmPasswordField.setToolTipText("Re-enter your password");
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(confirmPasswordField, gbc);

        // Botão para mostrar/ocultar senha
        JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.addActionListener(e -> {
            boolean showPassword = showPasswordCheckbox.isSelected();
            passwordField.setEchoChar(showPassword ? '\0' : '*');
            confirmPasswordField.setEchoChar(showPassword ? '\0' : '*');
        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(showPasswordCheckbox, gbc);

        // Botão de Registro
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(56, 56, 56)); // Cor de fundo
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(registerButton, gbc);
        registerButton.addActionListener(e -> register());

        // Botão para ir ao Login
        JButton loginButton = new JButton("Go to Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(56, 56, 56));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(loginButton, gbc);
        loginButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.LOGIN_FORM));

        add(formPanel, BorderLayout.CENTER);
    }

    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validação de campos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User newUser = userService.register(name, email, password);
            if (newUser != null) {
                LoginForm.onAuthSuccess(newUser, mainFrame);
                JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Limpar campos
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
