package com.ptda.tracker.ui.user.dialogs;

import com.ptda.tracker.models.tracker.ExpenseDivision;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.*;
import java.util.List;

public class ExpenseDivisionsDialog extends JDialog {
    private final List<ExpenseDivision> expenseDivisions;

    public ExpenseDivisionsDialog(List<ExpenseDivision> expenseDivisions) {
        this.expenseDivisions = expenseDivisions;
        initUI();
    }

    private void initUI() {
        setTitle(TITLE_DIVISONS);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        divisionsTable = createDivisionsTable(expenseDivisions);
        JScrollPane scrollPane = new JScrollPane(divisionsTable);
        add(scrollPane);
    }

    private JTable createDivisionsTable(List<ExpenseDivision> expenseDivisions) {
        return createDivisionsJTable(expenseDivisions);
    }

    public static JTable createDivisionsJTable(List<ExpenseDivision> expenseDivisions) {
        String[] columnNames = {USER, AMOUNT, PAID_AMOUNT};
        Object[][] data = new Object[expenseDivisions.size()][columnNames.length];
        for (int i = 0; i < expenseDivisions.size(); i++) {
            ExpenseDivision expenseDivision = expenseDivisions.get(i);
            data[i][0] = expenseDivision.getUser().getName();
            data[i][1] = expenseDivision.getAmount() + "€";
            data[i][2] = expenseDivision.getPaidAmount() + "€";
        }
        return new JTable(data, columnNames);
    }

    private JTable divisionsTable;
    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            TITLE_DIVISONS = localeManager.getTranslation("expense.divisions.title"),
            USER = localeManager.getTranslation("user"),
            AMOUNT = localeManager.getTranslation("amount"),
            PAID_AMOUNT = localeManager.getTranslation("paid.amount");
}