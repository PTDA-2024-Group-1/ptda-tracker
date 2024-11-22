package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ExpenseForm;
import com.ptda.tracker.ui.screens.ExpensesScreen;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static com.ptda.tracker.ui.views.BudgetDetailView.createStyledButton;

public class ExpenseDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final ExpenseService expenseService;
    private final Runnable refreshExpensesList;
    private Expense expense;

    private JLabel nameLabel, amountLabel, categoryLabel, dateLabel, createdByLabel;
    private JButton backButton, editButton, deleteButton;

    public ExpenseDetailView(MainFrame mainFrame, Runnable refreshExpensesList, Expense expense) {
        this.mainFrame = mainFrame;
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.refreshExpensesList = refreshExpensesList;
        this.expense = expense;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de detalhes da despesa
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

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botões com estilo e hover
        backButton = createStyledButton("Back to Expenses");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonsPanel.add(backButton);

        editButton = createStyledButton("Edit Expense");
        ExpenseForm expenseForm = new ExpenseForm(mainFrame, this::returnToThisScreen, expense);
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, expenseForm));
        buttonsPanel.add(editButton);

        deleteButton = createStyledButton("Delete Expense");
        deleteButton.addActionListener(e -> delete());

        buttonsPanel.add(deleteButton);

        add(buttonsPanel, BorderLayout.SOUTH);
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
                refreshExpensesList.run();
                mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
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
        nameLabel = new JLabel("Name: " + expense.getName());
        amountLabel = new JLabel("Amount: €" + expense.getAmount());
        categoryLabel = new JLabel("Category: " + expense.getCategory());
        dateLabel = new JLabel("Date: " + expense.getDate().toString());
        createdByLabel = new JLabel("Created By: " + expense.getCreatedBy().getName());
    }
}

