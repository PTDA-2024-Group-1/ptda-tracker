package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.BudgetForm;
import com.ptda.tracker.ui.user.components.renderers.BudgetListRenderer;
import com.ptda.tracker.ui.user.views.BudgetDetailView;
import com.ptda.tracker.util.Refreshable;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetsScreen extends JPanel implements Refreshable {
    private final BudgetService budgetService;
    private final JList<Budget> budgetList;
    private List<Budget> budgets;

    public BudgetsScreen(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        budgetList = new JList<>(new DefaultListModel<>());
        budgetList.setCellRenderer(new BudgetListRenderer());
        budgetService = mainFrame.getContext().getBean(BudgetService.class);
        budgets = budgetService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setBudgetList(budgets);

        budgetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Budget selectedBudget = budgetList.getSelectedValue();
                if (selectedBudget != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW,
                            new BudgetDetailView(mainFrame, selectedBudget));
                    budgetList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(SELECT_BUDGET, SwingConstants.CENTER);
        topPanel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton allButton = new JButton("All");
        JButton favoritesButton = new JButton("Favorites");

        allButton.addActionListener(e -> setBudgetList(budgets));
        favoritesButton.addActionListener(e ->
                setBudgetList(budgets.stream()
                        .filter(Budget::isFavorite)
                        .collect(Collectors.toList())
                ));

        buttonPanel.add(allButton);
        buttonPanel.add(favoritesButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(budgetList), BorderLayout.CENTER);

        JButton createButton = new JButton(CREATE_NEW_BUDGET);
        createButton.addActionListener(e -> {
            // Open BudgetForm in creation mode
            mainFrame.registerScreen(
                    ScreenNames.BUDGET_FORM,
                    new BudgetForm(mainFrame, null,
                            mainFrame.getCurrentScreen(),
                            this::refreshBudgetList)
            );
            mainFrame.showScreen(ScreenNames.BUDGET_FORM);
        });
        add(createButton, BorderLayout.SOUTH);
    }

    private void refreshBudgetList() {
        budgetList.clearSelection();
        budgets = budgetService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setBudgetList(budgets);
        budgetList.updateUI();
    }

    @Override
    public void refresh() {
        refreshBudgetList();
    }

    public void setBudgetList(List<Budget> budgets) {
        DefaultListModel<Budget> model = (DefaultListModel<Budget>) budgetList.getModel();
        model.clear(); // Clear old data
        budgets.forEach(model::addElement); // Add new data
    }

    private static final String
            SELECT_BUDGET = "Select a budget to view details",
            CREATE_NEW_BUDGET = "Create New Budget";
}