package com.ptda.tracker.ui.user.forms.obsolete;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseDivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseDivisionsOldFormRefactored extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final Budget budget;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseDivisionService expenseDivisionService;
    private final List<ExpenseDivision> expenseDivisions;
    private final Runnable onBack;

    public ExpenseDivisionsOldFormRefactored(MainFrame mainFrame, Expense expense, Budget budget, Runnable onBack) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.budget = budget;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.expenseDivisionService = mainFrame.getContext().getBean(ExpenseDivisionService.class);
        this.expenseDivisions = expenseDivisionService.getAllByExpenseId(expense.getId());
        this.onBack = onBack;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        equitableRadioButton.addActionListener(e -> switchToEquitableDistribution());
        customRadioButton.addActionListener(e -> switchToCustomDistribution());
    }

    private void switchToEquitableDistribution() {
        customDistributionPanel.setVisible(false);
        totalPercentageLabel.setVisible(false);
    }

    private void switchToCustomDistribution() {
        customDistributionPanel.setVisible(true);
        totalPercentageLabel.setVisible(true);
        loadCustomDistributionRows();
    }

    private void distributeExpenses() {
        if (equitableRadioButton.isSelected()) {
            distributeEquitably();
        } else if (customRadioButton.isSelected()) {
            distributeCustom();
        }
    }

    private void distributeEquitably() {
        double remainingPercentage = calculateRemainingPercentage();
        if (remainingPercentage <= 0) {
            showMessage(DISTRIBUTION_ALREADY_COMPLETE);
            return;
        }

        List<BudgetAccess> availableUsers = getAvailableUsersForDistribution();
        if (availableUsers.isEmpty()) {
            showMessage(NO_USERS_LEFT);
            return;
        }

        List<BudgetAccess> selectedUsers = showUserChecklistDialog(availableUsers);
        if (selectedUsers.isEmpty()) {
            showMessage(NO_USERS_SELECTED);
            return;
        }

        allocateEquitableDistribution(selectedUsers, remainingPercentage);
    }

    private void distributeCustom() {
        double totalPercentage = validateAndDistributeCustomRows();
        handleRemainingPercentage(totalPercentage);
    }

    private double validateAndDistributeCustomRows() {
        // Validation and processing logic for custom rows
        return 0; // Adjust as per the calculation logic
    }

    private void handleRemainingPercentage(double totalPercentage) {
        double remainingPercentage = 100 - totalPercentage;
        if (remainingPercentage > 0) {
            showMessage(DISTRIBUTION_SAVED + remainingPercentage + "%.");
        } else {
            showMessage(DISTRIBUTION_COMPLETE);
        }
        navigateBack();
    }

    private void loadCustomDistributionRows() {
        customDistributionPanel.removeAll();
        expenseDivisions.forEach(this::addCustomDistributionRow);
        customDistributionPanel.revalidate();
        customDistributionPanel.repaint();
    }

    private void addCustomDistributionRow(ExpenseDivision expenseDivision) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel(expenseDivision.getUser().getName());
        JTextField percentageField = new JTextField(5);
        percentageField.setText(String.valueOf(expenseDivision.getPercentage()));
        rowPanel.add(nameLabel);
        rowPanel.add(percentageField);
        customDistributionPanel.add(rowPanel);
    }

    private double calculateRemainingPercentage() {
        return 100 - expenseDivisions.stream().mapToDouble(ExpenseDivision::getPercentage).sum();
    }

    private List<BudgetAccess> getAvailableUsersForDistribution() {
        List<BudgetAccess> availableUsers = new ArrayList<>();
        List<BudgetAccess> users = getUsers(budget);
        for (BudgetAccess userAccess : users) {
            if (expenseDivisions.stream().noneMatch(div -> div.getUser().equals(userAccess.getUser()))) {
                availableUsers.add(userAccess);
            }
        }
        return availableUsers;
    }

    private List<BudgetAccess> getUsers(Budget budget) {
        return budgetAccessService.getAllByBudgetId(budget.getId());
    }

    private List<BudgetAccess> showUserChecklistDialog(List<BudgetAccess> users) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (BudgetAccess access : users) {
            JCheckBox checkBox = new JCheckBox(access.getUser().getName());
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, SELECT_USERS, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return checkBoxes.stream()
                    .filter(JCheckBox::isSelected)
                    .map(checkBox -> users.get(checkBoxes.indexOf(checkBox)))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private void allocateEquitableDistribution(List<BudgetAccess> selectedUsers, double remainingPercentage) {
        double equalShare = remainingPercentage / selectedUsers.size();
        double roundedShare = Math.round(equalShare * 10) / 10.0;

        for (BudgetAccess userAccess : selectedUsers) {
            double amount = expense.getAmount() * (roundedShare / 100);
            expenseDivisionService.create(
                    ExpenseDivision.builder()
                            .expense(expense)
                            .user(userAccess.getUser())
                            .percentage(roundedShare)
                            .amount(amount)
                            .build()
            );
        }

        showMessage(REMAINING_PERCENTAGE_DISTRIBUTED + roundedShare + "%.");
        navigateBack();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void navigateBack() {
        if (onBack != null) {
            onBack.run();
        } else {
            mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        addDistributionTypeSection(formPanel, gbc);
        addCustomDistributionSection(formPanel, gbc);
        addButtonsSection(formPanel, gbc);

        return formPanel;
    }

    private void addDistributionTypeSection(JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(DISTRIBUTION_TYPE), gbc);

        gbc.gridx = 1;
        JPanel distributionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        equitableRadioButton = new JRadioButton(EQUITABLE);
        customRadioButton = new JRadioButton(CUSTOM);
        ButtonGroup distributionGroup = new ButtonGroup();
        distributionGroup.add(equitableRadioButton);
        distributionGroup.add(customRadioButton);
        distributionPanel.add(equitableRadioButton);
        distributionPanel.add(customRadioButton);
        formPanel.add(distributionPanel, gbc);
    }

    private void addCustomDistributionSection(JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        customDistributionPanel = new JPanel();
        customDistributionPanel.setLayout(new BoxLayout(customDistributionPanel, BoxLayout.Y_AXIS));
        customDistributionPanel.setBorder(BorderFactory.createTitledBorder(CUSTOM_DISTRIBUTION));
        customDistributionPanel.setVisible(false);
        formPanel.add(customDistributionPanel, gbc);

        gbc.gridy = 2;
        totalPercentageLabel = new JLabel(TOTAL_PERCENTAGE + ": 0%");
        totalPercentageLabel.setVisible(false);
        formPanel.add(totalPercentageLabel, gbc);
    }

    private void addButtonsSection(JPanel formPanel, GridBagConstraints gbc) {
        gbc.gridy = 3;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> navigateBack());
        buttonsPanel.add(cancelButton);

        JButton distributeButton = new JButton(DISTRIBUTE);
        distributeButton.addActionListener(e -> distributeExpenses());
        buttonsPanel.add(distributeButton);

        formPanel.add(buttonsPanel, gbc);
    }

    private JRadioButton equitableRadioButton, customRadioButton;
    private JPanel customDistributionPanel;
    private JLabel totalPercentageLabel;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            DISTRIBUTION_TYPE = localeManager.getTranslation("distribution_type"),
            EQUITABLE = localeManager.getTranslation("equitable"),
            CUSTOM = localeManager.getTranslation("custom"),
            CUSTOM_DISTRIBUTION = localeManager.getTranslation("custom_distribution"),
            CANCEL = localeManager.getTranslation("cancel"),
            DISTRIBUTE = localeManager.getTranslation("distribute"),
            DISTRIBUTION_COMPLETE = localeManager.getTranslation("distribution_complete"),
            DISTRIBUTION_SAVED = localeManager.getTranslation("distribution_saved"),
            REMAINING_PERCENTAGE_DISTRIBUTED = localeManager.getTranslation("remaining_percentage_distributed"),
            NO_USERS_LEFT = localeManager.getTranslation("no_users_left"),
            INVALID_PERCENTAGE = localeManager.getTranslation("invalid_percentage"),
            PERCENTAGE_GREATER_THAN_ZERO = localeManager.getTranslation("percentage_greater_than_zero"),
            TOTAL_PERCENTAGE_EXCEED = localeManager.getTranslation("total_percentage_exceed"),
            NO_USERS_SELECTED = localeManager.getTranslation("no_users_selected"),
            DISTRIBUTION_ALREADY_COMPLETE = localeManager.getTranslation("distribution_already_complete"),
            PERCENTAGE = localeManager.getTranslation("percentage"),
            TOTAL_PERCENTAGE = localeManager.getTranslation("total_percentage"),
            SELECT_USERS = localeManager.getTranslation("select_users");
}