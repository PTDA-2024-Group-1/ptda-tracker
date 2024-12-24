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
import java.util.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<<<<<<< HEAD

import com.ptda.tracker.ui.user.screens.NavigationScreen;
=======
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66

public class BudgetAuditDetailView extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(BudgetAuditDetailView.class);

    private final MainFrame mainFrame;
    private Budget budget;
    private final BudgetAuditService budgetAuditService;
    private final ExpenseAuditService expenseAuditService;
    private final ExpenseService expenseService;
<<<<<<< HEAD
    private final BudgetService budgetService; 
=======
    private final BudgetService budgetService; // Added BudgetService
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66

    // UI components
    private JTable revisionsTable;
    private JButton revertButton;
    private JButton viewDetailsButton;
    private final List<Long> revisionNumbers = new ArrayList<>();

    public BudgetAuditDetailView(MainFrame mainFrame, Budget budget) {
        this(mainFrame, budget, false); 
    }

    public BudgetAuditDetailView(MainFrame mainFrame, Budget budget, boolean isReadOnly) {
        this.mainFrame = mainFrame;
        this.budgetAuditService = mainFrame.getContext().getBean(BudgetAuditService.class);
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.budgetService = mainFrame.getContext().getBean(BudgetService.class); // Initialize BudgetService
<<<<<<< HEAD

        this.budget = budget;
        this.revisionNumbers.clear();
=======
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66

        initComponents();
        populateRevisionsTable(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Audit Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));


        revisionsTable = new JTable(new DefaultTableModel(
                new String[]{"Type", "Date", "Name", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        revisionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane revisionsScrollPane = new JScrollPane(revisionsTable);
        mainPanel.add(revisionsScrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Revert Button
        revertButton = new JButton("Revert to Selected Revision");
        revertButton.addActionListener(e -> revertSelectedRevision());
        actionsPanel.add(revertButton);

        viewDetailsButton = new JButton("View Revision Details");
        viewDetailsButton.addActionListener(e -> viewSelectedRevisionDetails());
        actionsPanel.add(viewDetailsButton);

        mainPanel.add(actionsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Footer Panel with Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }


<<<<<<< HEAD
    private void populateRevisionsTable() {
        DefaultTableModel model = (DefaultTableModel) revisionsTable.getModel();
        model.setRowCount(0); 
        revisionNumbers.clear(); 

        try {
            List<Object[]> allDetails = budgetAuditService.getBudgetRevisionsWithDetails(budget.getId());

            Set<String> uniqueStates = new HashSet<>();
            List<Object[]> filteredDetails = new ArrayList<>();

            for (Object[] detail : allDetails) {
                Budget currentBudget = (Budget) detail[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                RevisionType revisionType = (RevisionType) detail[2];

                 String stateKey = currentBudget.getName() + "||" + currentBudget.getDescription();

                if (!uniqueStates.contains(stateKey)) {
                    uniqueStates.add(stateKey);
                    filteredDetails.add(detail);
                }
                 }

            for (Object[] detail : filteredDetails) {
                Budget currentBudget = (Budget) detail[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) detail[1];
                RevisionType revisionType = (RevisionType) detail[2];

                // Store the revision number in the internal list, casting int to long
                revisionNumbers.add((long) revisionEntity.getId());

                // Add the row to the table without the revision number
                Object[] row = new Object[]{
                        revisionType,
                        revisionEntity.getRevisionDate(),
                        currentBudget.getName(),
                        currentBudget.getDescription()
                };
                model.addRow(row);
            }
        } catch (Exception ex) {
            logger.error("Error populating revisions table for Budget ID: " + budget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while loading revisions: " + ex.getMessage(),
=======
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
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Handles the action to view full details of the selected revision.
     * Displays the details in a separate dialog window and updates the NavigationScreen.
     */
<<<<<<< HEAD
    private void viewSelectedRevisionDetails() {
        int selectedRow = revisionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a revision to view details.",
                    "No Revision Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
=======
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
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        }

        // Retrieve the revision number from the internal list
        Long revisionNumber = null;
        try {
            revisionNumber = revisionNumbers.get(selectedRow).longValue();
        } catch (IndexOutOfBoundsException e) {
            logger.error("Selected row index out of bounds: " + selectedRow, e);
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid revision selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Fetch the budget at the selected revision
        Budget selectedRevisionBudget;
        try {
            selectedRevisionBudget = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionNumber);
            if (selectedRevisionBudget == null) {
                throw new Exception("Reverted budget not found.");
            }
        } catch (Exception ex) {
            logger.error("Error fetching Budget at Revision #" + revisionNumber, ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while fetching revision details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Determine the revision type string
        String revisionTypeStr = revisionsTable.getValueAt(selectedRow, 0).toString();

        // Extract the revision date from the table
        String revisionDateStr = revisionsTable.getValueAt(selectedRow, 1).toString();

        // Build the details message
        StringBuilder detailsMessage = new StringBuilder();
        detailsMessage.append(String.format("Revision #%d (%s)\n", revisionNumber, revisionTypeStr));
        detailsMessage.append(String.format("Date: %s\n", revisionDateStr));
        detailsMessage.append(String.format("Name: %s\n", selectedRevisionBudget.getName()));
        detailsMessage.append(String.format("Description: %s\n", selectedRevisionBudget.getDescription()));
        // Add more fields if necessary

        // Display the details in a dialog window
        JOptionPane.showMessageDialog(
                this,
                detailsMessage.toString(),
                "Revision Details",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Set the selected revision in NavigationScreen
        String selectedRevisionInfo = String.format("Type: %s, Date: %s, Name: %s, Description: %s",
                revisionTypeStr, revisionDateStr, selectedRevisionBudget.getName(), selectedRevisionBudget.getDescription());
        NavigationScreen.setSelectedRevision(revisionNumber, selectedRevisionInfo);
    }

    /**
     * Handles the revert action to a selected budget revision.
     * Updates the view to display the selected revision and persists the change.
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

        // Retrieve the revision number from the internal list
        Long revisionLong = null;
        try {
            revisionLong = revisionNumbers.get(selectedRow).longValue();
        } catch (IndexOutOfBoundsException e) {
            logger.error("Selected row index out of bounds: " + selectedRow, e);
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid revision selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Budget revertedBudget;
        try {
            // Fetch the budget at the selected revision
            revertedBudget = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionLong);
            if (revertedBudget == null) {
                throw new Exception("Reverted budget not found.");
            }

<<<<<<< HEAD
=======
        Budget revertedBudget;
        try {
            // Fetch the budget at the selected revision
            revertedBudget = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionLong);
            if (revertedBudget == null) {
                throw new Exception("Reverted budget not found.");
            }

>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
            // Persist the reverted budget to the database
            budgetService.updateWithoutAudit(revertedBudget);
        } catch (Exception ex) {
            logger.error("Error reverting Budget ID: " + budget.getId() + " to Revision #" + revisionLong, ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while reverting the budget: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Confirm the revert action
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Budget has been reverted to revision #" + revisionLong + ". Would you like to view the updated details?",
                "Confirm Revert",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

<<<<<<< HEAD
        // Update the budget reference to the reverted budget
        this.budget = revertedBudget;

        // Determine the revision type string and date
        String revisionTypeStr = revisionsTable.getValueAt(selectedRow, 0).toString();
        String revisionDateStr = revisionsTable.getValueAt(selectedRow, 1).toString();

        // Build the selected revision info string
        String selectedRevisionInfo = String.format("Type: %s, Date: %s, Name: %s, Description: %s",
                revisionTypeStr, revisionDateStr, revertedBudget.getName(), revertedBudget.getDescription());

        // Set the selected revision in NavigationScreen
        NavigationScreen.setSelectedRevision(revisionLong, selectedRevisionInfo);

        // Navigate to BudgetDetailView to display the reverted budget
=======
        // Navigate to BudgetDetailView without read-only flag
>>>>>>> a54db8f7f59dfc13f3e06079a37311993b9cce66
        try {
            // Fetch the latest budget from the database to ensure consistency
            Budget updatedBudget = budgetService.getById(budget.getId())
                    .orElseThrow(() -> new Exception("Budget not found after revert."));
            mainFrame.registerAndShowScreen(
                    ScreenNames.BUDGET_DETAIL_VIEW,
                    new BudgetDetailView(mainFrame, updatedBudget) // Default isReadOnly=false
            );
        } catch (Exception ex) {
            logger.error("Error navigating to BudgetDetailView for Budget ID: " + budget.getId(), ex);
            JOptionPane.showMessageDialog(
                    this,
                    "An error occurred while displaying the reverted budget: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
