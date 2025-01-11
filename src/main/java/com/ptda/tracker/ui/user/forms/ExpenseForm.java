package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.views.ExpenseDetailView;
import com.ptda.tracker.util.DateFormatManager;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpenseForm extends JPanel {
    private final MainFrame mainFrame;
    private Runnable onFormSubmit;
    private Expense expense;
    private Budget budget;
    private final String returnScreen;
    boolean deleteDivisions = false, updateDivisions = false;

    public ExpenseForm(MainFrame mainFrame, Expense expense, Budget budget, String returnScreen, Runnable onFormSubmit) {
        this.mainFrame = mainFrame;
        this.onFormSubmit = onFormSubmit;
        this.expense = expense;
        this.budget = budget;
        this.returnScreen = returnScreen;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        // lets user input only double values in amount field
        amountField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!((c >= '0' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE || c == '.' || c == ',')) {
                    evt.consume();
                }
            }
        });
        backButton.addActionListener(e -> {
            if (onFormSubmit != null) onFormSubmit.run();
            mainFrame.showScreen(returnScreen);
        });
        saveButton.addActionListener(e -> saveExpense());
    }

    private void saveExpense() {
        if (expense != null && expense.getBudget() != null) {
            askAboutDivisions();
        }
        // get form values
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String amountText = amountField.getText().trim();
        double amount = amountText.isEmpty() ? 0 : Double.parseDouble(amountText);
        Date date = dateChooser.getDate();
        ExpenseCategory category = (ExpenseCategory) categoryComboBox.getSelectedItem();

        // verifications
        if (title.isEmpty() || amount <= 0) {
            JOptionPane.showMessageDialog(this, TITLE_AND_AMOUNT_REQUIRED, VALIDATION_ERROR, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // set values
        if (expense == null) {
            expense = new Expense();
            expense.setBudget(budget);
        } else {
            String budgetName = (String) budgetComboBox.getSelectedItem();
            Budget selectedBudget = budgetMap.get(budgetName);
            expense.setBudget(selectedBudget);
        }
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setCategory(category);
        expense.setDescription(description);

        // save expense
        ExpenseService expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        if (expense.getId() == null) {
            if (expenseService.create(expense) == null) {
                JOptionPane.showMessageDialog(this, FAILED_TO_SAVE_EXPENSE, ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (expense.getBudget() != null && !deleteDivisions) {
                if (expenseService.update(expense, updateDivisions) == null) {
                    JOptionPane.showMessageDialog(this, FAILED_TO_UPDATE_EXPENSE, ERROR, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                if (expenseService.update(expense) == null) {
                    JOptionPane.showMessageDialog(this, FAILED_TO_UPDATE_EXPENSE, ERROR, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        clearFields();
        onFormSubmit.run();
        mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, returnScreen, onFormSubmit));
    }

    private void askAboutDivisions() {
        int keepResult = JOptionPane.showConfirmDialog(
                this, "Want to keep divisions?",
                "Expense Divisions", JOptionPane.YES_NO_OPTION
        );
        if (keepResult == JOptionPane.YES_OPTION) {
            int updateResult = JOptionPane.showConfirmDialog(
                    this, "Want to update divisions proportionally to the new amount?",
                    "Expense Divisions", JOptionPane.YES_NO_OPTION
            );
            if (updateResult == JOptionPane.YES_OPTION) {
                updateDivisions = true;
            }
        } else {
            deleteDivisions = true;
        }
    }

    private void clearFields() {
        titleField.setText("");
        amountField.setText("");
        dateChooser.setDate(new Date());
        categoryComboBox.setSelectedIndex(0);
        budgetComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel(expense == null ? CREATE_NEW_EXPENSE : EDIT_EXPENSE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(TITLE + ":"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(expense != null ? expense.getTitle() : "", 25);
        formPanel.add(titleField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(AMOUNT + ":"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(expense != null ? String.valueOf(expense.getAmount()) : "", 25);
        formPanel.add(amountField, gbc);

        // Date Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel(DATE + ":"), gbc);

        gbc.gridx = 1;
        dateChooser = new JXDatePicker();
        dateChooser.getEditor().setEditable(false);
        dateChooser.setFormats(DateFormatManager.getInstance().getDateFormat());
        dateChooser.setDate(expense != null ? expense.getDate() : new Date());
        formPanel.add(dateChooser, gbc);

        // Category ComboBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel(CATEGORY + ":"), gbc);

        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(ExpenseCategory.values());
        if (expense != null) {
            categoryComboBox.setSelectedItem(expense.getCategory());
        }
        formPanel.add(categoryComboBox, gbc);

        // Budget ComboBox (initialize but do not add if budget is not null)
        budgetMap = new HashMap<>();
        budgetComboBox = new JComboBox<>();
        budgetComboBox.addItem(NO_BUDGET);
        BudgetService budgetService = mainFrame.getContext().getBean(BudgetService.class);
        for (Budget budget : budgetService.getAllByUserId(UserSession.getInstance().getUser().getId())) {
            budgetComboBox.addItem(budget.getName());
            budgetMap.put(budget.getName(), budget);
        }
        if (expense != null && expense.getBudget() != null) {
            budgetComboBox.setSelectedItem(expense.getBudget().getName());
        } else {
            budgetComboBox.setSelectedItem(NO_BUDGET);
        }
        if (budget == null && (expense == null || expense.getBudget() == null)) {
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(new JLabel(BUDGET + ":"), gbc);

            gbc.gridx = 1;
            formPanel.add(budgetComboBox, gbc);
        }

        // Description Area
        gbc.gridx = 0;
        gbc.gridy = budget == null && (expense == null || expense.getBudget() == null) ? 5 : 4;
        formPanel.add(new JLabel(DESCRIPTION + ":"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(expense != null ? expense.getDescription() : "", 4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(descriptionScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton = new JButton(BACK);
        leftButtonPanel.add(backButton);
        saveButton = new JButton(SAVE);
        rightButtonPanel.add(saveButton);

        buttonsPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonsPanel.add(rightButtonPanel, BorderLayout.EAST);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTextField titleField, amountField;
    private JTextArea descriptionArea;
    private JComboBox<String> budgetComboBox;
    private Map<String, Budget> budgetMap;
    private JComboBox<ExpenseCategory> categoryComboBox;
    private JXDatePicker dateChooser;
    private JButton saveButton, backButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            CREATE_NEW_EXPENSE = localeManager.getTranslation("create_new_expense"),
            EDIT_EXPENSE = localeManager.getTranslation("edit_expense"),
            TITLE = localeManager.getTranslation("title"),
            AMOUNT = localeManager.getTranslation("amount"),
            DATE = localeManager.getTranslation("date"),
            CATEGORY = localeManager.getTranslation("category"),
            BUDGET = localeManager.getTranslation("budget"),
            DESCRIPTION = localeManager.getTranslation("description"),
            NO_BUDGET = localeManager.getTranslation("no_budget"),
            BACK = localeManager.getTranslation("back"),
            SAVE = localeManager.getTranslation("save"),
            VALIDATION_ERROR = localeManager.getTranslation("validation_error"),
            TITLE_AND_AMOUNT_REQUIRED = localeManager.getTranslation("title_and_amount_required"),
            FAILED_TO_SAVE_EXPENSE = localeManager.getTranslation("failed_to_save_expense"),
            FAILED_TO_UPDATE_EXPENSE = localeManager.getTranslation("failed_to_update_expense"),
            ERROR = localeManager.getTranslation("error");
}