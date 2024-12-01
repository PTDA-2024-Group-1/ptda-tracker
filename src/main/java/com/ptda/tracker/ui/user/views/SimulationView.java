package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.Subdivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationView extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final ExpenseService expenseService;
    private final SubdivisionService subdivisionService;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable rankingTable;
    private DefaultTableModel rankingTableModel;
    private JButton backButton;

    private static final String
            BUDGET_SIMULATION = "Budget Simulation",
            BACK = "Back",
            ALL_USERS_TOTAL_EXPENSES = "All Users - Total Expenses";

    public SimulationView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.subdivisionService = mainFrame.getContext().getBean(SubdivisionService.class);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        backButton = new JButton(BACK);
        backButton.addActionListener(e -> {
            if (getCurrentPanelName().equals("Expenses")) {
                cardLayout.show(mainPanel, "Rankings");
            } else {
                mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW);
            }
        });

        JLabel titleLabel = new JLabel(BUDGET_SIMULATION, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createRankingsPanel(), "Rankings");
        mainPanel.add(new ExpenseDetailsView(expenseService, subdivisionService, budget.getId()), "Expenses");

        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private String getCurrentPanelName() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == mainPanel.getComponent(0)) {
                    return "Rankings";
                } else if (comp == mainPanel.getComponent(1)) {
                    return "Expenses";
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

        rankingTableModel = new DefaultTableModel(new String[]{"User", "Total Paid"}, 0);
        populateRankingsTable();
        rankingTable = new JTable(rankingTableModel);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        rankingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && rankingTable.getSelectedRow() != -1) {
                String selectedUserName = rankingTableModel.getValueAt(rankingTable.getSelectedRow(), 0).toString();
                User selectedUser = getUserByName(selectedUserName);

                // Passar o usuário selecionado para o painel de detalhes
                ExpenseDetailsView expenseDetailsView = (ExpenseDetailsView) mainPanel.getComponent(1);
                expenseDetailsView.setSelectedUser(selectedUser);
                cardLayout.show(mainPanel, "Expenses");
            }
        });


        return panel;
    }


    private void populateRankingsTable() {
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
        Map<User, Double> userPayments = new HashMap<>();

        for (Expense expense : expenses) {
            List<Subdivision> subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
            for (Subdivision subdivision : subdivisions) {
                User user = subdivision.getUser();
                double userAmount = expense.getAmount() * (subdivision.getPercentage() / 100);
                userPayments.put(user, userPayments.getOrDefault(user, 0.0) + userAmount);
            }
        }

        rankingTableModel.setRowCount(0);
        for (Map.Entry<User, Double> entry : userPayments.entrySet()) {
            User user = entry.getKey();
            double totalPaid = entry.getValue();
            rankingTableModel.addRow(new Object[]{user.getName(), "€" + String.format("%.2f", totalPaid)});
        }
    }

    private User getUserByName(String name) {
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
        for (Expense expense : expenses) {
            List<Subdivision> subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
            for (Subdivision subdivision : subdivisions) {
                if (subdivision.getUser().getName().equals(name)) {
                    return subdivision.getUser();
                }
            }
        }
        return null;
    }
}