package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.*;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetSplitService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SimulationView extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final ExpenseService expenseService;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseDivisionService expenseDivisionService;
    private final BudgetSplitService budgetSplitService;

    public SimulationView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.budgetSplitService = mainFrame.getContext().getBean(BudgetSplitService.class);

        initComponents();
        populateRankingsTable();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> {
            if (getCurrentPanelName().equals(EXPENSES)) {
                cardLayout.show(mainPanel, RANKINGS);
            } else {
                mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW);
            }
        });
    }

    private void populateRankingsTable() {
        List<BudgetSplit> budgetSplits = budgetSplitService.getAllByBudgetId(budget.getId());
        if (budgetSplits.isEmpty() || budgetSplits.getFirst().getCreatedAt() <= budget.getUpdatedAt()) {
            budgetSplits = budgetSplitService.split(budget.getId());
        }
        rankingTableModel.setRowCount(0); // Clear existing rows

        for (BudgetSplit budgetSplit : budgetSplits) {
            User user = budgetSplit.getUser();
            BigDecimal paid = BigDecimal.valueOf(budgetSplit.getPaidAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal toPay = BigDecimal.valueOf(budgetSplit.getAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal balance = toPay.subtract(paid).setScale(2, RoundingMode.HALF_UP).negate(); // Invert balance

            rankingTableModel.addRow(new Object[]{
                    user.getName(),
                    toPay.doubleValue(),
                    paid.doubleValue(),
                    balance.doubleValue()
            });
        }
    }

    private User getUserByName(String name) {
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
        for (Expense expense : expenses) {
            List<ExpenseDivision> expenseDivisions = expenseDivisionService.getAllByExpenseId(expense.getId());
            for (ExpenseDivision expenseDivision : expenseDivisions) {
                if (expenseDivision.getUser().getName().equals(name)) {
                    return expenseDivision.getUser();
                }
            }
        }
        return null;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(SPLIT_SIMULATION, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createRankingsPanel(), RANKINGS);
        mainPanel.add(new ExpenseDetailsView(expenseService, expenseDivisionService, budgetAccessService, budget.getId()), EXPENSES);

        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(BACK);
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private String getCurrentPanelName() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == mainPanel.getComponent(0)) {
                    return RANKINGS;
                } else if (comp == mainPanel.getComponent(1)) {
                    return EXPENSES;
                }
            }
        }
        return "";
    }

    private JPanel createRankingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel rankingsTitle = new JLabel(ALL_USERS_TOTAL_EXPENSES, SwingConstants.LEFT);
        rankingsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(rankingsTitle, BorderLayout.NORTH);

        rankingTableModel = new DefaultTableModel(new String[]{USER, TO_PAY, PAID, BALANCE}, 0);
        rankingTable = new JTable(rankingTableModel);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        rankingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && rankingTable.getSelectedRow() != -1) {
                String selectedUserName = rankingTableModel.getValueAt(rankingTable.getSelectedRow(), 0).toString();
                User selectedUser = getUserByName(selectedUserName);

                ExpenseDetailsView expenseDetailsView = (ExpenseDetailsView) mainPanel.getComponent(1);
                expenseDetailsView.setSelectedUser(selectedUser);
                cardLayout.show(mainPanel, EXPENSES);
            }
        });
        return panel;
    }

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable rankingTable;
    private DefaultTableModel rankingTableModel;
    private JButton backButton;

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            EXPENSES = localeManager.getTranslation("expenses"),
            RANKINGS = localeManager.getTranslation("rankings"),
            USER = localeManager.getTranslation("user"),
            PAID = localeManager.getTranslation("paid"),
            TO_PAY = localeManager.getTranslation("to_pay"),
            BALANCE = localeManager.getTranslation("balance"),
            SPLIT_SIMULATION = localeManager.getTranslation("split_simulation"),
            BACK = localeManager.getTranslation("back"),
            ALL_USERS_TOTAL_EXPENSES = localeManager.getTranslation("all_users_total_expenses");
}