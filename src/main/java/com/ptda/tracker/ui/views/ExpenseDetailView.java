package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.DistributeExpenseForm;
import com.ptda.tracker.ui.forms.ExpenseForm;
import com.ptda.tracker.ui.screens.NavigationScreen;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.ptda.tracker.ui.dialogs.SubdivisionsDialog.createSubdivisionsJTable;

public class ExpenseDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private Expense expense;
    private final List<Subdivision> subdivisions;
    private final String returnScreen;
    private final Runnable onBack;

    private JLabel nameLabel, amountLabel, categoryLabel, dateLabel, createdByLabel;
    private JTable subdivisionsTable;
    private JButton backButton, editButton, deleteButton, createSubdivisionButton;

    public ExpenseDetailView(MainFrame mainFrame, Expense expense, String returnScreen, Runnable onBack) {
        this.mainFrame = mainFrame;
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.expense = expense;
        subdivisions = mainFrame.getContext().getBean(SubdivisionService.class).getAllByExpenseId(expense.getId());
        this.returnScreen = returnScreen;
        this.onBack = onBack;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Expense Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Expense Details"));

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

        // Subdivisions Panel
        if (!subdivisions.isEmpty()) {
            JPanel subdivisionsPanel = new JPanel();
            subdivisionsPanel.setLayout(new BoxLayout(subdivisionsPanel, BoxLayout.Y_AXIS));
            subdivisionsPanel.setBorder(BorderFactory.createTitledBorder("Subdivisions"));
            subdivisionsTable = createSubdivisionsTable(subdivisions);
            subdivisionsPanel.add(new JScrollPane(subdivisionsTable));
            add(subdivisionsPanel, BorderLayout.EAST);
        }
        // End Subdivisions Panel

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
        buttonsPanel.add(backButton);

        editButton = new JButton("Edit Expense");
        ExpenseForm expenseForm = new ExpenseForm(mainFrame, expense, mainFrame.getCurrentScreen(), this::returnToThisScreen);
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, expenseForm));
        buttonsPanel.add(editButton);

        deleteButton = new JButton("Delete Expense");
        deleteButton.addActionListener(e -> delete());
        buttonsPanel.add(deleteButton);

        createSubdivisionButton = new JButton("Create Subdivision");
        buttonsPanel.add(createSubdivisionButton);

        add(buttonsPanel, BorderLayout.SOUTH);
        // End Buttons Panel
    }

    private void setListeners() {
        // TODO: Implement createSubdivisionButton listener
    }

    private JTable createSubdivisionsTable(List<Subdivision> subdivisions) {
        return createSubdivisionsJTable(subdivisions);
    }

    private void delete() {
        if (expense.getId() == null) {
            JOptionPane.showMessageDialog(this, "The expense ID is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?", "Delete Expense", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                expenseService.delete(expense.getId());
                mainFrame.showScreen(returnScreen);
                onBack.run();
                JOptionPane.showMessageDialog(this, "Expense deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while deleting the expense: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnToThisScreen() {
        Optional<Expense> optionalExpense = expenseService.getById(expense.getId());
        if (optionalExpense.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The expense ID is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            expense = optionalExpense.get();
            setValues(expense);
            mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW);
        }
    }

    private void setValues(Expense expense) {
        nameLabel = new JLabel("Name: " + expense.getTitle());
        amountLabel = new JLabel("Amount: €" + expense.getAmount());
        categoryLabel = new JLabel("Category: " + expense.getCategory());
        dateLabel = new JLabel("Date: " + expense.getDate().toString());
        createdByLabel = new JLabel("Created By: " + expense.getCreatedBy().getName());
    }
}