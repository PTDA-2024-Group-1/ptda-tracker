package com.ptda.tracker.ui.screens;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ExpenseForm;
import com.ptda.tracker.ui.renderers.ExpenseListRenderer;
import com.ptda.tracker.ui.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ExpensesScreen extends JPanel {
    private final ExpenseService expenseService;
    private final JList<Expense> expenseList;
    private List<Expense> expenses;

    public ExpensesScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        expenseList = new JList<>(new DefaultListModel<>());
        expenseList.setCellRenderer(new ExpenseListRenderer());
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        expenses = expenseService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setExpenseList(expenses);

        expenseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Expense selectedExpense = expenseList.getSelectedValue();
                if (selectedExpense != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, this::refreshExpenseList, selectedExpense));
                    expenseList.clearSelection(); // Limpar seleção para permitir nova interação
                }
            }
        });

        add(new JScrollPane(expenseList), BorderLayout.CENTER);

        JLabel label = new JLabel("Select an expense to view details", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton createButton = new JButton("Create New Expense");
        styleButton(createButton);
        createButton.addActionListener(e -> {
            // Abrir o ExpenseForm no modo de criação
            mainFrame.registerScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, this::refreshExpenseList, null));
            mainFrame.showScreen(ScreenNames.EXPENSE_FORM);
        });
        add(createButton, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Cor elegante para o botão
        button.setForeground(Color.WHITE); // Texto branco
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40)); // Tamanho do botão
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito de hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0)); // Cor mais escura ao passar o mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(56, 56, 56)); // Voltar à cor original
            }
        });
    }

    private void refreshExpenseList() {
        expenseList.clearSelection();
        expenses = expenseService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setExpenseList(expenses);
    }

    public void setExpenseList(List<Expense> expenses) {
        DefaultListModel<Expense> model = (DefaultListModel<Expense>) expenseList.getModel();
        model.clear(); // Clear old data
        expenses.forEach(model::addElement); // Add new data
    }
}
