package com.ptda.tracker.ui.forms;

import com.ptda.tracker.models.dispute.Subdivision;
import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.services.tracker.SubdivisionService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.views.ExpenseDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubdivisionForm extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final Budget budget;
    private final BudgetAccessService budgetAccessService;
    private final SubdivisionService subdivisionService;
    private final List<Subdivision> subdivisions;
    private final Runnable onBack;

    private JRadioButton equitableRadioButton, customRadioButton;
    private JPanel customDistributionPanel;

    private static final String
            DISTRIBUTION_TYPE = "Distribution Type:",
            EQUITABLE = "Equitable",
            CUSTOM = "Custom",
            CUSTOM_DISTRIBUTION = "Custom Distribution",
            CANCEL = "Cancel",
            DISTRIBUTE = "Distribute",
            DISTRIBUTION_COMPLETE = "Distribution complete (100%).",
            DISTRIBUTION_SAVED = "Distribution saved. Remaining percentage to distribute: ",
            REMAINING_PERCENTAGE_DISTRIBUTED = "Remaining percentage distributed equitably. Each user received: ",
            NO_USERS_LEFT = "No users left to distribute the remaining percentage.",
            INVALID_PERCENTAGE = "Invalid percentage value.",
            PERCENTAGE_GREATER_THAN_ZERO = "Percentage must be greater than 0.",
            TOTAL_PERCENTAGE_EXCEED = "The total percentage must not exceed 100%. Remaining percentage: ",
            USER_INCLUDED_ONCE = "Each user can only be included once per expense.",
            DISTRIBUTION_ALREADY_COMPLETE = "The distribution is already complete (100%). No remaining percentage to distribute.",
            PERCENTAGE = "Percentage";

    public SubdivisionForm(MainFrame mainFrame, Expense expense, Budget budget, Runnable onBack) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.budget = budget;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.subdivisionService = mainFrame.getContext().getBean(SubdivisionService.class);
        this.subdivisions = subdivisionService.getAllByExpenseId(expense.getId());
        this.onBack = onBack;

        initUI();
        setListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Distribution Type RadioButtons
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel distributionLabel = new JLabel(DISTRIBUTION_TYPE);
        distributionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(distributionLabel, gbc);

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

        // Custom Distribution Panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        customDistributionPanel = new JPanel();
        customDistributionPanel.setLayout(new BoxLayout(customDistributionPanel, BoxLayout.Y_AXIS));
        customDistributionPanel.setBorder(BorderFactory.createTitledBorder(CUSTOM_DISTRIBUTION));
        customDistributionPanel.setVisible(false);
        formPanel.add(customDistributionPanel, gbc);

        // Buttons Panel
        gbc.gridy = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW));
        buttonsPanel.add(cancelButton);

        JButton distributeButton = new JButton(DISTRIBUTE);
        distributeButton.addActionListener(e -> distributeExpenses());
        buttonsPanel.add(distributeButton);

        formPanel.add(buttonsPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void setListeners() {
        customRadioButton.addActionListener(e -> {
            customDistributionPanel.setVisible(true);
            addCustomDistributionRows();
        });
        equitableRadioButton.addActionListener(e -> customDistributionPanel.setVisible(false));
    }

    private void distributeExpenses() {
        if (equitableRadioButton.isSelected()) {
            distributeEquitably();
        } else if (customRadioButton.isSelected()) {
            distributeCustom();
        }
    }

    private BudgetAccess[] getUsers(Budget budget, BudgetAccessService budgetAccessService) {
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budget.getId());
        return accesses.toArray(new BudgetAccess[0]);
    }

    private void distributeEquitably() {
        Expense selectedExpense = expense;
        List<Subdivision> existingSubdivisions = subdivisionService.getAllByExpenseId(selectedExpense.getId());
        double totalPercentage = existingSubdivisions.stream().mapToDouble(Subdivision::getPercentage).sum();
        double remainingPercentage = 100 - totalPercentage;
        User currentUser = UserSession.getInstance().getUser();

        if (remainingPercentage <= 0) {
            JOptionPane.showMessageDialog(this, DISTRIBUTION_ALREADY_COMPLETE);
            return;
        }

        // Filtrar os utilizadores que já têm subdivisão
        List<User> includedUsers = existingSubdivisions.stream()
                .map(Subdivision::getUser)
                .collect(Collectors.toList());

        List<User> usersToDistribute = new ArrayList<>();
        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel panel) {
                JLabel userLabel = (JLabel) panel.getComponent(0);
                String userName = userLabel.getText().replace(":", "").trim();
                BudgetAccess selectedUser = getUserByName(userName);
                if (selectedUser != null && !includedUsers.contains(selectedUser.getUser())) {
                    usersToDistribute.add(selectedUser.getUser());
                }
            }
        }

        if (usersToDistribute.isEmpty()) {
            JOptionPane.showMessageDialog(this, NO_USERS_LEFT);
            return;
        }

        // Calcular a percentagem equitativa para cada utilizador restante
        double equalPercentage = remainingPercentage / usersToDistribute.size();

        for (User user : usersToDistribute) {
            double amount = expense.getAmount() * (equalPercentage / 100);

            Subdivision subdivision = Subdivision.builder()
                    .expense(expense)
                    .user(user)
                    .amount(amount)
                    .percentage(equalPercentage)
                    .createdBy(currentUser)
                    .build();

            subdivisions.add(subdivision);
            subdivisionService.create(subdivision);
        }

        JOptionPane.showMessageDialog(this, REMAINING_PERCENTAGE_DISTRIBUTED + equalPercentage + "%.");

        if (onBack != null) {
            onBack.run();
        } else {
            mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, ScreenNames.EXPENSE_DETAIL_VIEW, () -> {}));
        }
    }

    private void distributeCustom() {
        Expense selectedExpense = expense;
        List<Subdivision> existingSubdivisions = subdivisionService.getAllByExpenseId(selectedExpense.getId());
        double totalPercentage = existingSubdivisions.stream().mapToDouble(Subdivision::getPercentage).sum();
        double remainingPercentage = 100 - totalPercentage;
        User currentUser = UserSession.getInstance().getUser();

        if (remainingPercentage <= 0) {
            JOptionPane.showMessageDialog(this, DISTRIBUTION_ALREADY_COMPLETE);
            return;
        }

        List<User> includedUsers = new ArrayList<>();
        for (Subdivision subdivision : existingSubdivisions) {
            includedUsers.add(subdivision.getUser());
        }

        double newTotalPercentage = totalPercentage;
        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel panel) {
                JLabel userLabel = (JLabel) panel.getComponent(0);
                JTextField percentageField = (JTextField) panel.getComponent(2);

                String userName = userLabel.getText().replace(":", "").trim();
                BudgetAccess selectedUser = getUserByName(userName);
                if (selectedUser == null) continue;

                if (includedUsers.contains(selectedUser.getUser())) {
                    JOptionPane.showMessageDialog(this, USER_INCLUDED_ONCE);
                    return;
                }

                double percentage;
                try {
                    percentage = Double.parseDouble(percentageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, INVALID_PERCENTAGE);
                    return;
                }

                if (percentage <= 0) {
                    JOptionPane.showMessageDialog(this, PERCENTAGE_GREATER_THAN_ZERO);
                    return;
                }

                newTotalPercentage += percentage;
                if (newTotalPercentage > 100) {
                    JOptionPane.showMessageDialog(this, TOTAL_PERCENTAGE_EXCEED + remainingPercentage + "%.");
                    return;
                }
            }
        }

        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel panel) {
                JLabel userLabel = (JLabel) panel.getComponent(0);
                JTextField percentageField = (JTextField) panel.getComponent(2);

                String userName = userLabel.getText().replace(":", "").trim();
                BudgetAccess selectedUser = getUserByName(userName);
                if (selectedUser == null) continue;

                double percentage = Double.parseDouble(percentageField.getText());
                double amount = expense.getAmount() * (percentage / 100);

                Subdivision subdivision = Subdivision.builder()
                        .expense(expense)
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

        double remainingAfterDistribution = 100 - newTotalPercentage;
        if (remainingAfterDistribution > 0) {
            JOptionPane.showMessageDialog(this, DISTRIBUTION_SAVED + remainingAfterDistribution + "%.");
        } else {
            JOptionPane.showMessageDialog(this, DISTRIBUTION_COMPLETE);
        }

        if (onBack != null) {
            onBack.run();
        }
    }

    private BudgetAccess getUserByName(String userName) {
        for (BudgetAccess access : getUsers(budget, budgetAccessService)) {
            if (access.getUser().getName().equals(userName)) {
                return access;
            }
        }
        return null;
    }

    private void addCustomDistributionRows() {
        customDistributionPanel.removeAll();
        BudgetAccess[] users = getUsers(budget, budgetAccessService);
        List<User> includedUsers = new ArrayList<>();
        for (Subdivision subdivision : subdivisions) {
            includedUsers.add(subdivision.getUser());
        }
        for (BudgetAccess user : users) {
            if (includedUsers.contains(user.getUser())) {
                continue; // Skip users already included
            }
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel userLabel = new JLabel(user.getUser().getName() + ":");
            JTextField percentageField = new JTextField(5);
            rowPanel.add(userLabel);
            rowPanel.add(new JLabel(PERCENTAGE + ":"));
            rowPanel.add(percentageField);
            customDistributionPanel.add(rowPanel);
        }
        customDistributionPanel.revalidate();
        customDistributionPanel.repaint();
    }
}