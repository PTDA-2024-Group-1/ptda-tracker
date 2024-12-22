package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.ParticipantsDialog;
import com.ptda.tracker.ui.user.forms.*;
import com.ptda.tracker.ui.user.screens.ExpensesImportScreen;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final User user = UserSession.getInstance().getUser();
    private final BudgetService budgetService;
    private final Budget budget;
    private final List<Expense> expenses;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;
    private JPanel paginationPanel;
    private JTable expensesTable;
    JLabel nameLabel, descriptionLabel, createdByLabel;
    private JButton auditButton, backButton, participantsButton, editButton, shareButton, addExpenseButton, importButton;
    private static final String
            BUDGET_DETAILS = "Budget Details",
            NAME = "Name",
            DESCRIPTION = "Description",
            CREATED_BY = "Created By",
            EXPENSES = "Expenses",
            BACK = "Back",
            PARTICIPANTS = "Participants",
            EDIT_BUDGET = "Edit Budget",
            STATISTICS = "Statistics",
            ADD_EXPENSE = "Add Expense",
            SHARE_BUDGET = "Share Budget",
            SPLIT_SIMULATION = "Split Simulation",
            TITLE = "Title",
            AMOUNT = "Amount",
            CATEGORY = "Category",
            DATE = "Date",
            CREATED_BY_COLUMN = "Created By";

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.budget = budgetService.getById(budget.getId()).orElseThrow(() -> new IllegalArgumentException("Budget not found")); // Fetch the latest budget from DB
        this.expenses = new ArrayList<>(); // Initialize as empty list

        initComponents();    // Initialize UI components first
        setListeners();      // Set up event listeners
        refreshExpenses();   // Now safely refresh expenses
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        participantsButton.addActionListener(e -> {
            ParticipantsDialog participantsDialog = new ParticipantsDialog(mainFrame, budget);
            participantsDialog.setVisible(true);
        });
        if (editButton != null) {
            editButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_FORM,
                    new BudgetForm(mainFrame, null, budget)
            ));
        }
        if (shareButton != null) {
            shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_SHARE_FORM, new ShareBudgetForm(mainFrame, budget)));
        }
        if (addExpenseButton != null) {
            addExpenseButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.EXPENSE_FORM,
                    new ExpenseForm(
                            mainFrame,
                            null,
                            budget,
                            mainFrame.getCurrentScreen(),
                            this::refreshExpenses
                    )
            ));
        }
        importButton.addActionListener(e -> {
            if (mainFrame.getScreen(ScreenNames.EXPENSES_IMPORT_SCREEN) == null) {
                mainFrame.registerAndShowScreen(ScreenNames.EXPENSES_IMPORT_SCREEN,
                        new ExpensesImportScreen(mainFrame, budget,
                                ScreenNames.BUDGET_DETAIL_VIEW,
                                this::refreshExpenses));
            } else {
                mainFrame.showScreen(ScreenNames.EXPENSES_IMPORT_SCREEN);
            }
        });
        expensesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = expensesTable.getSelectedRow();
                if (selectedRow != -1) {
                    Expense selectedExpense = expenses.get(selectedRow);
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW,
                            new ExpenseDetailView(mainFrame, selectedExpense,
                                    mainFrame.getCurrentScreen(), this::refreshExpenses
                            )
                    );
                    expensesTable.clearSelection();
                }
            }
        });
    }

    /**
     * Re-fetches expenses and updates the table.
     */
    private void refreshExpenses() {
        try {
            int offset = currentPage * PAGE_SIZE;
            expenses.clear();
            expenses.addAll(mainFrame.getContext().getBean(ExpenseService.class).getExpensesByBudgetIdWithPagination(budget.getId(), offset, PAGE_SIZE));

            if (expensesTable != null) { // Defensive check
                expensesTable.setModel(createExpensesTableModel(expenses));
                updatePaginationPanel();
            } else {
                // Log or handle the scenario where expensesTable is still null
                JOptionPane.showMessageDialog(
                        this,
                        "Expenses table is not initialized.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            // Optionally log the exception
            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred while refreshing expenses: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updatePaginationPanel() {
        paginationPanel.removeAll();
        long totalExpenses = mainFrame.getContext().getBean(ExpenseService.class).getCountByBudgetId(budget.getId());
        int totalPages = (int) Math.ceil((double) totalExpenses / PAGE_SIZE);
        if (totalPages > 1) {
            for (int i = 0; i < totalPages; i++) {
                int pageIndex = i;
                JButton pageButton = new JButton(String.valueOf(i + 1));
                pageButton.addActionListener(e -> {
                    currentPage = pageIndex;
                    refreshExpenses();
                });
                paginationPanel.add(pageButton);
            }
        }
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private DefaultTableModel createExpensesTableModel(List<Expense> expenses) {
        String[] columnNames = {TITLE, AMOUNT, CATEGORY, DATE, CREATED_BY_COLUMN};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Expense expense : expenses) {
            model.addRow(new Object[]{
                    expense.getTitle(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getDate(),
                    expense.getCreatedBy().getName()});
        }
        return model;
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel for Budget Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BUDGET_DETAILS));

        nameLabel = new JLabel(NAME + ": " + budget.getName());
        descriptionLabel = new JLabel(DESCRIPTION + ": " + budget.getDescription());
        createdByLabel = new JLabel(CREATED_BY + ": " + budget.getCreatedBy().getName());

        Font font = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        createdByLabel.setFont(font);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);

        // Checkbox for favorites
        JCheckBox favoriteCheckBox = new JCheckBox("Favorite");
        favoriteCheckBox.setSelected(budget.isFavorite());
        favoriteCheckBox.addActionListener(e -> {
            budget.setFavorite(favoriteCheckBox.isSelected());
            mainFrame.getContext().getBean(BudgetService.class).update(budget);
            // Optionally, notify user of success
        });
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(favoriteCheckBox);

        // Top buttons panel
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        participantsButton = new JButton(PARTICIPANTS);
        topButtonsPanel.add(participantsButton);

        boolean hasOwnerAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.OWNER);
        boolean hasEditorAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.EDITOR);

        if (hasEditorAccess) {
            editButton = new JButton(EDIT_BUDGET);
            topButtonsPanel.add(editButton);
        }
        if (hasOwnerAccess) {
            shareButton = new JButton(SHARE_BUDGET);
            topButtonsPanel.add(shareButton);
        }

        importButton = new JButton("Import Expenses");
        topButtonsPanel.add(importButton);

        // Audit button: Navigate to BudgetAuditDetailView
        auditButton = new JButton("Audit Changes");
        auditButton.addActionListener(e ->
                mainFrame.registerAndShowScreen(
                        ScreenNames.BUDGET_AUDIT_DETAIL_VIEW,
                        new BudgetAuditDetailView(mainFrame, budget)
                )
        );
        topButtonsPanel.add(auditButton);

        // Combine details and top buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(detailsPanel, BorderLayout.CENTER);
        topPanel.add(topButtonsPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for Expenses table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(EXPENSES));
        expensesTable = new JTable(createExpensesTableModel(expenses));
        expensesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for Back, pagination, and optional buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Left button panel (Back)
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);

        // Right button panel (Statistics, Simulation, Add Expense)
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (!expenses.isEmpty()) {
            // Add Statistics button
            JButton statisticsButton = new JButton(STATISTICS);
            statisticsButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_STATISTICS_VIEW,
                    new BudgetStatisticsView(mainFrame, budget)
            ));
            rightButtonPanel.add(statisticsButton);
        }
        if (!expenses.isEmpty()) {
            JButton splitSimulation = new JButton(SPLIT_SIMULATION);
            splitSimulation.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.SIMULATE_VIEW,
                    new SimulationView(mainFrame, budget)
            ));
            rightButtonPanel.add(splitSimulation);
        }
        if (hasEditorAccess) {
            addExpenseButton = new JButton(ADD_EXPENSE);
            rightButtonPanel.add(addExpenseButton);
        }
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

        // Pagination panel in the center
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(paginationPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
