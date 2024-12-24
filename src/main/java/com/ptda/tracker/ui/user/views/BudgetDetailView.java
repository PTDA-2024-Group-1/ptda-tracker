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
import com.ptda.tracker.ui.user.forms.BudgetForm;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.forms.ShareBudgetForm;
import com.ptda.tracker.ui.user.screens.ExpensesImportScreen;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BudgetDetailView.class);

    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
<<<<<<< HEAD

    private Budget budget;
=======
    private final Budget budget;
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
    private final List<Expense> expenses;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;
    private JPanel paginationPanel;
    private JTable expensesTable;
    private JLabel nameLabel, descriptionLabel, createdByLabel;
    private JButton auditButton, backButton, participantsButton, editButton, shareButton, addExpenseButton, importButton;
    private boolean isReadOnly;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
<<<<<<< HEAD
        this(mainFrame, budget, false); 
=======
        this(mainFrame, budget, false); // Default is not read-only
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
    }

    public BudgetDetailView(MainFrame mainFrame, Budget budget, boolean isReadOnly) {
        this.mainFrame = mainFrame;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
<<<<<<< HEAD

        this.budget = budgetService.getById(budget.getId())
                .orElse(budget); 

        this.expenses = new ArrayList<>();
        this.isReadOnly = isReadOnly;

=======
        this.budget = budget;
        this.expenses = new ArrayList<>();
        this.isReadOnly = isReadOnly;

>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        initComponents();
        setListeners();
        refreshExpenses();

        if (isReadOnly) {
            disableEditingFeatures();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Budget Details"));

        nameLabel = new JLabel("Name: " + budget.getName());
        descriptionLabel = new JLabel("Description: " + budget.getDescription());
        createdByLabel = new JLabel("Created By: " + budget.getCreatedBy().getName());

        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        createdByLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);

        JCheckBox favoriteCheckBox = new JCheckBox("Favorite");
        favoriteCheckBox.setSelected(budget.isFavorite());
        favoriteCheckBox.addActionListener(e -> {
            if (!isReadOnly) {
                budget.setFavorite(favoriteCheckBox.isSelected());
<<<<<<< HEAD
                budgetService.update(budget); 
=======
                budgetService.update(budget);
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
            }
        });
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(favoriteCheckBox);

<<<<<<< HEAD
=======
        // Top Buttons Panel
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        participantsButton = new JButton("Participants");
        topButtonsPanel.add(participantsButton);

<<<<<<< HEAD
        boolean hasOwnerAccess = budgetAccessService.hasAccess(
                budget.getId(),
                UserSession.getInstance().getUser().getId(),
                BudgetAccessLevel.OWNER
        );
        boolean hasEditorAccess = budgetAccessService.hasAccess(
                budget.getId(),
                UserSession.getInstance().getUser().getId(),
                BudgetAccessLevel.EDITOR
        );
=======
        boolean hasOwnerAccess = budgetAccessService.hasAccess(budget.getId(), UserSession.getInstance().getUser().getId(), BudgetAccessLevel.OWNER);
        boolean hasEditorAccess = budgetAccessService.hasAccess(budget.getId(), UserSession.getInstance().getUser().getId(), BudgetAccessLevel.EDITOR);
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66

        if (hasEditorAccess && !isReadOnly) {
            editButton = new JButton("Edit Budget");
            topButtonsPanel.add(editButton);
        }
        if (hasOwnerAccess && !isReadOnly) {
            shareButton = new JButton("Share Budget");
            topButtonsPanel.add(shareButton);
        }

        if (!isReadOnly) {
            importButton = new JButton("Import Expenses");
            topButtonsPanel.add(importButton);
        }

        if (!isReadOnly) {
            auditButton = new JButton("Audit Changes");
            auditButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_AUDIT_DETAIL_VIEW,
                    new BudgetAuditDetailView(mainFrame, budget)
            ));
            topButtonsPanel.add(auditButton);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(detailsPanel, BorderLayout.CENTER);
        topPanel.add(topButtonsPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

<<<<<<< HEAD
=======
        // Expenses Table
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Expenses"));
        expensesTable = new JTable(createExpensesTableModel(expenses));
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

<<<<<<< HEAD
=======
        // Bottom Panel
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back");
        leftButtonPanel.add(backButton);
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (!expenses.isEmpty() && !isReadOnly) {
            JButton statisticsButton = new JButton("Statistics");
            statisticsButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_STATISTICS_VIEW,
                    new BudgetStatisticsView(mainFrame, budget)
            ));
            rightButtonPanel.add(statisticsButton);
        }
