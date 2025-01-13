package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.ui.MainFrame;
import com.ptda.tracker.util.LocaleManager;
import org.hibernate.envers.DefaultRevisionEntity;

import javax.swing.*;

public class ExpenseDetailDialog extends JDialog {
    private final MainFrame mainFrame;
    private final Expense expense;
    private final DefaultRevisionEntity revisionEntity;

    // Constructor for Expense object
    public ExpenseDetailDialog(MainFrame mainFrame, Expense expense) {
        super(mainFrame);
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.revisionEntity = null;

        initComponents();
    }

    // Constructor for Revision Entry
    public ExpenseDetailDialog(MainFrame mainFrame, Expense expense, DefaultRevisionEntity revisionEntity) {
        super(mainFrame);
        this.mainFrame = mainFrame;
        this.expense = expense;
        this.revisionEntity = revisionEntity;

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add components to the content panel
        contentPanel.add(new JLabel(TITLE + ": " + expense.getTitle()));
        contentPanel.add(new JLabel(DESCRIPTION + ": " + expense.getDescription()));
        contentPanel.add(new JLabel(AMOUNT + ": " + expense.getAmount()));
        contentPanel.add(new JLabel(DATE + ": " + expense.getDate()));
        contentPanel.add(new JLabel(CATEGORY + ": " + expense.getCategory().toString()));
        contentPanel.add(new JLabel(BUDGET + ": " + expense.getBudget().getName()));
        contentPanel.add(new JLabel(CREATED_BY + ": " + expense.getCreatedBy().getName()));

        // Add revision-specific info if available
        if (revisionEntity != null) {
            contentPanel.add(new JLabel(REVISION_ID + ": " + revisionEntity.getId()));
            contentPanel.add(new JLabel(REVISION_DATE + ": " + revisionEntity.getRevisionDate()));
        }

        // Add the content panel to the dialog
        add(contentPanel);

        setTitle("Expense Details");
        setModal(true);
        pack();
        setLocationRelativeTo(getParent());
    }

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            TITLE = localeManager.getTranslation("title"),
            DESCRIPTION = localeManager.getTranslation("description"),
            AMOUNT = localeManager.getTranslation("amount"),
            DATE = localeManager.getTranslation("date"),
            CATEGORY = localeManager.getTranslation("category"),
            BUDGET = localeManager.getTranslation("budget"),
            CREATED_BY = localeManager.getTranslation("created.by"),
            REVISION_ID = localeManager.getTranslation("revision.id"),
            REVISION_DATE = localeManager.getTranslation("revision.date");
}
