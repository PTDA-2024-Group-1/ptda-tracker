package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.ShareBudgetForm;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BudgetDetailView extends JPanel {
    private final BudgetAccessService budgetAccessService;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Budget Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        detailsPanel.add(new JLabel("Name: " + budget.getName(), SwingConstants.LEFT));
        detailsPanel.add(new JLabel("Description: " + budget.getDescription(), SwingConstants.LEFT));
        detailsPanel.add(new JLabel("Created By: " + budget.getCreatedBy().getName(), SwingConstants.LEFT));
        add(detailsPanel, BorderLayout.NORTH);

        // Participants Table
        JTable participantsTable = createParticipantsTable(budget.getId());
        add(new JScrollPane(participantsTable), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back to Budgets");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonsPanel.add(backButton);

        JButton editButton = new JButton("Edit Budget");
        editButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_FORM));
        buttonsPanel.add(editButton);

        JButton shareButton = new JButton("Share Budget");
        shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_SHARE_FORM,
                new ShareBudgetForm(mainFrame, budget)));
        buttonsPanel.add(shareButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTable createParticipantsTable(Long budgetId) {
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budgetId);

        String[] columnNames = {"Name", "Email", "Access Level"};
        String[][] data = new String[accesses.size()][3];
        for (int i = 0; i < accesses.size(); i++) {
            BudgetAccess access = accesses.get(i);
            data[i][0] = access.getUser().getName();
            data[i][1] = access.getUser().getEmail();
            data[i][2] = access.getAccessLevel().toString();
        }

        return new JTable(data, columnNames);
    }
}
