package com.ptda.tracker.ui.dialogs;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.util.List;

public class ParticipantsDialog extends JDialog {
    private List<BudgetAccess> accesses;
    private JTable participantsTable;

    public ParticipantsDialog(MainFrame mainFrame, Budget budget) {
        accesses = mainFrame.getContext().getBean(BudgetAccessService.class).getAllByBudgetId(budget.getId());

        initUI();
        setListeners();
    }

    private void initUI() {
        setTitle("Participants");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        participantsTable = createParticipantsTable();
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        add(scrollPane);
    }

    private JTable createParticipantsTable() {
        String[] columnNames = {"Name", "Email", "Access Level"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (BudgetAccess access : accesses) {
            model.addRow(new Object[]{access.getUser().getName(), access.getUser().getEmail(), access.getAccessLevel().toString()});
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        return table;
    }

    private void setListeners() {
        participantsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = participantsTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    BudgetAccess selectedAccess = accesses.get(row);
                    ProfileDialog profileDialog = new ProfileDialog(selectedAccess.getUser());
                    profileDialog.setVisible(true);
                }
            }
        });
    }
}
