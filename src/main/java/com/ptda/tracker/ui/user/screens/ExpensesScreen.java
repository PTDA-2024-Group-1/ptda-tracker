package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.renderers.ExpenseListRenderer;
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
import java.util.Date;
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
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                importExpensesFromCSV(selectedFile);
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

        // After importing, refresh the expense list
        refreshExpenseList();
    }

    private ExpenseCategory convertToExpenseCategory(String category) {
        try {
            return ExpenseCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle the case where the category does not match any enum constant
            JOptionPane.showMessageDialog(this, "Unknown category: " + category, "Error", JOptionPane.ERROR_MESSAGE);
            return null; // or handle it in another way
        }
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createButton = new JButton(CREATE_NEW_EXPENSE);
        importButton = new JButton(IMPORT_EXPENSES);

        buttonPanel.add(importButton);
        buttonPanel.add(createButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton, importButton;
    private static final String
            SELECT_EXPENSE = "Select an expense to view details",
            CREATE_NEW_EXPENSE = "Create New Expense",
            IMPORT_EXPENSES = "Import Expenses";
}