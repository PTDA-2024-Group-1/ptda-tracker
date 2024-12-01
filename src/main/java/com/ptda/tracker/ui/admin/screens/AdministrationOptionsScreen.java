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
        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Administration Options", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.setFont(new Font("Arial", Font.BOLD, 14));
        manageUsersButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.MANAGE_USER_VIEW, new ManageUserView(mainFrame)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(manageUsersButton);
        add(buttonPanel, BorderLayout.CENTER);
    }
}