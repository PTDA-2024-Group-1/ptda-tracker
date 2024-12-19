package com.ptda.tracker.ui.user.dialogs.expenses;

import com.ptda.tracker.util.ImportSharedData;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImportColumnsDialog extends JDialog {
    private final ImportSharedData sharedData;
    private final Runnable onDone;

    public ImportColumnsDialog(JFrame parent, Runnable onDone) {
        super(parent, "Import Columns", true);
        this.sharedData = ImportSharedData.getInstance();
        this.onDone = onDone;

        initComponents();
        setListeners();
    }

    private void setListeners() {
        cancelButton.addActionListener(e -> dispose());
        confirmButton.addActionListener(e -> {
            mapColumns();
            onDone.run();
            dispose();
        });
        columnsTable.getModel().addTableModelListener(e -> updateConfirmButtonState());
    }

    private void mapColumns() {
        ColumnsTableModel model = (ColumnsTableModel) columnsTable.getModel();
        Map<String, Integer> columnMapping = new HashMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String columnName = (String) model.getValueAt(i, 0);
            ExpenseFieldOptions selectedField = (ExpenseFieldOptions) model.getValueAt(i, 1);

            if (selectedField != ExpenseFieldOptions.IGNORE) {
                columnMapping.put(columnName, selectedField.ordinal());
            }
        }

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
        cancelButton = new JButton("Cancel");
        confirmButton = new JButton("Confirm");
        confirmButton.setEnabled(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }

    private JTable columnsTable;
    private JButton cancelButton, confirmButton;

    private static class ColumnsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Column Name", "Mapped Field"};
        private final Object[][] data;

        public ColumnsTableModel(Map<String, Integer> existingMapping) {
            String[] rawColumnNames = ImportSharedData.getInstance().getRawData().getFirst();
            data = new Object[rawColumnNames.length][2];

            for (int i = 0; i < rawColumnNames.length; i++) {
                data[i][0] = rawColumnNames[i];
                // Restore mapping or default to IGNORE
                ExpenseFieldOptions defaultOption = ExpenseFieldOptions.IGNORE;
                if (existingMapping != null && existingMapping.containsKey(rawColumnNames[i])) {
                    defaultOption = ExpenseFieldOptions.values()[existingMapping.get(rawColumnNames[i])];
                }
                data[i][1] = defaultOption;
            }
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            data[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        public boolean isOptionUsed(ExpenseFieldOptions option, int excludeRow) {
            for (int i = 0; i < getRowCount(); i++) {
                if (i != excludeRow && data[i][1] == option) {
                    return true;
                }
            }
            return false;
        }
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

    public enum ExpenseFieldOptions {
        IGNORE, AMOUNT, DATE, CATEGORY, TITLE, DESCRIPTION
    }

    private static final String
            COLUMN = "Column",

            IGNORE = ExpenseFieldOptions.IGNORE.toString(),
            AMOUNT = ExpenseFieldOptions.AMOUNT.toString(),
            DATE = ExpenseFieldOptions.DATE.toString(),
            CATEGORY = ExpenseFieldOptions.CATEGORY.toString(),
            TITLE = ExpenseFieldOptions.TITLE.toString(),
            DESCRIPTION = ExpenseFieldOptions.DESCRIPTION.toString();
}
