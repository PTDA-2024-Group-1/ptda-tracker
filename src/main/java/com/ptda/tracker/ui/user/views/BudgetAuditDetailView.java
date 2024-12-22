package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAuditService;
import com.ptda.tracker.services.tracker.BudgetService;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.ScreenNames;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BudgetAuditDetailView extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BudgetAuditDetailView.class);

    private final MainFrame mainFrame;
    private final Budget budget;
    private final BudgetAuditService budgetAuditService;
    private final ExpenseAuditService expenseAuditService;
    private final ExpenseService expenseService;

    // UI components
    private JTextArea auditDetailsArea;
    private JTable revisionsTable;
    private JButton revertButton;

    public BudgetAuditDetailView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.budgetAuditService = mainFrame.getContext().getBean(BudgetAuditService.class);
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);

        initComponents();
        populateAuditDetails();
        populateRevisionsTable(); // Populate revisions table
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Audit Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Split Pane to divide audit details and revisions
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(400);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        // Left side: Audit Details Area
        auditDetailsArea = new JTextArea();
        auditDetailsArea.setEditable(false);
        JScrollPane auditScrollPane = new JScrollPane(auditDetailsArea);
        splitPane.setLeftComponent(auditScrollPane);

        // Right side: Revisions Table and Revert Button
        JPanel revisionsPanel = new JPanel(new BorderLayout(10, 10));

        // Revisions Table
        revisionsTable = new JTable(new DefaultTableModel(new String[]{"Revision #", "Date", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        JScrollPane revisionsScrollPane = new JScrollPane(revisionsTable);
        revisionsPanel.add(revisionsScrollPane, BorderLayout.CENTER);

        // Revert Button
        revertButton = new JButton("Revert to Selected Revision");
        revertButton.addActionListener(e -> revertSelectedRevision());
        JPanel revertButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        revertButtonPanel.add(revertButton);
        revisionsPanel.add(revertButtonPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(revisionsPanel);
        add(splitPane, BorderLayout.CENTER);

        // Footer Panel with Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Populates the audit details text area with budget and expense changes.
     */
    private void populateAuditDetails() {
        StringBuilder auditInfo = new StringBuilder();

        try {
            // Fetch and append budget audit details
            List<Object[]> budgetAuditDetails = budgetAuditService.getBudgetRevisionsWithDetails(budget.getId());
            for (Object[] detail : budgetAuditDetails) {
                // Assuming detail structure: [Budget, DefaultRevisionEntity, RevisionType]
                Budget oldBudget = (Budget) detail[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                RevisionType revisionType = (RevisionType) detail[2];

                auditInfo.append("Budget Revision #")
                        .append(revisionEntity.getId())
                        .append(" (")
                        .append(revisionType)
                        .append(") on ")
                        .append(revisionEntity.getRevisionDate())
                        .append(": Name = ")
                        .append(oldBudget.getName())
                        .append(", Description = ")
                        .append(oldBudget.getDescription())
                        .append("\n");
            }

            // Fetch and append expense audit details
            List<Expense> expenses = expenseService.getAllByBudgetId(budget.getId());
            for (Expense expense : expenses) {
                List<Object[]> expenseAuditDetails = expenseAuditService.getExpenseRevisionsWithDetails(expense.getId());
                for (Object[] detail : expenseAuditDetails) {
                    // Assuming detail structure: [Expense, DefaultRevisionEntity, RevisionType]
                    Expense oldExpense = (Expense) detail[0];
                    DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                    RevisionType revisionType = (RevisionType) detail[2];

                    auditInfo.append("Expense Revision #")
                            .append(revisionEntity.getId())
                            .append(" (")
                            .append(revisionType)
                            .append(") on ")
                            .append(revisionEntity.getRevisionDate())
                            .append(": Title = ")
                            .append(oldExpense.getTitle())
                            .append(", Amount = ")
                            .append(oldExpense.getAmount())
                            .append("\n");
                }
            }

            auditDetailsArea.setText(auditInfo.toString());
        } catch (Exception ex) {
            logger.error("Error populating audit details for Budget ID: " + budget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while loading audit details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Populates the revisions table with budget revision information.
     */
    private void populateRevisionsTable() {
        DefaultTableModel model = (DefaultTableModel) revisionsTable.getModel();
        model.setRowCount(0); // Clear existing rows

        try {
            List<Object[]> budgetAuditDetails = budgetAuditService.getBudgetRevisionsWithDetails(budget.getId());
            for (Object[] detail : budgetAuditDetails) {
                // Assuming detail structure: [Budget, DefaultRevisionEntity, RevisionType]
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                RevisionType revisionType = (RevisionType) detail[2];

                // Ensure that revisionEntity.getId() is treated as a Number
                Number revisionId = revisionEntity.getId();

                Object[] row = new Object[]{
                        revisionId, // Could be Integer or Long
                        revisionEntity.getRevisionDate(),
                        revisionType
                };
                model.addRow(row);
            }
        } catch (Exception ex) {
            logger.error("Error populating revisions table for Budget ID: " + budget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while loading revisions: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Handles the revert action to a selected budget revision.
     * Updates the view to display the selected revision without persisting changes.
     */
    private void revertSelectedRevision() {
        int selectedRow = revisionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a revision to revert to.",
                    "No Revision Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Retrieve the revision number as a Number to handle both Integer and Long
        Object revisionObj = revisionsTable.getValueAt(selectedRow, 0);
        if (!(revisionObj instanceof Number)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid revision number selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Number revisionNumber = (Number) revisionObj;
        Long revisionLong = revisionNumber.longValue(); // Safely convert to Long

        Budget revertedBudget;
        try {
            // Fetch the budget at the selected revision
            revertedBudget = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionLong);
        } catch (Exception ex) {
            logger.error("Error fetching Budget at Revision #" + revisionLong + " for Budget ID: " + budget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while fetching the selected revision: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (revertedBudget == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to retrieve the selected revision.",
                    "Revert Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Confirm the revert action
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to view revision #" + revisionLong + "? This action will not alter the current budget.",
                "Confirm Revert",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Navigate to BudgetDetailView with the revertedBudget data without persisting
        try {
            mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_DETAIL_VIEW,
                    new BudgetDetailView(mainFrame, revertedBudget, true) // Assuming a flag to indicate read-only
            );
        } catch (Exception ex) {
            logger.error("Error navigating to BudgetDetailView for Budget ID: " + revertedBudget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while displaying the selected revision: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
