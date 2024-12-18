package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.components.renderers.ExpenseListRenderer;
import com.ptda.tracker.ui.user.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpensesScreen extends JPanel {
    private final MainFrame mainFrame;
    private ExpenseService expenseService;

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
                    expensesList.clearSelection();
                }
            }
        });
        createButton.addActionListener(e -> {
            mainFrame.registerScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, null, mainFrame.getCurrentScreen(), this::refreshExpenseList));
            mainFrame.showScreen(ScreenNames.EXPENSE_FORM);
        });
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                importExpensesFromCSV(selectedFile);
            }
        });
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

    private void importExpensesFromCSV(File file) {
        List<Expense> importedExpenses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (FileReader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                Expense expense = new Expense();
                expense.setTitle(csvRecord.get("title"));
                expense.setAmount(Double.parseDouble(csvRecord.get("amount")));
                LocalDate localDate = LocalDate.parse(csvRecord.get("date"), formatter);
                expense.setDate(java.sql.Date.valueOf(localDate));
                expense.setCategory(convertToExpenseCategory(csvRecord.get("category")));
                expense.setDescription(csvRecord.get("description"));
                expense.setCreatedBy(UserSession.getInstance().getUser());
                importedExpenses.add(expense);
            }

            expenseService.saveAll(importedExpenses);
            JOptionPane.showMessageDialog(this, "Expenses imported successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error importing expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        refreshExpenseList();
    }

    private ExpenseCategory convertToExpenseCategory(String category) {
        try {
            return ExpenseCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Unknown category: " + category, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void refreshExpenseList() {
        expensesList.clearSelection();
        expenses = expenseService.getPersonalExpensesByUserIdWithPagination(UserSession.getInstance().getUser().getId(), currentPage * PAGE_SIZE, PAGE_SIZE);
        updatePagination();
    }

    private void updatePaginationPanel() {
        paginationPanel.removeAll();
        long totalExpenses = expenseService.countByBudgetId(UserSession.getInstance().getUser().getId());
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
}