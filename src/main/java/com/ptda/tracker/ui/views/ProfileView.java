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
        gbc.insets = new Insets(20, 20, 20, 20);
        setBackground(new Color(245, 245, 245));  // Cor de fundo suave

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("My Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(56, 56, 56));
        add(titleLabel, gbc);

        // Spacer
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(Box.createVerticalStrut(20), gbc);  // Adicionando espaçamento vertical

        // Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel nameLabelText = new JLabel("Name:");
        nameLabelText.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabelText.setForeground(new Color(56, 56, 56));
        add(nameLabelText, gbc);

        gbc.gridx = 1;
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(56, 56, 56));
        add(nameLabel, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel emailLabelText = new JLabel("Email:");
        emailLabelText.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabelText.setForeground(new Color(56, 56, 56));
        add(emailLabelText, gbc);

        gbc.gridx = 1;
        emailLabel = new JLabel();
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(56, 56, 56));
        add(emailLabel, gbc);

        // Spacer
        gbc.gridy = 4;
        add(Box.createVerticalStrut(30), gbc);

        // Buttons Panel
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 5;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(new Color(245, 245, 245));  // Fundo suave para os botões

        // Edit Profile Button
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> navigateToEditProfile());
        buttonsPanel.add(editButton);

        // Spacer
        buttonsPanel.add(Box.createHorizontalStrut(20));

        // Change Password Button
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> navigateToChangePassword());
        buttonsPanel.add(changePasswordButton);

        add(buttonsPanel, gbc);

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
        mainFrame.registerAndShowScreen(ScreenNames.PROFILE_FORM, new ProfileForm(mainFrame, this::refreshUserData));
    }

    private void navigateToChangePassword() {
        mainFrame.registerAndShowScreen(ScreenNames.CHANGE_PASSWORD_FORM, new ChangePasswordForm(mainFrame));
    }
}