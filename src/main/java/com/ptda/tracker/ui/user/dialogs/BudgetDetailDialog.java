package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.ui.MainFrame;
import org.hibernate.envers.DefaultRevisionEntity;

import javax.swing.*;

public class BudgetDetailDialog extends JDialog {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final DefaultRevisionEntity revisionEntity;

    // Constructor for Budget object
    public BudgetDetailDialog(MainFrame mainFrame, Budget budget) {
        super(mainFrame);
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.revisionEntity = null;

        initComponents();
    }

    // Constructor for Revision Entry
    public BudgetDetailDialog(MainFrame mainFrame, Budget budget, DefaultRevisionEntity revisionEntity) {
        super(mainFrame);
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.revisionEntity = revisionEntity;

        initComponents();
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add components to the content panel
        contentPanel.add(new JLabel(NAME + ": " + budget.getName()));
        contentPanel.add(new JLabel(DESCRIPTION + ": " + budget.getDescription()));

        // Add revision-specific info if available
        if (revisionEntity != null) {
            contentPanel.add(new JLabel(REVISION_ID + ": " + revisionEntity.getId()));
            contentPanel.add(new JLabel(REVISION_DATE + ": " + revisionEntity.getRevisionDate()));
        }

        // Add the content panel to the dialog
        add(contentPanel);

        setTitle(BUDGET_DETAILS);
        setModal(true);
        pack();
        setLocationRelativeTo(getParent());
    }

    private static final String
            BUDGET_DETAILS = "Budget Details",
            NAME = "Name",
            DESCRIPTION = "Description",
            REVISION_ID = "Revision ID",
            REVISION_DATE = "Revision Date";
}
