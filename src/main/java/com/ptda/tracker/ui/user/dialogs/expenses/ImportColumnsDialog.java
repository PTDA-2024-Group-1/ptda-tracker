package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.ui.user.components.tables.ColumnsTableModel;
import com.ptda.tracker.util.ExpensesImportSharedData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImportColumnsDialog extends JDialog {
    private final ExpensesImportSharedData sharedData;
    private final Runnable onDone;

    public ImportColumnsDialog(JFrame parent, Runnable onDone) {
        super(parent, IMPORT_COLUMNS_MAPPING, true);
        this.sharedData = ExpensesImportSharedData.getInstance();
        this.onDone = onDone;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        skipButton.addActionListener(e -> dispose());
        confirmButton.addActionListener(e -> {
            mapColumns();
            dispose();
            onDone.run();
        });
        columnsTable.getModel().addTableModelListener(e -> updateConfirmButtonState());
    }

    private void mapColumns() {
        ColumnsTableModel model = (ColumnsTableModel) columnsTable.getModel();
        Map<String, Integer> columnMapping = new HashMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            ExpenseFieldOptions selectedField = (ExpenseFieldOptions) model.getValueAt(i, 1);
            if (selectedField != ExpenseFieldOptions.IGNORE) {
                columnMapping.put(selectedField.name(), i); // Map field name to column index
            }
        }

        System.out.println("Mapped Columns: " + columnMapping); // Debug mapping
        sharedData.setColumnMapping(columnMapping);
    }

    private void updateConfirmButtonState() {
        ColumnsTableModel model = (ColumnsTableModel) columnsTable.getModel();
        boolean hasAmount = false;
        boolean hasDate = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            ExpenseFieldOptions selectedField = (ExpenseFieldOptions) model.getValueAt(i, 1);
            if (selectedField == ExpenseFieldOptions.AMOUNT) {
                hasAmount = true;
            } else if (selectedField == ExpenseFieldOptions.DATE) {
                hasDate = true;
            }
        }

        confirmButton.setEnabled(hasAmount && hasDate);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table setup
        columnsTable = new JTable(new ColumnsTableModel(sharedData.getColumnMapping()));
        columnsTable.getColumnModel().getColumn(1).setCellEditor(new UniqueComboBoxEditor(new JComboBox<>(ExpenseFieldOptions.values())));

        add(new JScrollPane(columnsTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        skipButton = new JButton(SKIP);
        confirmButton = new JButton(CONFIRM);
        confirmButton.setEnabled(false);
        buttonPanel.add(skipButton);
        buttonPanel.add(confirmButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private JTable columnsTable;
    private JButton skipButton, confirmButton;
    private static final String
            IMPORT_COLUMNS_MAPPING = "Import Columns Mapping",
            SKIP = "Skip",
            CONFIRM = "Confirm",

            IGNORE = ExpenseFieldOptions.IGNORE.toString(),
            AMOUNT = ExpenseFieldOptions.AMOUNT.toString(),
            DATE = ExpenseFieldOptions.DATE.toString(),
            CATEGORY = ExpenseFieldOptions.CATEGORY.toString(),
            TITLE = ExpenseFieldOptions.TITLE.toString(),
            DESCRIPTION = ExpenseFieldOptions.DESCRIPTION.toString();

    public enum ExpenseFieldOptions {
        IGNORE, AMOUNT, DATE, CATEGORY, TITLE, DESCRIPTION
    }

    private class UniqueComboBoxEditor extends DefaultCellEditor {
        private final JComboBox<ExpenseFieldOptions> comboBox;

        public UniqueComboBoxEditor(JComboBox<ExpenseFieldOptions> comboBox) {
            super(comboBox);
            this.comboBox = comboBox;
        }

        @Override
        public boolean stopCellEditing() {
            int editingRow = columnsTable.getEditingRow();
            if (editingRow == -1) {
                return super.stopCellEditing();
            }

            ExpenseFieldOptions selectedOption = (ExpenseFieldOptions) comboBox.getSelectedItem();
            ColumnsTableModel model = (ColumnsTableModel) columnsTable.getModel();

            // Skip the check if the selected option is the same as the current value of the cell
            if (selectedOption != ExpenseFieldOptions.IGNORE && selectedOption != model.getValueAt(editingRow, 1) && model.isOptionUsed(selectedOption, editingRow)) {
                comboBox.setSelectedItem(ExpenseFieldOptions.IGNORE);
                JOptionPane.showMessageDialog(ImportColumnsDialog.this, "This option is already used in another column.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return super.stopCellEditing();
        }
    }
}
