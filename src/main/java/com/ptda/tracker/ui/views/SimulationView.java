package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.UserSession;
import com.ptda.tracker.util.ScreenNames;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SimulationView extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final ExpenseService expenseService;
    private final SubdivisionService subdivisionService;

    public SimulationView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.subdivisionService = mainFrame.getContext().getBean(SubdivisionService.class);

        initComponents();
        displayUserExpenses();
        displayUserRankings();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(BUDGET_SIMULATION, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        //titleLabel.setForeground(new Color(56, 56, 56));
        add(titleLabel, gbc);

        // Search Field Panel
        gbc.gridy = 1;
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JXSearchField searchField = new JXSearchField(SEARCH);
        searchPanel.add(searchField);
        add(searchPanel, gbc);

        // Buttons Panel
        gbc.gridy = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        buttonsPanel.add(cancelButton);

        add(buttonsPanel, gbc);

       // setBackground(new Color(240, 240, 240));

        // Add search functionality
        searchField.addCaretListener(e -> filterTable(searchField.getText()));
    }

    private void displayUserExpenses() {
        User currentUser = UserSession.getInstance().getUser();
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());

        // Table columns
        String[] columnNames = {"Expense", "Amount", "User Paid", "Percentage"};
        expenseTableModel = new DefaultTableModel(columnNames, 0);

        double totalAmount = 0;

        for (Expense expense : expenses) {
            List<Subdivision> subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
            for (Subdivision subdivision : subdivisions) {
                if (subdivision.getUser().getId().equals(currentUser.getId())) {
                    double userAmount = expense.getAmount() * (subdivision.getPercentage() / 100);
                    totalAmount += userAmount;

                    // Add row to the table
                    expenseTableModel.addRow(new Object[]{
                            expense.getTitle(),
                            "€" + String.format("%.2f", expense.getAmount()),
                            "€" + String.format("%.2f", userAmount),
                            String.format("%.2f", subdivision.getPercentage()) + "%"
                    });
                }
            }
        }

        // Create table with the model
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setDefaultEditor(Object.class, null);  // Disable editing

        // Add title above the table
        JLabel expenseTableTitle = new JLabel(MY_EXPENSES);
        expenseTableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 15, 5, 15);
        add(expenseTableTitle, gbc);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 15, 15, 15);
        add(scrollPane, gbc);

        // Add total amount label below the table
        JLabel totalAmountLabel = new JLabel(TOTAL_AMOUNT_PAID + ": €" + String.format("%.2f", totalAmount));
        gbc.gridy = 5;
        add(totalAmountLabel, gbc);
    }

    private void displayUserRankings() {
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

        // Table columns
        String[] columnNames = {"User", "Total Paid"};
        rankingTableModel = new DefaultTableModel(columnNames, 0);

        double totalBudgetAmount = 0;

        for (Map.Entry<User, Double> entry : userPayments.entrySet()) {
            User user = entry.getKey();
            double totalPaid = entry.getValue();
            totalBudgetAmount += totalPaid;

            // Add row to the table
            rankingTableModel.addRow(new Object[]{
                    user.getName(),
                    "€" + String.format("%.2f", totalPaid)
            });
        }

        // Create table with the model
        rankingTable = new JTable(rankingTableModel);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setDefaultEditor(Object.class, null);  // Disable editing

        // Sort the table by the "Total Paid" column in descending order
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(rankingTableModel);
        rankingTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        // Add title above the table
        JLabel rankingTableTitle = new JLabel(ALL_USERS_TOTAL_EXPENSES);
        rankingTableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 15, 5, 15);
        add(rankingTableTitle, gbc);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 15, 15, 15);
        add(scrollPane, gbc);

        // Add total budget amount label below the table
        JLabel totalBudgetAmountLabel = new JLabel(TOTAL_BUDGET_AMOUNT + ": €" + String.format("%.2f", totalBudgetAmount));
        gbc.gridy = 5;
        add(totalBudgetAmountLabel, gbc);
    }

    private void filterTable(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(expenseTableModel);
        expenseTable.setRowSorter(sorter);

        // Filter the table rows based on the query
        RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + query); // case-insensitive filter
        sorter.setRowFilter(rowFilter);
    }

    private JTable expenseTable;
    private JTable rankingTable;
    private DefaultTableModel expenseTableModel;
    private DefaultTableModel rankingTableModel;

    private static final String
            BUDGET_SIMULATION = "Budget Simulation",
            SEARCH = "Search",
            CANCEL = "Cancel",
            MY_EXPENSES = "My Expenses",
            TOTAL_AMOUNT_PAID = "Total Amount Paid",
            ALL_USERS_TOTAL_EXPENSES = "All Users - Total Expenses",
            TOTAL_BUDGET_AMOUNT = "Total Budget Amount";
}