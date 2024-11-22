package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpenseForm extends JPanel {
    private final MainFrame mainFrame;
    private final ApplicationContext context;
    private final Runnable onFormSubmit;
    private Expense expense;

    private JTextField titleField, amountField;
    private JTextArea descriptionArea;
    private JComboBox<String> budgetComboBox;
    private Map<String, Budget> budgetMap;
    private JComboBox<ExpenseCategory> categoryComboBox;
    private JSpinner dateSpinner;
    private JButton saveButton, backButton;

    private static final Color PRIMARY_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(56, 56, 56, 255);
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 0, 0, 255);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    public ExpenseForm(MainFrame mainFrame, Runnable onFormSubmit, Expense expense) {
        this.mainFrame = mainFrame;
        this.context = mainFrame.getContext();
        this.onFormSubmit = onFormSubmit;
        this.expense = expense;

        budgetMap = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(PRIMARY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel(expense == null ? "Create New Expense" : "Edit Expense");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.DARK_GRAY);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(expense != null ? expense.getTitle() : "", 25);
        formPanel.add(titleField, gbc);

        // Amount Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(expense != null ? String.valueOf(expense.getAmount()) : "", 25);
        formPanel.add(amountField, gbc);

        // Date Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);

        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(expense != null ? expense.getDate() : new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        formPanel.add(dateSpinner, gbc);

        // Category ComboBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(ExpenseCategory.values());
        if (expense != null) {
            categoryComboBox.setSelectedItem(expense.getCategory());
        }
        formPanel.add(categoryComboBox, gbc);

        // Budget ComboBox
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Budget:"), gbc);

        gbc.gridx = 1;
        budgetComboBox = new JComboBox<>();
        BudgetService budgetService = context.getBean(BudgetService.class);
        for (Budget budget : budgetService.getAllByUserId(UserSession.getInstance().getUser().getId())) {
            budgetComboBox.addItem(budget.getName());
            budgetMap.put(budget.getName(), budget);
        }
        if (expense != null && expense.getBudget() != null) {
            budgetComboBox.setSelectedItem(expense.getBudget().getName());
        }
        formPanel.add(budgetComboBox, gbc);

        // Description Area
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(expense != null ? expense.getDescription() : "", 4, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(descriptionScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PRIMARY_COLOR);

        // Back Button
        backButton = createStyledButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));

        // Save Button
        saveButton = createStyledButton("Save");
        saveButton.addActionListener(this::saveExpense);

        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveExpense(ActionEvent e) {
        // get form values
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String amountText = amountField.getText().trim();
        double amount = amountText.isEmpty() ? 0 : Double.parseDouble(amountText);
        Date date = (Date) dateSpinner.getValue();
        ExpenseCategory category = (ExpenseCategory) categoryComboBox.getSelectedItem();
        String budgetName = (String) budgetComboBox.getSelectedItem();
        Budget budget = budgetMap.get(budgetName);

        // verifications
        if (title.isEmpty() || amount <= 0) {
            JOptionPane.showMessageDialog(this, "Title and valid amount are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // set values
        if (expense == null) {
            expense = new Expense();
        }
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setCategory(category);
        expense.setBudget(budget);
        expense.setDescription(description);

        // save expense
        ExpenseService expenseService = context.getBean(ExpenseService.class);
        if (expense.getId() == null) {
            if (expenseService.create(expense) == null) {
                JOptionPane.showMessageDialog(this, "Failed to save expense.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (expenseService.update(expense) == null) {
                JOptionPane.showMessageDialog(this, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        clear();
        onFormSubmit.run();
        mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN);
    }

    private void clear() {
        titleField.setText("");
        amountField.setText("");
        dateSpinner.setValue(new Date());
        categoryComboBox.setSelectedIndex(0);
        budgetComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
    }

    private JButton createStyledButton(String text) {
        return getJButton(text, BUTTON_COLOR, BUTTON_HOVER_COLOR, BUTTON_TEXT_COLOR);
    }

    public static JButton getJButton(String text, Color buttonColor, Color buttonHoverColor, Color buttonTextColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonHoverColor);
                button.setForeground(buttonTextColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonColor);
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }
}