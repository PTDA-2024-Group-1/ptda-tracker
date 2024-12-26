package com.ptda.tracker.ui.admin.screens;

import com.ptda.tracker.models.admin.GlobalVariableName;
import com.ptda.tracker.services.administration.AdminService;
import com.ptda.tracker.services.administration.GlobalVariableService;
import com.ptda.tracker.services.assistance.AssistantService;
import com.ptda.tracker.services.assistance.TicketService;
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
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            refreshData();
        }
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

        JButton manageUsersButton = new JButton("Manage Users");
        manageUsersButton.setFont(new Font("Arial", Font.BOLD, 14));
        manageUsersButton.addActionListener(e -> {
            AdministrationOptionsScreen adminOptionsScreen = this;
            mainFrame.registerAndShowScreen(ScreenNames.MANAGE_USER_VIEW, new ManageUserView(mainFrame, adminOptionsScreen));
        });

        JButton manageTicketsButton = new JButton("Manage Tickets");
        manageTicketsButton.setFont(new Font("Arial", Font.BOLD, 14));
        manageTicketsButton.addActionListener(e -> {
            AdministrationOptionsScreen adminOptionsScreen = this;
            mainFrame.registerAndShowScreen(ScreenNames.MANAGE_TICKET_VIEW, new ManageTicketView(mainFrame));
        });

        buttons.add(manageUsersButton);
        buttons.add(manageTicketsButton);

        // Email Verification Checkbox
        emailVerificationCheckbox = new JCheckBox("Email Verification");
        emailVerificationCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        String verifyEmail = globalVariableService.get(GlobalVariableName.VERIFY_EMAIL.toString()) != null ? globalVariableService.get(GlobalVariableName.VERIFY_EMAIL.toString()) : "false";
        emailVerificationCheckbox.setSelected(Boolean.parseBoolean(verifyEmail));
        emailVerificationCheckbox.addActionListener(e -> {
            boolean isEmailVerified = emailVerificationCheckbox.isSelected();
            globalVariableService.set(GlobalVariableName.VERIFY_EMAIL, String.valueOf(isEmailVerified));
        });

        buttons.add(emailVerificationCheckbox);

        buttonPanel.add(buttons, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshData();
    }

    private void showStats() {
        // Load and resize icons
        ImageIcon budgetIcon = resizeIcon(new ImageIcon(BUDGET_ICON_PATH), 50, 50);
        ImageIcon expenseIcon = resizeIcon(new ImageIcon(EXPENSE_ICON_PATH), 50, 50);
        ImageIcon userIcon = resizeIcon(new ImageIcon(USERS_ICON_PATH), 50, 50);
        ImageIcon assistantIcon = resizeIcon(new ImageIcon(ASSISTANT_ICON_PATH), 50, 50);
        ImageIcon adminIcon = resizeIcon(new ImageIcon(ADMIN_ICON_PATH), 50, 50);
        ImageIcon ticketIcon = resizeIcon(new ImageIcon(TICKETS_ICON_PATH), 50, 50);

        // Create labels with resized icons
        budgetsLabel = createStatLabel(budgetIcon, "Total Budgets: 0");
        expensesLabel = createStatLabel(expenseIcon, "Total Expenses: 0");
        usersLabel = createStatLabel(userIcon, "Users: 0");
        assistantsLabel = createStatLabel(assistantIcon, "Assistants: 0");
        adminsLabel = createStatLabel(adminIcon, "Admins: 0");
        ticketsLabel = createStatLabel(ticketIcon, "Total Tickets: 0");

        statsPanel.add(budgetsLabel);
        statsPanel.add(expensesLabel);
        statsPanel.add(ticketsLabel);
        statsPanel.add(usersLabel);
        statsPanel.add(assistantsLabel);
        statsPanel.add(adminsLabel);

        updateStatistics();
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
    private JCheckBox emailVerificationCheckbox;
    private JPanel statsPanel;
}