package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ChangePasswordForm;
import com.ptda.tracker.ui.forms.ProfileForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;

public class ProfileView extends JPanel {
    private final JLabel nameLabel;
    private final JLabel emailLabel;
    private final MainFrame mainFrame;

    public ProfileView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameLabel = new JLabel();
        add(nameLabel, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailLabel = new JLabel();
        add(emailLabel, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> navigateToEditProfile());
        add(editButton, gbc);

        gbc.gridx = 1;
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> navigateToChangePassword());
        add(changePasswordButton, gbc);

        // Load user data
        refreshUserData();
    }

    private void refreshUserData() {
        User user = UserSession.getInstance().getUser();
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
        }
    }

    private void navigateToEditProfile() {
        mainFrame.registerAndShowScreen(ScreenNames.PROFILE_FORM, new ProfileForm(mainFrame));
    }

    private void navigateToChangePassword() {
        mainFrame.registerAndShowScreen(ScreenNames.CHANGE_PASSWORD_FORM, new ChangePasswordForm(mainFrame));
    }
}
