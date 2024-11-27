package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DistributeExpenseForm extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final BudgetAccessService budgetAccessService;
    private final ExpenseService expenseService;
    private final SubdivisionService subdivisionService;

    private JComboBox<Expense> expenseComboBox;
    private JRadioButton equitableRadioButton;
    private JRadioButton customRadioButton;
    private JPanel customDistributionPanel;
    private List<Subdivision> subdivisions;

    public DistributeExpenseForm(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.subdivisionService = mainFrame.getContext().getBean(SubdivisionService.class);
        this.subdivisions = new ArrayList<>();

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Distribute Expenses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(56, 56, 56));
        add(titleLabel, gbc);

        // Expense ComboBox
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel expenseLabel = new JLabel("Select Expense:");
        expenseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(expenseLabel, gbc);

        gbc.gridx = 1;
        expenseComboBox = new JComboBox<>(getExpenses());
        styleComboBox(expenseComboBox);
        add(expenseComboBox, gbc);

        // Distribution Type RadioButtons
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel distributionLabel = new JLabel("Distribution Type:");
        distributionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(distributionLabel, gbc);

        gbc.gridx = 1;
        JPanel distributionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        equitableRadioButton = new JRadioButton("Equitable");
        customRadioButton = new JRadioButton("Custom");
        ButtonGroup distributionGroup = new ButtonGroup();
        distributionGroup.add(equitableRadioButton);
        distributionGroup.add(customRadioButton);
        distributionPanel.add(equitableRadioButton);
        distributionPanel.add(customRadioButton);
        add(distributionPanel, gbc);

        // Custom Distribution Panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        customDistributionPanel = new JPanel();
        customDistributionPanel.setLayout(new BoxLayout(customDistributionPanel, BoxLayout.Y_AXIS));
        customDistributionPanel.setBorder(BorderFactory.createTitledBorder("Custom Distribution"));
        customDistributionPanel.setVisible(false);
        add(customDistributionPanel, gbc);

        // Buttons Panel
        gbc.gridy = 4;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        buttonsPanel.add(cancelButton);

        JButton distributeButton = new JButton("Distribute");
        distributeButton.addActionListener(e -> distributeExpenses());
        buttonsPanel.add(distributeButton);

        add(buttonsPanel, gbc);

        setBackground(new Color(240, 240, 240));
    }

    private void setListeners() {
        customRadioButton.addActionListener(e -> {
            customDistributionPanel.setVisible(true);
            addCustomDistributionRow();
        });
        equitableRadioButton.addActionListener(e -> customDistributionPanel.setVisible(false));
    }

    private Expense[] getExpenses() {
        List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
        return expenses.toArray(new Expense[0]);
    }

    private BudgetAccess[] getUsers() {
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budget.getId());
        return accesses.toArray(new BudgetAccess[0]);
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(new Color(255, 255, 255));
        comboBox.setForeground(new Color(56, 56, 56));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
    }

    private void distributeExpenses() {
        if (equitableRadioButton.isSelected()) {
            distributeEquitably();
        } else if (customRadioButton.isSelected()) {
            distributeCustom();
        }
    }

    private void distributeEquitably() {
        Expense selectedExpense = (Expense) expenseComboBox.getSelectedItem();
        List<Subdivision> existingSubdivisions = subdivisionService.getAllByExpenseId(selectedExpense.getId());

        if (!existingSubdivisions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This expense has already been subdivided and cannot be subdivided equitably again.");
            return;
        }

        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budget.getId());
        double amountPerUser = selectedExpense.getAmount() / accesses.size();
        User currentUser = UserSession.getInstance().getUser();

        for (BudgetAccess access : accesses) {
            Subdivision subdivision = Subdivision.builder()
                    .expense(selectedExpense)
                    .user(access.getUser())
                    .amount(amountPerUser)
                    .percentage(100.0 / accesses.size())
                    .createdBy(currentUser)
                    .build();
            subdivisions.add(subdivision);
            subdivisionService.create(subdivision);
        }

        mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
        JOptionPane.showMessageDialog(this, "Expenses distributed equitably.");
    }

    private void distributeCustom() {
        Expense selectedExpense = (Expense) expenseComboBox.getSelectedItem();
        List<Subdivision> existingSubdivisions = subdivisionService.getAllByExpenseId(selectedExpense.getId());
        double totalPercentage = existingSubdivisions.stream().mapToDouble(Subdivision::getPercentage).sum();
        User currentUser = UserSession.getInstance().getUser();

        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                JTextField percentageField = (JTextField) panel.getComponent(3);
                double percentage = Double.parseDouble(percentageField.getText());
                totalPercentage += percentage;
            }
        }

        if (totalPercentage > 100) {
            JOptionPane.showMessageDialog(this, "The total percentage must not exceed 100%.");
            return;
        }

        List<User> includedUsers = new ArrayList<>();
        for (Subdivision subdivision : existingSubdivisions) {
            includedUsers.add(subdivision.getUser());
        }

        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                JComboBox<BudgetAccess> userComboBox = (JComboBox<BudgetAccess>) panel.getComponent(1);
                JTextField percentageField = (JTextField) panel.getComponent(3);

                BudgetAccess selectedUser = (BudgetAccess) userComboBox.getSelectedItem();
                if (includedUsers.contains(selectedUser.getUser())) {
                    JOptionPane.showMessageDialog(this, "Each user can only be included once per expense.");
                    return;
                }

                double percentage = Double.parseDouble(percentageField.getText());
                double amount = selectedExpense.getAmount() * (percentage / 100);

                Subdivision subdivision = Subdivision.builder()
                        .expense(selectedExpense)
                        .user(selectedUser.getUser())
                        .amount(amount)
                        .percentage(percentage)
                        .createdBy(currentUser)
                        .build();
                subdivisions.add(subdivision);
                subdivisionService.create(subdivision);
                includedUsers.add(selectedUser.getUser());
            }
        }

        mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, budget));
        JOptionPane.showMessageDialog(this, "Expenses distributed custom.");
    }

    public void addCustomDistributionRow() {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel userLabel = new JLabel("Select User:");
        JComboBox<BudgetAccess> userComboBox = new JComboBox<>(getUsers());
        JTextField percentageField = new JTextField(5);
        rowPanel.add(userLabel);
        rowPanel.add(userComboBox);
        rowPanel.add(new JLabel("Percentage:"));
        rowPanel.add(percentageField);
        customDistributionPanel.add(rowPanel);
        customDistributionPanel.revalidate();
        customDistributionPanel.repaint();
    }
}