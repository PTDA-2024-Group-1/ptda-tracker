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

    public ExpensesScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI();
        setListeners();
    }

    private void setListeners() {
        expensesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Expense selectedExpense = expensesList.getSelectedValue();
                if (selectedExpense != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), this::refreshExpenseList));
                    expensesList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });
        createButton.addActionListener(e -> {
            // Open ExpenseForm in creation mode
            mainFrame.registerScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, null, mainFrame.getCurrentScreen(), this::refreshExpenseList));
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

    private void initUI() {
        setLayout(new BorderLayout());

        expensesList = new JList<>(new DefaultListModel<>());
        expensesList.setCellRenderer(new ExpenseListRenderer());
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        expenses = expenseService.getPersonalExpensesByUserId(UserSession.getInstance().getUser().getId());
        setExpensesList(expenses);

        add(new JScrollPane(expensesList), BorderLayout.CENTER);

        JLabel label = new JLabel(SELECT_EXPENSE, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        createButton = new JButton(CREATE_NEW_EXPENSE);
        add(createButton, BorderLayout.SOUTH);
    }

    private JButton createButton;
    private static final String
            SELECT_EXPENSE = "Select an expense to view details",
            CREATE_NEW_EXPENSE = "Create New Expense";

}