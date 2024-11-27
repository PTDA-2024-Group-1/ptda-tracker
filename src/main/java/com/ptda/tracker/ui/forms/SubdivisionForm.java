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

    public SubdivisionForm(MainFrame mainFrame, Expense expense, Budget budget, BudgetAccessService budgetAccessService, Runnable onBack) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.budget = budget;
        this.budgetAccessService = budgetAccessService;
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
        JLabel distributionLabel = new JLabel("Distribution Type:");
        distributionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(distributionLabel, gbc);

        gbc.gridx = 1;
        JPanel distributionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        equitableRadioButton = new JRadioButton("Equitable");
        customRadioButton = new JRadioButton("Custom");
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
        customDistributionPanel.setBorder(BorderFactory.createTitledBorder("Custom Distribution"));
        customDistributionPanel.setVisible(false);
        formPanel.add(customDistributionPanel, gbc);

        // Buttons Panel
        gbc.gridy = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW));
        buttonsPanel.add(cancelButton);

        JButton distributeButton = new JButton("Distribute");
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

        if (!existingSubdivisions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This expense has already been subdivided and cannot be subdivided equitably again.");
            return;
        }

        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(expense.getBudget().getId());
        double amountPerUser = expense.getAmount() / accesses.size();
        User currentUser = UserSession.getInstance().getUser();

        for (BudgetAccess access : accesses) {
            Subdivision subdivision = Subdivision.builder()
                    .expense(expense)
                    .user(access.getUser())
                    .amount(amountPerUser)
                    .percentage(100.0 / accesses.size())
                    .createdBy(currentUser)
                    .build();
            subdivisions.add(subdivision);
            subdivisionService.create(subdivision);
        }

        if (onBack != null) {
            onBack.run();
        } else {
            mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, ScreenNames.EXPENSE_DETAIL_VIEW, () -> {}));
        }
        JOptionPane.showMessageDialog(this, "Expenses distributed equitably.");
    }

    private void distributeCustom() {
        Expense selectedExpense = expense;
        List<Subdivision> existingSubdivisions = subdivisionService.getAllByExpenseId(selectedExpense.getId());
        double totalPercentage = existingSubdivisions.stream().mapToDouble(Subdivision::getPercentage).sum();
        User currentUser = UserSession.getInstance().getUser();

        List<User> includedUsers = new ArrayList<>();
        for (Subdivision subdivision : existingSubdivisions) {
            includedUsers.add(subdivision.getUser());
        }

        double newTotalPercentage = totalPercentage;
        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                JLabel userLabel = (JLabel) panel.getComponent(0);
                JTextField percentageField = (JTextField) panel.getComponent(2);

                String userName = userLabel.getText().replace(":", "").trim();
                BudgetAccess selectedUser = getUserByName(userName);
                if (selectedUser == null) continue;

                if (includedUsers.contains(selectedUser.getUser())) {
                    JOptionPane.showMessageDialog(this, "Each user can only be included once per expense.");
                    return;
                }

                double percentage;
                try {
                    percentage = Double.parseDouble(percentageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid percentage value.");
                    return;
                }

                newTotalPercentage += percentage;

                if (newTotalPercentage > 100) {
                    JOptionPane.showMessageDialog(this, "The total percentage must not exceed 100%.");
                    return;
                }
            }
        }

        for (Component component : customDistributionPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
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

        if (onBack != null) {
            onBack.run();
        } else {
            mainFrame.registerAndShowScreen(ScreenNames.EXPENSE_DETAIL_VIEW, new ExpenseDetailView(mainFrame, expense, ScreenNames.EXPENSE_DETAIL_VIEW, () -> {}));
        }
        JOptionPane.showMessageDialog(this, "Expenses distributed custom.");
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
        for (BudgetAccess user : users) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel userLabel = new JLabel(user.getUser().getName() + ":");
            JTextField percentageField = new JTextField(5);
            rowPanel.add(userLabel);
            rowPanel.add(new JLabel("Percentage:"));
            rowPanel.add(percentageField);
            customDistributionPanel.add(rowPanel);
        }
        customDistributionPanel.revalidate();
        customDistributionPanel.repaint();
    }
}