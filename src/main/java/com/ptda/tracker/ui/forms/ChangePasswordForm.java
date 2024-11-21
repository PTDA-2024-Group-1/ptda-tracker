package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordForm extends JPanel {
    private final UserService userService;
    private final JPasswordField currentPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;
    private final JButton saveButton;
    private final JButton cancelButton;

    public ChangePasswordForm(MainFrame mainFrame) {
        userService = mainFrame.getContext().getBean(UserService.class);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Change Password", SwingConstants.CENTER);
        add(title, gbc);

        // Current Password
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        currentPasswordField = new JPasswordField();
        add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField();
        add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField, gbc);

        // Save Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave(mainFrame));
        add(saveButton, gbc);

        // Cancel Button
        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        add(cancelButton, gbc);
    }

    private void onSave(MainFrame mainFrame) {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call service to change password (e.g., userService.changePassword)
        User updatedUser = userService.changePassword(UserSession.getInstance().getUser().getEmail(), currentPassword, newPassword);
        UserSession.getInstance().setUser(updatedUser);
        JOptionPane.showMessageDialog(this, "Password successfully changed!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Navigate back
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }
}
