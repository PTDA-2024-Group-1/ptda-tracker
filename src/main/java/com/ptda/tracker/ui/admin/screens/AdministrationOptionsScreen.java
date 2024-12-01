package com.ptda.tracker.ui.admin.screens;

import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.views.ManageUserView;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class AdministrationOptionsScreen extends JPanel {
    private final MainFrame mainFrame;

    public AdministrationOptionsScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout()); // Layout to center all content
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title configuration
        JLabel titleLabel = new JLabel("Administration Options", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Central panel configuration
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(ScreenNames.MANAGE_USER_VIEW, new ManageUserView(mainFrame));
        });
        buttonPanel.add(manageUsersButton, new GridBagConstraints());

        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        add(contentPanel, gbc);
    }
}
