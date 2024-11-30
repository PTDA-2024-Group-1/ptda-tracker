package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ExpenseForm;
import com.ptda.tracker.ui.forms.SubdivisionForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.ptda.tracker.ui.dialogs.SubdivisionsDialog.createSubdivisionsJTable;

public class ExpenseDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private final BudgetAccessService budgetAccessService;
    private Expense expense;
    private final List<Subdivision> subdivisions;
    private final String returnScreen;
    private final Runnable onBack;

    private JLabel nameLabel, amountLabel, categoryLabel, dateLabel, createdByLabel;
    private JTable subdivisionsTable;
    private JButton backButton, editButton, deleteButton, distributeDivisionExpenseButton;

    private static final String
            EXPENSE_DETAILS = "Expense Details",
            NAME = "Name",
            AMOUNT = "Amount",
            CATEGORY = "Category",
            DATE = "Date",
            CREATED_BY = "Created By",
            SUBDIVISIONS = "Subdivisions",
            BACK = "Back",
            EDIT_EXPENSE = "Edit Expense",
            DELETE_EXPENSE = "Delete Expense",
            DISTRIBUTE_SUBDIVISIONS = "Distribute Subdivisions",
            ERROR = "Error",
            SUCCESS = "Success",
            DELETE_CONFIRMATION = "Are you sure you want to delete this expense?",
            DELETE_EXPENSE_TITLE = "Delete Expense",
            EXPENSE_DELETED_SUCCESS = "Expense deleted successfully.",
            DELETE_ERROR_MESSAGE = "An error occurred while deleting the expense",
            EXPENSE_NOT_FOUND = "Expense not found.";

    public ExpenseDetailView(MainFrame mainFrame, Expense expense, String returnScreen, Runnable onBack) {
        this.mainFrame = mainFrame;
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.expense = expense;
        this.subdivisions = mainFrame.getContext().getBean(SubdivisionService.class).getAllByExpenseId(expense.getId());
        this.returnScreen = returnScreen;
        this.onBack = onBack;

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Expense Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(EXPENSE_DETAILS));

        setValues(expense);

        Font font = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(font);
        amountLabel.setFont(font);
        categoryLabel.setFont(font);
        dateLabel.setFont(font);
        createdByLabel.setFont(font);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(amountLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(categoryLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);
        add(detailsPanel, BorderLayout.CENTER);
        // End Expense Details Panel

        // Subdivisions Panel (Only show if subdivisions exist)
        if (!subdivisions.isEmpty()) {
            JPanel subdivisionsPanel = new JPanel();
            subdivisionsPanel.setLayout(new BoxLayout(subdivisionsPanel, BoxLayout.Y_AXIS));
            subdivisionsPanel.setBorder(BorderFactory.createTitledBorder(SUBDIVISIONS));
            subdivisionsTable = createSubdivisionsTable(subdivisions);
            subdivisionsPanel.add(new JScrollPane(subdivisionsTable));
            add(subdivisionsPanel, BorderLayout.EAST);
        }
        // End Subdivisions Panel

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton(BACK);
        buttonsPanel.add(backButton);

        editButton = new JButton(EDIT_EXPENSE);
        buttonsPanel.add(editButton);

        deleteButton = new JButton(DELETE_EXPENSE);
        buttonsPanel.add(deleteButton);

        if (expense.getBudget() != null) {
            User currentUser = UserSession.getInstance().getUser();
            BudgetAccess currentUserAccess = budgetAccessService.getAllByBudgetId(expense.getBudget().getId())
                    .stream()
                    .filter(access -> access.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElse(null);

            if (currentUserAccess == null || currentUserAccess.getAccessLevel() != BudgetAccessLevel.VIEWER) {
                distributeDivisionExpenseButton = new JButton(DISTRIBUTE_SUBDIVISIONS);
                buttonsPanel.add(distributeDivisionExpenseButton);
            }
        }

        add(buttonsPanel, BorderLayout.SOUTH);
        // End Buttons Panel
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
        if (editButton != null) {
            editButton.addActionListener(e -> {
                ExpenseForm expenseForm = new ExpenseForm(mainFrame, expense, mainFrame.getCurrentScreen(), onBack);
                mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, expenseForm);
            });
        }
        if (deleteButton != null) {
            deleteButton.addActionListener(e -> delete());
        }
        if (distributeDivisionExpenseButton != null) {
            distributeDivisionExpenseButton.addActionListener(e -> {
                SubdivisionForm subdivisionForm = new SubdivisionForm(mainFrame, expense, expense.getBudget(), () -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, returnScreen, onBack)));
                mainFrame.registerAndShowScreen(ScreenNames.SUBDIVISION_FORM, subdivisionForm);
            });
        }
    }

    private JTable createSubdivisionsTable(List<Subdivision> subdivisions) {
        return createSubdivisionsJTable(subdivisions);
    }

    private void delete() {
        if (expense.getId() == null) {
            JOptionPane.showMessageDialog(this, EXPENSE_NOT_FOUND, ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                DELETE_CONFIRMATION, DELETE_EXPENSE_TITLE, JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                expenseService.delete(expense.getId());
                JOptionPane.showMessageDialog(this, EXPENSE_DELETED_SUCCESS, SUCCESS, JOptionPane.INFORMATION_MESSAGE);
                if (onBack != null) {
                    onBack.run();
                }
                mainFrame.showScreen(returnScreen);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, DELETE_ERROR_MESSAGE + ": " + ex.getMessage(),
                        ERROR, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setValues(Expense expense) {
        nameLabel = new JLabel(NAME + ": " + expense.getTitle());
        amountLabel = new JLabel(AMOUNT + ": €" + expense.getAmount());
        categoryLabel = new JLabel(CATEGORY + ": " + expense.getCategory());
        dateLabel = new JLabel(DATE + ": " + expense.getDate().toString());
        createdByLabel = new JLabel(CREATED_BY + ": " + expense.getCreatedBy().getName());
    }
}