<<<<<<< HEAD
=======

>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        if (!expenses.isEmpty() && !isReadOnly) {
            JButton splitSimulation = new JButton("Split Simulation");
            splitSimulation.addActionListener(e -> mainFrame.registerAndShowScreen(
                    ScreenNames.SIMULATE_VIEW,
                    new SimulationView(mainFrame, budget)
            ));
            rightButtonPanel.add(splitSimulation);
        }
<<<<<<< HEAD
=======

>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        if (!isReadOnly && hasEditorAccess) {
            addExpenseButton = new JButton("Add Expense");
            rightButtonPanel.add(addExpenseButton);
        }
<<<<<<< HEAD
=======

>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(paginationPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void disableEditingFeatures() {
        if (editButton != null) editButton.setEnabled(false);
        if (shareButton != null) shareButton.setEnabled(false);
        if (addExpenseButton != null) addExpenseButton.setEnabled(false);
        if (importButton != null) importButton.setEnabled(false);
        if (auditButton != null) auditButton.setEnabled(false);
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));

        participantsButton.addActionListener(e -> {
            ParticipantsDialog participantsDialog = new ParticipantsDialog(mainFrame, budget);
            participantsDialog.setVisible(true);
        });

        if (!isReadOnly) {
            if (editButton != null) {
                editButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                        ScreenNames.BUDGET_FORM,
                        new BudgetForm(mainFrame, null, budget)
                ));
            }
            if (shareButton != null) {
                shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                        ScreenNames.BUDGET_SHARE_FORM,
                        new ShareBudgetForm(mainFrame, budget)));
            }
            if (addExpenseButton != null) {
                addExpenseButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                        ScreenNames.EXPENSE_FORM,
                        new ExpenseForm(mainFrame, null, budget, mainFrame.getCurrentScreen(), this::refreshExpenses)
                ));
            }
            if (importButton != null) {
                importButton.addActionListener(e -> mainFrame.registerAndShowScreen(
                        ScreenNames.EXPENSES_IMPORT_SCREEN,
                        new ExpensesImportScreen(mainFrame, budget, ScreenNames.BUDGET_DETAIL_VIEW, this::refreshExpenses)
                ));
            }
<<<<<<< HEAD

=======
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
            expensesTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = expensesTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Expense selectedExpense = expenses.get(selectedRow);
                        mainFrame.registerAndShowScreen(
                                ScreenNames.EXPENSE_DETAIL_VIEW,
                                new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), this::refreshExpenses)
                        );
                        expensesTable.clearSelection();
                    }
                }
            });
        }
    }

<<<<<<< HEAD
 
=======
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
    private void refreshExpenses() {
        if (isReadOnly) return;
        try {
            int offset = currentPage * PAGE_SIZE;
            expenses.clear();
            expenses.addAll(expenseService.getExpensesByBudgetIdWithPagination(budget.getId(), offset, PAGE_SIZE));
            expensesTable.setModel(createExpensesTableModel(expenses));
            updatePaginationPanel();
        } catch (Exception ex) {
            logger.error("Error refreshing expenses for Budget ID: " + budget.getId(), ex);
<<<<<<< HEAD
            JOptionPane.showMessageDialog(this,
                    "An error occurred while refreshing expenses: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
=======
            JOptionPane.showMessageDialog(this, "An error occurred while refreshing expenses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        }
    }

    private DefaultTableModel createExpensesTableModel(List<Expense> expenses) {
        String[] columnNames = {"Title", "Amount", "Category", "Date", "Created By"};
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

    private void updatePaginationPanel() {
        paginationPanel.removeAll();
        try {
            long totalExpenses = expenseService.getCountByBudgetId(budget.getId());
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
        } catch (Exception ex) {
            logger.error("Error updating pagination for Budget ID: " + budget.getId(), ex);
<<<<<<< HEAD
            JOptionPane.showMessageDialog(this,
                    "An error occurred while updating pagination: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
=======
            JOptionPane.showMessageDialog(this, "An error occurred while updating pagination: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        }
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }
}
