package com.ptda.tracker.ui.screens;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.BudgetForm;
import com.ptda.tracker.ui.renderers.BudgetListRenderer;
import com.ptda.tracker.ui.views.BudgetDetailView;
import com.ptda.tracker.util.ScreenNames;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BudgetsScreen extends JPanel {
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
                    mainFrame.registerAndShowScreen(ScreenNames.BUDGET_DETAIL_VIEW, new BudgetDetailView(mainFrame, selectedBudget));
                }
            }
        });

        add(new JScrollPane(budgetList), BorderLayout.CENTER);

        JLabel label = new JLabel("Select a budget to view details", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        JButton createButton = new JButton("Create New Budget");
        styleButton(createButton);
        createButton.addActionListener(e -> {
            // Abrir o BudgetForm no modo de criação
            mainFrame.registerScreen(ScreenNames.BUDGET_FORM, new BudgetForm(mainFrame, this::refreshBudgetList, null));
            mainFrame.showScreen(ScreenNames.BUDGET_FORM);
        });
        add(createButton, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Verde elegante
        button.setForeground(Color.WHITE); // Texto branco
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40)); // Tamanho do botão
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efeito de hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0)); // Verde mais escuro ao passar o mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(56, 56, 56)); // Voltar ao verde original
            }
        });
    }

    private void refreshBudgetList() {
        budgets = budgetService.getAllByUserId(UserSession.getInstance().getUser().getId());
        setBudgetList(budgets);
    }


    public void setBudgetList(List<Budget> budgets) {
        DefaultListModel<Budget> model = (DefaultListModel<Budget>) budgetList.getModel();
        model.clear(); // Clear old data
        budgets.forEach(model::addElement); // Add new data
    }
}
