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
import java.util.List;

public class ExpensesScreen extends JPanel {
    private final MainFrame mainFrame;
    private ExpenseService expenseService;

    private JList<Expense> expensesList;
    private List<Expense> expenses;
    private JButton createButton;

    public ExpensesScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        expensesList = new JList<>(new DefaultListModel<>());
        expensesList.setCellRenderer(new ExpenseListRenderer());
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        expenses = expenseService.getPersonalExpensesByUserId(UserSession.getInstance().getUser().getId());
        setExpensesList(expenses);

        add(new JScrollPane(expensesList), BorderLayout.CENTER);

        JLabel label = new JLabel("Select an expense to view details", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        createButton = new JButton("Create New Expense");
        add(createButton, BorderLayout.SOUTH);
    }

    private void setListeners() {
        expensesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Expense selectedExpense = expensesList.getSelectedValue();
                if (selectedExpense != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), this::refreshExpenseList));
                    expensesList.clearSelection(); // Limpar seleção para permitir nova interação
                }
            }
        });
        createButton.addActionListener(e -> {
            // Abrir o ExpenseForm no modo de criação
            mainFrame.registerScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, mainFrame.getCurrentScreen(), this::refreshExpenseList));
            mainFrame.showScreen(ScreenNames.EXPENSE_FORM);
        });
    }

    private void refreshExpenseList() {
        expensesList.clearSelection();
        expenses = expenseService.getPersonalExpensesByUserId(UserSession.getInstance().getUser().getId());
        setExpensesList(expenses);
    }

    public void setExpensesList(List<Expense> expenses) {
        DefaultListModel<Expense> model = (DefaultListModel<Expense>) expensesList.getModel();
        model.clear(); // Clear old data
        expenses.forEach(model::addElement); // Add new data
    }
}