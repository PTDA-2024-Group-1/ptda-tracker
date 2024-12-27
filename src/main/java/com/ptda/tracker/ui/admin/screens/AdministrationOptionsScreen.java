package com.ptda.tracker.ui.admin.screens;

import com.ptda.tracker.models.admin.GlobalVariableName;
import com.ptda.tracker.services.administration.GlobalVariableService;
import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.services.email.EmailService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.admin.views.ManageTicketView;
import com.ptda.tracker.ui.admin.views.ManageUserView;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

import static com.ptda.tracker.config.AppConfig.*;

public class AdministrationOptionsScreen extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final UserService userService;
    private final TicketService ticketService;
    private final GlobalVariableService globalVariableService;

    public AdministrationOptionsScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.userService = mainFrame.getContext().getBean(UserService.class);
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);
        this.globalVariableService = mainFrame.getContext().getBean(GlobalVariableService.class);
        initComponents();
        setListeners();
        showStats();
    }

    private void setListeners() {
        loadData.addActionListener(e -> refreshData());
        manageTicketsButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(
                    ScreenNames.MANAGE_TICKET_VIEW,
                    new ManageTicketView(mainFrame)
            );
        });
        manageUsersButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(
                    ScreenNames.MANAGE_USER_VIEW,
                    new ManageUserView(mainFrame, this::refreshData)
            );
        });
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Administration Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // Statistics Panel
        statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        add(statsPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        loadData = new JButton("Load Data");
        manageUsersButton = new JButton("Manage Users");
        manageTicketsButton = new JButton("Manage Tickets");

        buttons.add(loadData);
        buttons.add(manageUsersButton);
        buttons.add(manageTicketsButton);

        // Email Verification Checkbox
        emailVerificationToggleButton = new JCheckBox("Email Verification");
        emailVerificationToggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        boolean verifyEmail = mainFrame.getContext().getBean(EmailService.class).isEmailVerificationEnabled();
        emailVerificationToggleButton.setSelected(verifyEmail);
        emailVerificationToggleButton.addActionListener(e -> {
            boolean isEmailVerified = emailVerificationToggleButton.isSelected();
            globalVariableService.set(GlobalVariableName.VERIFY_EMAIL, String.valueOf(isEmailVerified));
        });

        buttons.add(emailVerificationToggleButton);

        buttonPanel.add(buttons, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showStats() {
        // Load and resize icons
        int iconSize = 50;
        ImageIcon budgetIcon = resizeIcon(new ImageIcon(BUDGET_ICON_PATH), iconSize, iconSize);
        ImageIcon expenseIcon = resizeIcon(new ImageIcon(EXPENSE_ICON_PATH), iconSize, iconSize);
        ImageIcon userIcon = resizeIcon(new ImageIcon(USERS_ICON_PATH), iconSize, iconSize);
        ImageIcon assistantIcon = resizeIcon(new ImageIcon(ASSISTANT_ICON_PATH), iconSize, iconSize);
        ImageIcon adminIcon = resizeIcon(new ImageIcon(ADMIN_ICON_PATH), iconSize, iconSize);
        ImageIcon ticketIcon = resizeIcon(new ImageIcon(TICKETS_ICON_PATH), iconSize, iconSize);

        // Create labels with resized icons
        budgetsLabel = createStatLabel(budgetIcon, "Total Budgets:");
        expensesLabel = createStatLabel(expenseIcon, "Total Expenses:");
        usersLabel = createStatLabel(userIcon, "Users:");
        assistantsLabel = createStatLabel(assistantIcon, "Assistants:");
        adminsLabel = createStatLabel(adminIcon, "Admins:");
        ticketsLabel = createStatLabel(ticketIcon, "Total Tickets:");

        statsPanel.add(budgetsLabel);
        statsPanel.add(expensesLabel);
        statsPanel.add(ticketsLabel);
        statsPanel.add(usersLabel);
        statsPanel.add(assistantsLabel);
        statsPanel.add(adminsLabel);
    }

    private JLabel createStatLabel(ImageIcon icon, String text) {
        JLabel label = new JLabel(text, icon, SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        return label;
    }

    public void refreshData() {
        updateStatistics();
    }

    public void updateStatistics() {
        budgetsLabel.setText("Total Budgets: " + budgetService.getAll().size());
        expensesLabel.setText("Total Expenses: " + expenseService.getAll().size());
        usersLabel.setText("Users: " + userService.countByUserType("USER"));
        assistantsLabel.setText("Assistants: " + userService.countByUserType("ASSISTANT"));
        adminsLabel.setText("Admins: " + userService.countByUserType("ADMIN"));
        ticketsLabel.setText("Total Tickets: " + ticketService.getAll().size());
    }

    private JLabel budgetsLabel, expensesLabel, usersLabel, assistantsLabel, adminsLabel, ticketsLabel;
    private JButton loadData, manageUsersButton, manageTicketsButton;
    private JToggleButton emailVerificationToggleButton;
    private JPanel statsPanel;
}