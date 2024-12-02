package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.ParticipantsDialog;
import com.ptda.tracker.ui.user.forms.BudgetForm;
import com.ptda.tracker.ui.user.forms.ExpenseForm;
import com.ptda.tracker.ui.user.forms.ShareBudgetForm;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAccessService budgetAccessService;
    private final User user = UserSession.getInstance().getUser();
    private final Budget budget;
    private final List<Expense> expenses;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.budget = budget;
        expenses = mainFrame.getContext().getBean(ExpenseService.class).getAllByBudgetId(budget.getId());

        initComponents();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        participantsButton.addActionListener(e -> {
            ParticipantsDialog participantsDialog = new ParticipantsDialog(mainFrame, budget);
            participantsDialog.setVisible(true);
        });
        if (editButton != null) {
            editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_FORM, new BudgetForm(mainFrame, null, budget)));
        }
        if (shareButton != null) {
            shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_SHARE_FORM, new ShareBudgetForm(mainFrame, budget)));
        }
        if (addExpenseButton != null) {
            addExpenseButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_FORM, new ExpenseForm(mainFrame, null, budget, mainFrame.getCurrentScreen(), this::refreshExpenses)));
        }
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                importExpensesFromCSV(selectedFile);
            }
        });
        expensesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = expensesTable.getSelectedRow();
                if (selectedRow != -1) {
                    Expense selectedExpense = expenses.get(selectedRow);
                    mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, selectedExpense, mainFrame.getCurrentScreen(), null));
                    expensesTable.clearSelection();
                }
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
                expense.setBudget(budget);
                importedExpenses.add(expense);
            }

            mainFrame.getContext().getBean(ExpenseService.class).saveAll(importedExpenses);
            JOptionPane.showMessageDialog(this, "Expenses imported successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error importing expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // After importing, refresh the expense list
        refreshExpenses();
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

    private void refreshExpenses() {
        expenses.clear();
        expenses.addAll(mainFrame.getContext().getBean(ExpenseService.class).getAllByBudgetId(budget.getId()));
        expensesTable.setModel(createExpensesTableModel());
    }

    private DefaultTableModel createExpensesTableModel() {
        String[] columnNames = {TITLE, AMOUNT, CATEGORY, DATE, CREATED_BY_COLUMN};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Expense expense : expenses) {
            model.addRow(new Object[]{
                    expense.getTitle(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getDate(),
                    expense.getCreatedBy().getName()});
        }
        return model;
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de Detalhes do Orçamento
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BUDGET_DETAILS));

        nameLabel = new JLabel(NAME + ": " + budget.getName());
        descriptionLabel = new JLabel(DESCRIPTION + ": " + budget.getDescription());
        createdByLabel = new JLabel(CREATED_BY + ": " + budget.getCreatedBy().getName());

        Font font = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        createdByLabel.setFont(font);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);

        // Painel para botões próximos aos detalhes (alinhados à direita)
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        participantsButton = new JButton(PARTICIPANTS);
        topButtonsPanel.add(participantsButton);

        boolean hasOwnerAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.OWNER);
        boolean hasEditorAccess = budgetAccessService.hasAccess(budget.getId(), user.getId(), BudgetAccessLevel.EDITOR);

        if (hasEditorAccess) {
            editButton = new JButton(EDIT_BUDGET);
            topButtonsPanel.add(editButton);
        }
        if (hasOwnerAccess) {
            shareButton = new JButton(SHARE_BUDGET);
            topButtonsPanel.add(shareButton);
        }

        importButton = new JButton("Import Expenses");
        topButtonsPanel.add(importButton);

        // Adiciona os detalhes e os botões ao topo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(detailsPanel, BorderLayout.CENTER);
        topPanel.add(topButtonsPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Painel Central (Tabela de Despesas)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(EXPENSES));
        expensesTable = new JTable(createExpensesTableModel());
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Painel Inferior dividido para Back e Simulate Budget
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Painel do botão "Back" (alinhado à esquerda)
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);
        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);

        // Painel do botão "Simulate Budget" (alinhado à direita)
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (!expenses.isEmpty()) {
            JButton simulateBudget = new JButton(SIMULATION_BUDGET);
            simulateBudget.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.SIMULATE_VIEW, new SimulationView(mainFrame, budget)));
            rightButtonPanel.add(simulateBudget);
        }
        if (hasEditorAccess) {
            addExpenseButton = new JButton(ADD_EXPENSE);
            rightButtonPanel.add(addExpenseButton);
        }
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTable expensesTable;
    JLabel nameLabel, descriptionLabel, createdByLabel;
    private JButton backButton, participantsButton, editButton, shareButton, addExpenseButton, importButton;
    private static final String
            BUDGET_DETAILS = "Budget Details",
            NAME = "Name",
            DESCRIPTION = "Description",
            CREATED_BY = "Created By",
            EXPENSES = "Expenses",
            BACK = "Back",
            PARTICIPANTS = "Participants",
            EDIT_BUDGET = "Edit Budget",
            ADD_EXPENSE = "Add Expense",
            SHARE_BUDGET = "Share Budget",
            SIMULATION_BUDGET = "Simulation Budget",
            TITLE = "Title",
            AMOUNT = "Amount",
            CATEGORY = "Category",
            DATE = "Date",
            CREATED_BY_COLUMN = "Created By";
}