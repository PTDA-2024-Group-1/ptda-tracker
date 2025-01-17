package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.ExpenseDetailDialog;
import com.ptda.tracker.util.DateFormatManager;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import org.hibernate.envers.DefaultRevisionEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
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

        JLabel titleLabel = new JLabel(EXPENSE_AUDIT_DETAILS, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        auditTable = createAuditTable();
        JScrollPane scrollPane = new JScrollPane(auditTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.EXPENSE_DETAIL_VIEW));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTable createAuditTable() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{TYPE, DATE, NAME_DESCRIPTION}, 0) {
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
                        DATE_FORMAT.format(revisionEntity.getRevisionDate()),
                        expense.getTitle() + " / " + expense.getDescription()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ERROR_LOADING_AUDIT_DETAILS + ex.getMessage(),
                    ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedRevisionDetails() {
        int selectedRow = auditTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, PLEASE_SELECT_A_REVISION_TO_VIEW_DETAILS,
                    WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String revisionType = auditTable.getValueAt(selectedRow, 0).toString();

            if ("ADD".equals(revisionType)) {
                new ExpenseDetailDialog(mainFrame, expense).setVisible(true);
            } else if ("MOD".equals(revisionType)) {
                long revisionNumber = Long.parseLong(auditTable.getValueAt(selectedRow, 1).toString());
                if (revisionNumber <= 0) {
                    throw new Exception(ERROR_INVALID_REVISION_NUMBER_FORMAT);
                }
                DefaultRevisionEntity revisionEntity = expenseAuditService.getRevisionEntity(revisionNumber); // Fetch revision entity
                Expense modifiedExpense = expenseAuditService.getExpenseAtRevision(expense.getId(), revisionNumber);
                new ExpenseDetailDialog(mainFrame, modifiedExpense, revisionEntity).setVisible(true);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ERROR_INVALID_REVISION_NUMBER_FORMAT,
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ERROR_LOADING_REVISION_DETAILS + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DateFormatManager.getInstance().getDateFormat() + " HH:mm:ss");
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            EXPENSE_AUDIT_DETAILS = localeManager.getTranslation("expense_audit_details"),
            BACK = localeManager.getTranslation("back"),
            ERROR = localeManager.getTranslation("error"),
            WARNING = localeManager.getTranslation("warning"),
            PLEASE_SELECT_A_REVISION_TO_VIEW_DETAILS = localeManager.getTranslation("please_select_a_revision_to_view_details"),
            ERROR_INVALID_REVISION_NUMBER_FORMAT = localeManager.getTranslation("error_invalid_revision_number_format"),
            ERROR_LOADING_AUDIT_DETAILS = localeManager.getTranslation("error_loading_audit_details"),
            ERROR_LOADING_REVISION_DETAILS = localeManager.getTranslation("error_loading_revision_details"),
            TYPE = localeManager.getTranslation("type"),
            DATE = localeManager.getTranslation("date"),
            NAME_DESCRIPTION = localeManager.getTranslation("name_description");
}
