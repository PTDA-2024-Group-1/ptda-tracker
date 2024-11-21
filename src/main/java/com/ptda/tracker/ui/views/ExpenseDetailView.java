package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ExpenseForm;
import com.ptda.tracker.ui.screens.ExpensesScreen;
import com.ptda.tracker.ui.screens.NavigationScreen;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;

// TO - DO - melhorar a logica de delete e edição

public class ExpenseDetailView extends JPanel {
    private final ExpenseService expenseService;

    public ExpenseDetailView(MainFrame mainFrame, Expense expense) {
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de detalhes da despesa
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Expense Details"));

        JLabel nameLabel = new JLabel("Name: " + expense.getName());
        JLabel amountLabel = new JLabel("Amount: €" + expense.getAmount());
        JLabel categoryLabel = new JLabel("Category: " + expense.getCategory());
        JLabel dateLabel = new JLabel("Date: " + expense.getDate().toString());
        JLabel createdByLabel = new JLabel("Created By: " + expense.getCreatedBy().getName());

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
        JButton backButton = createStyledButton("Back to Expenses");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonsPanel.add(backButton);

        JButton editButton = createStyledButton("Edit Expense");
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, expense)));
        buttonsPanel.add(editButton);

        JButton deleteButton = createStyledButton("Delete Expense");
        deleteButton.addActionListener(e -> {
            if (expense.getId() == null) {
                JOptionPane.showMessageDialog(this, "The expense ID is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this expense?", "Delete Expense", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    expenseService.delete(expense.getId());
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSES_SCREEN, new ExpensesScreen(mainFrame)); // Atualiza a lista
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "An error occurred while deleting the expense: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonsPanel.add(deleteButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Cor inicial do botão
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding no botão

        // Efeito de hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0)); // Cor ao passar o mouse
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 56, 56)); // Cor ao sair com o mouse
            }
        });

        return button;
    }
}

