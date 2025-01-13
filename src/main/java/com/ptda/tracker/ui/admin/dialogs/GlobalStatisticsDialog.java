package com.ptda.tracker.ui.admin.dialogs;

import com.ptda.tracker.services.assistance.TicketService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.user.UserService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.awt.*;

import static com.ptda.tracker.config.AppConfig.*;
import static com.ptda.tracker.config.AppConfig.TICKETS_ICON_PATH;

public class GlobalStatisticsDialog extends JDialog {
    private final MainFrame mainFrame;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final UserService userService;
    private final TicketService ticketService;

    public GlobalStatisticsDialog(MainFrame mainFrame) {
        super(mainFrame, GLOBAL_STATISTICS, true);
        this.mainFrame = mainFrame;
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.userService = mainFrame.getContext().getBean(UserService.class);
        this.ticketService = mainFrame.getContext().getBean(TicketService.class);

        initComponents();
        updateStatistics();
        setListeners();

        pack();
        setLocationRelativeTo(null);
    }

    private void setListeners() {
        refreshDataButton.addActionListener(e -> updateStatistics());
        closeButton.addActionListener(e -> dispose());
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JLabel createStatLabel(ImageIcon icon, String text) {
        JLabel label = new JLabel(text, icon, SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        return label;
    }

    public void updateStatistics() {
        budgetsLabel.setText(TOTAL_BUDGETS + ": " + budgetService.getCount());
        expensesLabel.setText(TOTAL_EXPENSES + ": " + expenseService.getCount());
        usersLabel.setText(USERS + ": " + userService.countByUserType("USER"));
        assistantsLabel.setText(ASSISTANTS + ": " + userService.countByUserType("ASSISTANT"));
        adminsLabel.setText(ADMINS + ": " + userService.countByUserType("ADMIN"));
        ticketsLabel.setText(TOTAL_TICKETS + ": " + ticketService.getAll().size());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
         //setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statsPanel, BorderLayout.CENTER);

        // Load and resize icons
        int iconSize = 50;
        ImageIcon budgetIcon = resizeIcon(new ImageIcon(BUDGET_ICON_PATH), iconSize, iconSize);
        ImageIcon expenseIcon = resizeIcon(new ImageIcon(EXPENSE_ICON_PATH), iconSize, iconSize);
        ImageIcon userIcon = resizeIcon(new ImageIcon(USERS_ICON_PATH), iconSize, iconSize);
        ImageIcon assistantIcon = resizeIcon(new ImageIcon(ASSISTANT_ICON_PATH), iconSize, iconSize);
        ImageIcon adminIcon = resizeIcon(new ImageIcon(ADMIN_ICON_PATH), iconSize, iconSize);
        ImageIcon ticketIcon = resizeIcon(new ImageIcon(TICKETS_ICON_PATH), iconSize, iconSize);

        // Create labels with resized icons
        budgetsLabel = createStatLabel(budgetIcon, TOTAL_BUDGETS);
        expensesLabel = createStatLabel(expenseIcon, TOTAL_EXPENSES);
        usersLabel = createStatLabel(userIcon, USERS);
        assistantsLabel = createStatLabel(assistantIcon, ASSISTANTS);
        adminsLabel = createStatLabel(adminIcon, ADMINS);
        ticketsLabel = createStatLabel(ticketIcon, TOTAL_TICKETS);

        statsPanel.add(budgetsLabel);
        statsPanel.add(expensesLabel);
        statsPanel.add(ticketsLabel);
        statsPanel.add(usersLabel);
        statsPanel.add(assistantsLabel);
        statsPanel.add(adminsLabel);
        add(statsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshDataButton = new JButton(REFRESH_DATA);
        closeButton = new JButton(CLOSE);
        buttonPanel.add(refreshDataButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel budgetsLabel, expensesLabel, usersLabel, assistantsLabel, adminsLabel, ticketsLabel;
    private JButton refreshDataButton, closeButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            GLOBAL_STATISTICS = localeManager.getTranslation("global.statistics"),
            TOTAL_BUDGETS = localeManager.getTranslation("total.budgets"),
            TOTAL_EXPENSES = localeManager.getTranslation("total.expenses"),
            USERS = localeManager.getTranslation("users"),
            ASSISTANTS = localeManager.getTranslation("assistants"),
            ADMINS = localeManager.getTranslation("admins"),
            TOTAL_TICKETS = localeManager.getTranslation("total.tickets"),
            REFRESH_DATA = localeManager.getTranslation("refresh.data"),
            CLOSE = localeManager.getTranslation("close");
}
