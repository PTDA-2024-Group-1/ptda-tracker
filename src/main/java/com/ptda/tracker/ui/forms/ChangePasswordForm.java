package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordForm extends JPanel {
    private final MainFrame mainFrame;

    public ChangePasswordForm(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        saveButton.addActionListener(e -> onSave(mainFrame));
    }

    private void onSave(MainFrame mainFrame) {
        // Get form values
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate form
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, ALL_FIELDS_REQUIRED, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, PASSWORDS_DO_NOT_MATCH, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        PasswordEncoder passwordEncoder = mainFrame.getContext().getBean(PasswordEncoder.class);
        if (!UserSession.getInstance().getUser().getPassword().equals(passwordEncoder.encode(currentPassword))) {
            JOptionPane.showMessageDialog(this, CURRENT_PASSWORD_INCORRECT, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call service to change password (e.g., userService.changePassword)
        UserService userService = mainFrame.getContext().getBean(UserService.class);
        User updatedUser = userService.changePassword(UserSession.getInstance().getUser().getEmail(), currentPassword, newPassword);
        UserSession.getInstance().setUser(updatedUser);
        JOptionPane.showMessageDialog(this, PASSWORD_CHANGED + "!", SUCCESS, JOptionPane.INFORMATION_MESSAGE);

        // Navigate back
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel(TITLE, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, gbc);

        // Current Password
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel currentPasswordLabel = new JLabel(CURRENT_PASSWORD + ":");
        currentPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(currentPasswordLabel, gbc);

        gbc.gridx = 1;
        currentPasswordField = new JPasswordField(20);
        add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel newPasswordLabel = new JLabel(NEW_PASSWORD + ":");
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        newPasswordField = new JPasswordField(20);
        add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel confirmPasswordLabel = new JLabel(CONFIRM_PASSWORD + ":");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);

        // Cancel Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        cancelButton = new JButton(CANCEL);
        add(cancelButton, gbc);

        // Save Button
        gbc.gridx = 1;
        saveButton = new JButton(SAVE);
        add(saveButton, gbc);
    }

    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            TITLE = localeManager.getTranslation("change_password"),
            CURRENT_PASSWORD = localeManager.getTranslation("current_password"),
            NEW_PASSWORD = localeManager.getTranslation("new_password"),
            CONFIRM_PASSWORD = localeManager.getTranslation("confirm_password"),
            CANCEL = localeManager.getTranslation("cancel"),
            SAVE = localeManager.getTranslation("save"),
            ALL_FIELDS_REQUIRED = localeManager.getTranslation("all_fields_required"),
            PASSWORDS_DO_NOT_MATCH = localeManager.getTranslation("passwords_do_not_match"),
            CURRENT_PASSWORD_INCORRECT = localeManager.getTranslation("current_password_incorrect"),
            PASSWORD_CHANGED = localeManager.getTranslation("password_changed"),
            SUCCESS = localeManager.getTranslation("success"),
            ERROR = localeManager.getTranslation("error");
}