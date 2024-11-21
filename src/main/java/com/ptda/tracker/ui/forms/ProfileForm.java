package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JPanel {
    private final JTextField nameField;
    private final JTextField emailField;
    private final JButton saveButton;
    private final JButton cancelButton;

    public ProfileForm(MainFrame mainFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        User user = UserSession.getInstance().getUser();

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Edit Profile", SwingConstants.CENTER);
        add(title, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(user.getName());
        add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail());
        add(emailField, gbc);

        // Save Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave(user, mainFrame));
        add(saveButton, gbc);

        // Cancel Button
        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        add(cancelButton, gbc);
    }

    private void onSave(User user, MainFrame mainFrame) {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update user object
        user.setName(newName);
        user.setEmail(newEmail);

        // Save user to the session or call a service
        UserSession.getInstance().setUser(user);

        JOptionPane.showMessageDialog(this, "Profile successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Navigate back
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }
}
