package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.components.renderers.ExpenseListRenderer;
import com.ptda.tracker.ui.user.views.ExpenseDetailView;
import com.ptda.tracker.util.ExpensesImportSharedData;
import com.ptda.tracker.util.Refreshable;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.ptda.tracker.ui.user.views.BudgetDetailView.openImport;

public class ExpensesScreen extends JPanel implements Refreshable {
    private final MainFrame mainFrame;
    private ExpenseService expenseService;
    private final ExpensesImportSharedData sharedData;

    public ExpensesScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        sharedData = ExpensesImportSharedData.getInstance();
        initUI();
        setListeners();
    }

    private void setListeners() {
        expensesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Expense selectedExpense = expensesList.getSelectedValue();
                if (selectedExpense != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), this::refreshExpenseList));
                    expensesList.clearSelection();
                }
            }
        });
        createButton.addActionListener(e -> {
            mainFrame.registerAndShowScreen(
                    ScreenNames.EXPENSE_FORM,
                    new ExpenseForm(mainFrame, null, null,
                            mainFrame.getCurrentScreen(), this::refreshExpenseList)
            );
        });
        importButton.addActionListener(e -> openImport(mainFrame, null, this::refreshExpenseList));
        nextPageButton.addActionListener(e -> {
            if ((currentPage + 1) * PAGE_SIZE < expenses.size()) {
                currentPage++;
                updatePagination();
            }
        });
        prevPageButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updatePagination();
            }
        });
    }

    private void refreshExpenseList() {
        expensesList.clearSelection();
        expenses = expenseService.getPersonalExpensesByUserIdWithPagination(UserSession.getInstance().getUser().getId(), currentPage * PAGE_SIZE, PAGE_SIZE);
        updatePagination();
    }

    private void updatePaginationPanel() {
        paginationPanel.removeAll();
        int totalExpenses = expenseService.getCountByBudgetId(UserSession.getInstance().getUser().getId());
        int totalPages = (int) Math.ceil((double) totalExpenses / PAGE_SIZE);
        if (totalExpenses > PAGE_SIZE) {
            for (int i = 0; i < totalPages; i++) {
                int pageIndex = i;
                JButton pageButton = new JButton(String.valueOf(i + 1));
                pageButton.setEnabled(pageIndex != currentPage);
                pageButton.addActionListener(e -> {
                    currentPage = pageIndex;
                    refreshExpenseList();
                });
                paginationPanel.add(pageButton);
            }
            paginationPanel.setVisible(true);
        } else {
            paginationPanel.setVisible(false);
        }
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private void updatePagination() {
        DefaultListModel<Expense> model = (DefaultListModel<Expense>) expensesList.getModel();
        model.clear();
        for (Expense expense : expenses) {
            model.addElement(expense);
        }
        updatePaginationPanel();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        expensesList = new JList<>(new DefaultListModel<>());
        expensesList.setCellRenderer(new ExpenseListRenderer());
        expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        expenses = expenseService.getPersonalExpensesByUserId(UserSession.getInstance().getUser().getId());

        add(new JScrollPane(expensesList), BorderLayout.CENTER);

        JLabel label = new JLabel(SELECT_EXPENSE, SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createButton = new JButton(CREATE_NEW_EXPENSE);
        importButton = new JButton(IMPORT_EXPENSES);

        buttonPanel.add(importButton);
        buttonPanel.add(createButton);

        // Initialize pagination buttons
        prevPageButton = new JButton();
        nextPageButton = new JButton();

        // Add pagination buttons to the pagination panel
        paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(prevPageButton);
        paginationPanel.add(nextPageButton);

        // Painel inferior que contém os botões e a paginação
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.add(paginationPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshExpenseList();
    }

    private JList<Expense> expensesList;
    private List<Expense> expenses;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 20;
    private JPanel paginationPanel;

    private JButton createButton, importButton, prevPageButton, nextPageButton;
    private static final String
            SELECT_EXPENSE = "Select an expense to view details",
            CREATE_NEW_EXPENSE = "Create New Expense",
            IMPORT_EXPENSES = "Import Expenses";

    @Override
    public void refresh() {
        refreshExpenseList();
    }
}