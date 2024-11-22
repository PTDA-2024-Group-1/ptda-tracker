package com.ptda.tracker.ui.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.forms.BudgetForm;
import com.ptda.tracker.ui.forms.ShareBudgetForm;
import com.ptda.tracker.util.ScreenNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// TO-DO - quando se edita ele dá save mas dá erro

public class BudgetDetailView extends JPanel {
    private final BudgetAccessService budgetAccessService;

    public BudgetDetailView(MainFrame mainFrame, Budget budget) {
        budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de detalhes do orçamento
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Budget Details"));

        JLabel nameLabel = new JLabel("Name: " + budget.getName());
        JLabel descriptionLabel = new JLabel("Description: " + budget.getDescription());
        JLabel createdByLabel = new JLabel("Created By: " + budget.getCreatedBy().getName());

        Font font = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        createdByLabel.setFont(font);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createdByLabel);
        add(detailsPanel, BorderLayout.NORTH);

        // Tabela de participantes
        JTable participantsTable = createParticipantsTable(budget.getId());
        participantsTable.setRowHeight(25);
        participantsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        participantsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        add(new JScrollPane(participantsTable), BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botões com estilo e hover
        JButton backButton = createStyledButton("Back to Budgets");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.NAVIGATION_SCREEN));
        buttonsPanel.add(backButton);

        JButton editButton = createStyledButton("Edit Budget");
        editButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_FORM, new BudgetForm(mainFrame, null, budget)));
        buttonsPanel.add(editButton);

        JButton shareButton = createStyledButton("Share Budget");
        shareButton.addActionListener(e -> mainFrame.registerAndShowScreen(ScreenNames.BUDGET_SHARE_FORM,
                new ShareBudgetForm(mainFrame, budget)));
        buttonsPanel.add(shareButton);

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTable createParticipantsTable(Long budgetId) {
        List<BudgetAccess> accesses = budgetAccessService.getAllByBudgetId(budgetId);

        String[] columnNames = {"Name", "Email", "Access Level"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (BudgetAccess access : accesses) {
            model.addRow(new Object[]{
                    access.getUser().getName(),
                    access.getUser().getEmail(),
                    access.getAccessLevel().toString()
            });
        }

        JTable table = new JTable(model);
        table.setEnabled(false); // Desativar edição
        return table;
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(56, 56, 56)); // Cor inicial do botão
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding no botão

        // Efeito de hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 0)); // Cor ao passar o mouse
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 56, 56)); // Cor ao sair com o mouse
            }
        });

        return button;
    }
}
