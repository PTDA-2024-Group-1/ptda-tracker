package com.ptda.tracker.ui.admin.screens;

import com.ptda.tracker.models.admin.GlobalVariableName;
import com.ptda.tracker.services.administration.GlobalVariableService;
import com.ptda.tracker.services.email.EmailService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.dialogs.GlobalStatisticsDialog;
import com.ptda.tracker.ui.admin.views.ManageTicketView;
import com.ptda.tracker.ui.admin.views.ManageUserView;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

public class AdministrationOptionsScreen extends JPanel {
    private final MainFrame mainFrame;
    private final GlobalVariableService globalVariableService;

    public AdministrationOptionsScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.globalVariableService = mainFrame.getContext().getBean(GlobalVariableService.class);
        initComponents();
        setListeners();
    }

    private void setListeners() {
        showStatsButton.addActionListener(e -> {
            new GlobalStatisticsDialog(mainFrame).setVisible(true);
        });
        manageTicketsButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(
                    ScreenNames.MANAGE_TICKET_VIEW,
                    new ManageTicketView(mainFrame, mainFrame.getCurrentScreen())
            );
        });
        manageUsersButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(
                    ScreenNames.MANAGE_USER_VIEW,
                    new ManageUserView(mainFrame, mainFrame.getCurrentScreen())
            );
        });
        emailVerificationToggleButton.addActionListener(e -> {
            boolean isEmailVerified = emailVerificationToggleButton.isSelected();
            globalVariableService.set(GlobalVariableName.VERIFY_EMAIL, String.valueOf(isEmailVerified));
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(ADMINISTRATION_DASHBOARD, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // Center Panel with GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        showStatsButton = new JButton(SHOW_STATS);
        manageUsersButton = new JButton(MANAGE_USERS);
        manageTicketsButton = new JButton(MANAGE_TICKETS);
        emailVerificationToggleButton = new JCheckBox(EMAIL_VERIFICATION);

        boolean verifyEmail = mainFrame.getContext()
                .getBean(EmailService.class).isEmailVerificationEnabled();
        emailVerificationToggleButton.setSelected(verifyEmail);

        centerPanel.add(showStatsButton, gbc);
        centerPanel.add(manageUsersButton, gbc);
        centerPanel.add(manageTicketsButton, gbc);
        centerPanel.add(emailVerificationToggleButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton showStatsButton, manageUsersButton, manageTicketsButton;
    private JToggleButton emailVerificationToggleButton;
    private static final String
            ADMINISTRATION_DASHBOARD = "Administration Dashboard",
            SHOW_STATS = "Show Stats",
            MANAGE_USERS = "Manage Users",
            MANAGE_TICKETS = "Manage Tickets",
            EMAIL_VERIFICATION = "Email Verification";
}
