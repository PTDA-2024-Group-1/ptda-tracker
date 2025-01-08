package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.ExpenseDetailDialog;
import com.ptda.tracker.util.ScreenNames;
import org.hibernate.envers.DefaultRevisionEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpenseAuditListView extends JPanel {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final ExpenseAuditService expenseAuditService;
    private JTable auditTable;

    public ExpenseAuditListView(MainFrame mainFrame, Expense expense) {
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);

        initComponents();
        populateAuditTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Expense Audit Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        auditTable = createAuditTable();
        JScrollPane scrollPane = new JScrollPane(auditTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTable createAuditTable() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"Type", "Date", "Name/Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFocusable(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                viewSelectedRevisionDetails();
            }
        });
        return table;
    }

    private void populateAuditTable() {
        DefaultTableModel model = (DefaultTableModel) auditTable.getModel();
        model.setRowCount(0);

        try {
            List<Object[]> auditDetails = expenseAuditService.getExpenseRevisionsWithDetails(expense.getId());
            for (Object[] detail : auditDetails) {
                Expense expense = (Expense) detail[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                model.addRow(new Object[]{
                        detail[2], // RevisionType ("MOD" or "ADD")
                        revisionEntity.getId(), // Revision ID
                        expense.getTitle() + " / " + expense.getDescription()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading audit details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedRevisionDetails() {
        int selectedRow = auditTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a revision to view details.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String revisionType = auditTable.getValueAt(selectedRow, 0).toString();

            if ("ADD".equals(revisionType)) {
                new ExpenseDetailDialog(mainFrame, expense).setVisible(true);
            } else if ("MOD".equals(revisionType)) {
                long revisionNumber = Long.parseLong(auditTable.getValueAt(selectedRow, 1).toString());
                if (revisionNumber <= 0) {
                    throw new Exception("Invalid revision number.");
                }
                DefaultRevisionEntity revisionEntity = expenseAuditService.getRevisionEntity(revisionNumber); // Fetch revision entity
                Expense modifiedExpense = expenseAuditService.getExpenseAtRevision(expense.getId(), revisionNumber);
                new ExpenseDetailDialog(mainFrame, modifiedExpense, revisionEntity).setVisible(true);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid revision number format.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading revision details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
