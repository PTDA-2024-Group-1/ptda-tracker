package com.ptda.tracker.ui.user.forms;

import com.ptda.tracker.models.tracker.Budget;
import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.ui.MainFrame;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.*;
import java.util.List;

public class ExpensesEditForm extends JPanel {
    private final MainFrame mainFrame;
    private final Budget budget;
    private final String returnScreen;
    private final Runnable onImportSuccess;

    public ExpensesEditForm(MainFrame mainFrame, List<Expense> expenses, Budget budget, String returnScreen, Runnable onImportSuccess) {
        this.mainFrame = mainFrame;
        this.budget = budget;
        this.returnScreen = returnScreen;
        this.onImportSuccess = onImportSuccess;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        backButton.addActionListener(e -> mainFrame.showScreen(returnScreen));
    }

    private void initComponents() {

    }

    private JXTable expensesPreviewTable;
    private DefaultTableModel tableModel;
    private JCheckBox hasHeaderCheckBox;
    private JButton submitButton, backButton;
    private static final String
            TITLE = "title",
            AMOUNT = "amount",
            DATE = "date",
            CATEGORY = "category",
            DESCRIPTION = "description",
            IGNORE = "ignore";
}
