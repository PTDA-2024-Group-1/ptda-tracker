package com.ptda.tracker.ui.user.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.forms.BudgetForm;
import com.ptda.tracker.ui.user.components.renderers.BudgetListRenderer;
import com.ptda.tracker.ui.user.views.BudgetDetailView;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.Refreshable;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetsScreen extends JPanel implements Refreshable {
    private final MainFrame mainFrame;
    private final BudgetService budgetService;
    private List<Budget> budgets;

    public BudgetsScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        budgetService = mainFrame.getContext().getBean(BudgetService.class);
        budgets = budgetService.getAllByUserId(UserSession.getInstance().getUser().getId());
        initComponents();
        setBudgetList(budgets);
        setListeners();
    }

    private void setListeners() {
        budgetList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Budget selectedBudget = budgetList.getSelectedValue();
                if (selectedBudget != null) {
                    mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW,
                            new BudgetDetailView(mainFrame, selectedBudget, this::refreshBudgetList));
                    budgetList.clearSelection(); // Clear selection to allow new interaction
                }
            }
        });
        allButton.addActionListener(e -> setBudgetList(budgets));
        favoritesButton.addActionListener(e ->
                setBudgetList(budgets.stream()
                        .filter(Budget::isFavorite)
                        .collect(Collectors.toList())
                )
        );
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
    }

    public void setBudgetList(List<Budget> budgets) {
        DefaultListModel<Budget> model = (DefaultListModel<Budget>) budgetList.getModel();
        model.clear(); // Clear old data
        budgets.forEach(model::addElement); // Add new data
    }

    private void refreshBudgetList() {
        budgetList.clearSelection();
        budgets = budgetService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setBudgetList(budgets);
        budgetList.updateUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(SELECT_BUDGET, SwingConstants.CENTER);
        topPanel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        allButton = new JButton(ALL);
        favoritesButton = new JButton(FAVORITES);

        buttonPanel.add(allButton);
        buttonPanel.add(favoritesButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        budgetList = new JList<>(new DefaultListModel<>());
        budgetList.setCellRenderer(new BudgetListRenderer());
        add(new JScrollPane(budgetList), BorderLayout.CENTER);

        createButton = new JButton(CREATE_NEW_BUDGET);
        add(createButton, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        refreshBudgetList();
    }

    private JList<Budget> budgetList;
    private JButton allButton, favoritesButton, createButton;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            SELECT_BUDGET = localeManager.getTranslation("select_budget_to_view"),
            CREATE_NEW_BUDGET = localeManager.getTranslation("create_new_budget"),
            ALL = localeManager.getTranslation("all"),
            FAVORITES = localeManager.getTranslation("favorites");
}