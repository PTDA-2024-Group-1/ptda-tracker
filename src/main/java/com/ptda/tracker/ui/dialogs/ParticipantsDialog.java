package com.ptda.tracker.ui.dialogs;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.BudgetAccess;
import com.ptda.tracker.models.tracker.BudgetAccessLevel;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.services.tracker.BudgetAccessService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ParticipantsDialog extends JDialog {
    private List<BudgetAccess> accesses;
    private JTable participantsTable;
    private BudgetAccessService budgetAccessService;
    private Budget budget;
    private JButton saveButton;

    private static final String
            TITLE = "Participants",
            REMOVE = "Remove",
            SAVE = "Save",
            NAME = "Name",
            EMAIL = "Email",
            ACCESS_LEVEL = "Access Level",
            ONLY_OWNER_CAN_REMOVE = "Only the OWNER can remove participants.",
            ONLY_OWNER_CAN_CHANGE_ACCESS = "Only the OWNER can change access levels.",
            PARTICIPANT_REMOVED_SUCCESS = "Participant removed successfully.",
            ACCESS_LEVEL_CHANGED_SUCCESS = "Access level changed successfully.",
            SELECT_PARTICIPANT_TO_REMOVE = "Please select a participant to remove.";

    public ParticipantsDialog(MainFrame mainFrame, Budget budget) {
        this.budget = budget;
        this.budgetAccessService = mainFrame.getContext().getBean(BudgetAccessService.class);
        this.accesses = budgetAccessService.getAllByBudgetId(budget.getId());

        initUI();
        setListeners();
    }

    private void initUI() {
        setTitle(TITLE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        participantsTable = createParticipantsTable();
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton removeButton = new JButton(REMOVE);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedParticipant();
            }
        });
        buttonPanel.add(removeButton);

        saveButton = new JButton(SAVE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAccessLevelChanges();
            }
        });
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createParticipantsTable() {
        String[] columnNames = {NAME, EMAIL, ACCESS_LEVEL};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (BudgetAccess access : accesses) {
            if (access.getAccessLevel() != BudgetAccessLevel.OWNER) {
                model.addRow(new Object[]{access.getUser().getName(), access.getUser().getEmail(), access.getAccessLevel()});
            }
        }

        JTable table = new JTable(model);
        TableColumn accessLevelColumn = table.getColumnModel().getColumn(2);
        JComboBox<BudgetAccessLevel> comboBox = new JComboBox<>(BudgetAccessLevel.values());
        accessLevelColumn.setCellEditor(new DefaultCellEditor(comboBox));

        return table;
    }

    private void setListeners() {
        participantsTable.addMouseListener(new java.awt.event.MouseAdapter() {
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

    private void removeSelectedParticipant() {
        User currentUser = UserSession.getInstance().getUser();
        BudgetAccess currentUserAccess = budgetAccessService.getAllByBudgetId(budget.getId())
                .stream()
                .filter(access -> access.getUser().getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        if (currentUserAccess == null || currentUserAccess.getAccessLevel() != BudgetAccessLevel.OWNER) {
            JOptionPane.showMessageDialog(this, ONLY_OWNER_CAN_REMOVE);
            return;
        }

        int selectedRow = participantsTable.getSelectedRow();
        if (selectedRow >= 0) {
            BudgetAccess selectedAccess = accesses.get(selectedRow);
            budgetAccessService.delete(selectedAccess.getId());
            accesses.remove(selectedRow);
            ((DefaultTableModel) participantsTable.getModel()).removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, PARTICIPANT_REMOVED_SUCCESS);
        } else {
            JOptionPane.showMessageDialog(this, SELECT_PARTICIPANT_TO_REMOVE);
        }
    }

    private void saveAccessLevelChanges() {
        User currentUser = UserSession.getInstance().getUser();
        BudgetAccess currentUserAccess = budgetAccessService.getAllByBudgetId(budget.getId())
                .stream()
                .filter(access -> access.getUser().getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);

        if (currentUserAccess == null || currentUserAccess.getAccessLevel() != BudgetAccessLevel.OWNER) {
            JOptionPane.showMessageDialog(this, ONLY_OWNER_CAN_CHANGE_ACCESS);
            return;
        }

        for (int i = 0; i < participantsTable.getRowCount(); i++) {
            BudgetAccess access = accesses.get(i);
            BudgetAccessLevel newAccessLevel = (BudgetAccessLevel) participantsTable.getValueAt(i, 2);
            if (access.getAccessLevel() != newAccessLevel && access.getAccessLevel() != BudgetAccessLevel.OWNER) {
                access.setAccessLevel(newAccessLevel);
                budgetAccessService.update(access);
            }
        }
        JOptionPane.showMessageDialog(this, ACCESS_LEVEL_CHANGED_SUCCESS);
    }
}