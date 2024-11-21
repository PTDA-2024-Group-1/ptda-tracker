package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
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

        setLayout(new GridLayout(10, 2, 10, 10)); // 6 rows, 2 columns, 10px horizontal and vertical gaps

        add(new JLabel("Register Form"));
        add(new JLabel());

        add(new JLabel("Name"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        add(new JLabel());
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());
        add(registerButton);

        add(new JLabel());
        JButton button = new JButton("Go to Login");
        button.addActionListener(e -> mainFrame.showScreen("loginScreen"));
        add(button);
    }

    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
        }
        User newUser = userService.register(name, email, password);
        if (newUser != null) {
            LoginForm.onAuthSuccess(newUser, mainFrame);
            JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
