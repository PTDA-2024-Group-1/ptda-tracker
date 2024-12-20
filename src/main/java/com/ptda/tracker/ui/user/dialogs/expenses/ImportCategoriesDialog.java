package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.ui.user.components.tables.CategoriesTableModel;
import com.ptda.tracker.util.ExpensesImportSharedData;

import javax.swing.*;
import java.awt.*;

public class ImportCategoriesDialog extends JDialog {

    public ImportCategoriesDialog(JFrame parent, Runnable onDone) {
        super(parent, IMPORT_COLUMNS_MAPPING, true);

        initComponents();
        setListeners(onDone);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Initialize table with CategoriesTableModel
        categoriesTable = new JTable(new CategoriesTableModel());
        categoriesTable.getColumnModel().getColumn(1)
                .setCellEditor(new DefaultCellEditor(createCategoryComboBox()));

        add(new JScrollPane(categoriesTable), BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        confirmButton = new JButton(CONFIRM);
        skipButton = new JButton(SKIP);
        buttonPanel.add(confirmButton);
        buttonPanel.add(skipButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private JComboBox<ExpenseCategory> createCategoryComboBox() {
        return new JComboBox<>(ExpenseCategory.values());
    }

    private void setListeners(Runnable onDone) {
        confirmButton.addActionListener(e -> {
            saveCategoryMapping();
            onDone.run();
            dispose();
        });

        skipButton.addActionListener(e -> dispose());
    }

    private void saveCategoryMapping() {
        CategoriesTableModel model = (CategoriesTableModel) categoriesTable.getModel();
        ExpensesImportSharedData sharedData = ExpensesImportSharedData.getInstance();

        // Update category mappings in sharedData
        sharedData.setCategoryMapping(model.getUpdatedMapping());
    }

    private JTable categoriesTable;
    private JButton confirmButton;
    private JButton skipButton;
    private static final String
            IMPORT_COLUMNS_MAPPING = "Import Columns Mapping",
            SKIP = "Skip",
            CONFIRM = "Confirm";
}
