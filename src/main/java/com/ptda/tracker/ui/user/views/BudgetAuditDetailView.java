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

public class BudgetAuditDetailView extends JPanel {
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
    }

    /**
     * Populates the revisions table with budget revision information.
     */
    private void populateRevisionsTable() {
        DefaultTableModel model = (DefaultTableModel) revisionsTable.getModel();
        model.setRowCount(0); // Clear existing rows

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
    }

    /**
     * Handles the revert action to a selected budget revision.
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

        // Fetch the budget at the selected revision
        Budget revertedBudget = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionLong);
        if (revertedBudget == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to retrieve the selected revision.",
                    "Revert Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Check if the current budget is already in the state of the selected revision
        boolean isDifferent = false;
        if (!budget.getName().equals(revertedBudget.getName()) ||
                !budget.getDescription().equals(revertedBudget.getDescription()) ||
                budget.isFavorite() != revertedBudget.isFavorite()) {
            isDifferent = true;
        }

        if (!isDifferent) {
            JOptionPane.showMessageDialog(
                    this,
                    "The budget is already in the selected revision state.",
                    "No Changes Needed",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Confirm the revert action
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to revert to revision #" + revisionLong + "?",
                "Confirm Revert",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Update the current budget with reverted data
        budget.setName(revertedBudget.getName());
        budget.setDescription(revertedBudget.getDescription());
        budget.setFavorite(revertedBudget.isFavorite());
        // Add any additional fields as necessary

        // Persist the reverted budget as the latest version
        BudgetService budgetService = mainFrame.getContext().getBean(BudgetService.class);
        try {
            budgetService.update(budget);
            JOptionPane.showMessageDialog(
                    this,
                    "Budget successfully reverted to revision #" + revisionLong + ".",
                    "Revert Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Navigate back to BudgetDetailView to reflect changes
            mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while reverting the budget: " + ex.getMessage(),
                    "Revert Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
