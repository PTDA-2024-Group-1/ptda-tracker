package com.ptda.tracker.ui.user.views;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.services.tracker.BudgetAuditService;
import com.ptda.tracker.services.tracker.ExpenseAuditService;
import com.ptda.tracker.services.tracker.ExpenseService;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.ui.user.dialogs.BudgetDetailDialog;
import com.ptda.tracker.ui.user.dialogs.ExpenseDetailDialog;
import com.ptda.tracker.util.LocaleManager;
import com.ptda.tracker.util.ScreenNames;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BudgetAuditListView extends JPanel {
    private final MainFrame mainFrame;
    private final BudgetAuditService budgetAuditService;
    private final ExpenseAuditService expenseAuditService;
    private final ExpenseService expenseService;
    private final List<Long> revisionNumbers;
    private final Budget budget;

    public BudgetAuditListView(MainFrame mainFrame, Budget budget) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.budgetAuditService = mainFrame.getContext().getBean(BudgetAuditService.class);
        this.expenseAuditService = mainFrame.getContext().getBean(ExpenseAuditService.class);
        this.expenseService = mainFrame.getContext().getBean(ExpenseService.class);
        this.revisionNumbers = new ArrayList<>();

        initComponents();
        populateRevisionsTable();
    }

    private void populateRevisionsTable() {
        DefaultTableModel model = (DefaultTableModel) revisionsTable.getModel();
        model.setRowCount(0);
        revisionNumbers.clear();

        try {
            // Fetch revisions and expenses
            List<Object[]> budgetRevisions = budgetAuditService.getBudgetRevisionsWithDetails(budget.getId());
            List<Object[]> expenseRevisions = expenseAuditService.getExpenseRevisionsWithDetails(budget.getId());
            List<Expense> allExpenses = expenseService.getAllByBudgetId(budget.getId());

            List<Object[]> combinedRevisions = new ArrayList<>();
            combinedRevisions.addAll(budgetRevisions);
            combinedRevisions.addAll(expenseRevisions);

            // Add expenses as "CREATE" entries if they are not in revisions
            for (Expense expense : allExpenses) {
                boolean isAdded = expenseRevisions.stream().anyMatch(revision ->
                        ((Expense) revision[0]).getId().equals(expense.getId())
                );

                if (!isAdded) {
                    DefaultRevisionEntity mockRevisionEntity = new DefaultRevisionEntity();
                    mockRevisionEntity.setTimestamp(expense.getCreatedAt());

                    combinedRevisions.add(new Object[]{
                            expense,
                            mockRevisionEntity,
                            RevisionType.ADD
                    });
                }
            }

            // Sort by date (handle mocked revisions with createdDate properly)
            combinedRevisions.sort((o1, o2) -> {
                Date date1 = ((DefaultRevisionEntity) o1[1]).getRevisionDate();
                Date date2 = ((DefaultRevisionEntity) o2[1]).getRevisionDate();
                return date2.compareTo(date1);
            });

            // Populate rows
            populateTableRows(combinedRevisions, model);
        } catch (Exception ex) {
            showErrorDialog(ERROR_OCCURRED + ex.getMessage());
        }
    }

    private void populateTableRows(List<Object[]> revisions, DefaultTableModel model) {
        Set<String> uniqueEntries = new HashSet<>();

        for (Object[] revision : revisions) {
            Object entity = revision[0];
            DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) revision[1];
            RevisionType revisionType = (RevisionType) revision[2];

            String entityType = (entity instanceof Budget) ? "Budget" : "Expense";
            String revisionTypeName = (revisionType == RevisionType.ADD && "Budget".equals(entityType)) ? "CREATE" : revisionType.toString();

            String uniqueKey = entity.toString() + "||" + revisionEntity.getId();
            if (uniqueEntries.add(uniqueKey)) {
                revisionNumbers.add((long) revisionEntity.getId());

                String nameOrDescription = (entity instanceof Budget) ?
                        ((Budget) entity).getName() + " / " + ((Budget) entity).getDescription() :
                        ((Expense) entity).getTitle() + " / " + ((Expense) entity).getDescription();

                model.addRow(new Object[] {
                        revisionTypeName,
                        revisionEntity.getRevisionDate(),
                        entityType,
                        nameOrDescription
                });
            }
        }
    }

    private void viewSelectedRevisionDetails() {
        int selectedRow = revisionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningDialog(SELECT_REVISION);
            return;
        }

        try {
            String entityType = (String) revisionsTable.getValueAt(selectedRow, 2);
            String revisionTypeName = (String) revisionsTable.getValueAt(selectedRow, 0);

            if ("Budget".equals(entityType)) {
                if ("CREATE".equals(revisionTypeName)) {
                    new BudgetDetailDialog(mainFrame, budget).setVisible(true);
                } else {
                    long revisionNumber = revisionNumbers.get(selectedRow);
                    DefaultRevisionEntity revisionEntity = budgetAuditService.getRevisionEntity(revisionNumber); // Fetch revision entity
                    Budget budgetRevision = budgetAuditService.getBudgetAtRevision(budget.getId(), revisionNumber);
                    new BudgetDetailDialog(mainFrame, budgetRevision, revisionEntity).setVisible(true);
                }
            } else if ("Expense".equals(entityType)) {
                if ("CREATE".equals(revisionTypeName) || "ADD".equals(revisionTypeName)) {
                    String name = revisionsTable.getValueAt(selectedRow, 3).toString().split(" / ")[0];
                    Expense expense = expenseService.getAllByBudgetId(budget.getId()).stream()
                            .filter(e -> e.getTitle().equals(name))
                            .findFirst().orElseThrow(() -> new Exception(EXPENSE_NOT_FOUND));
                    new ExpenseDetailDialog(mainFrame, expense).setVisible(true);
                } else {
                    long revisionNumber = revisionNumbers.get(selectedRow);
                    DefaultRevisionEntity revisionEntity = expenseAuditService.getRevisionEntity(revisionNumber); // Fetch revision entity
                    Expense expenseRevision = expenseAuditService.getExpenseAtRevision(budget.getId(), revisionNumber);
                    new ExpenseDetailDialog(mainFrame, expenseRevision, revisionEntity).setVisible(true);
                }
            }
        } catch (Exception ex) {
            showErrorDialog(ERROR_FETCHING_REVISION_DETAILS + ex.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, ERROR, JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, WARNING, JOptionPane.WARNING_MESSAGE);
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel(AUDIT_DETAILS, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        return titleLabel;
    }

    private JTable createRevisionsTable() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{TYPE, DATE, ENTITY, NAME_DESCRIPTION}, 0) {
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

    private JPanel createFooterPanel() {
        JButton backButton = new JButton(BACK);
        backButton.addActionListener(e -> mainFrame.showScreen(ScreenNames.BUDGET_DETAIL_VIEW));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.add(backButton);
        return footerPanel;
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = createTitleLabel();
        add(titleLabel, BorderLayout.NORTH);

        revisionsTable = createRevisionsTable();
        JScrollPane revisionsScrollPane = new JScrollPane(revisionsTable);
        add(revisionsScrollPane, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTable revisionsTable;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            ERROR_OCCURRED = localeManager.getTranslation("error_occurred"),
            WARNING = localeManager.getTranslation("warning"),
            ERROR = localeManager.getTranslation("error"),
            CREATE = localeManager.getTranslation("create"),
            SELECT_REVISION = localeManager.getTranslation("select_revision"),
            AUDIT_DETAILS = localeManager.getTranslation("audit_details"),
            ERROR_FETCHING_REVISION_DETAILS = localeManager.getTranslation("error_fetching_revision_details"),
            EXPENSE_NOT_FOUND = localeManager.getTranslation("expense_not_found"),
            TYPE = localeManager.getTranslation("type"),
            DATE = localeManager.getTranslation("date"),
            ENTITY = localeManager.getTranslation("entity"),
            NAME_DESCRIPTION = localeManager.getTranslation("name_description"),
            BACK = localeManager.getTranslation("back");
}
