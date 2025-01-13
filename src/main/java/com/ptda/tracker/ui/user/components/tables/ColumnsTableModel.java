package com.ptda.tracker.ui.user.components.tables;

import com.ptda.tracker.ui.user.dialogs.expenses.ImportColumnsDialog.ExpenseFieldOptions;
import com.ptda.tracker.util.ExpensesImportSharedData;
import com.ptda.tracker.util.LocaleManager;

import javax.swing.table.AbstractTableModel;
import java.util.Map;

public class ColumnsTableModel extends AbstractTableModel {
    private final String[] columnNames = {COLUMN_NAME, MAPPED_FIELD};
    private final Object[][] data;

    public ColumnsTableModel(Map<String, Integer> existingMapping) {
        ExpensesImportSharedData sharedData = ExpensesImportSharedData.getInstance();
        String[] rawColumnNames;

        // Determine raw column names based on header presence
        if (sharedData.isHasHeader()) {
            rawColumnNames = sharedData.getRawData().getFirst();
        } else {
            int columnCount = sharedData.getRawData().getFirst().length;
            rawColumnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rawColumnNames[i] = COLUMN + " " + (i + 1);
            }
        }

        data = new Object[rawColumnNames.length][2];

        // Initialize rows with existing mappings or default to IGNORE
        for (int i = 0; i < rawColumnNames.length; i++) {
            data[i][0] = rawColumnNames[i];
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
        return columnIndex == 1; // Only the "Mapped Field" column is editable
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

    private static final LocaleManager localeManager = LocaleManager.getInstance();
    private static final String
            COLUMN = localeManager.getTranslation("column"),
            COLUMN_NAME = localeManager.getTranslation("column_name"),
            MAPPED_FIELD = localeManager.getTranslation("mapped_field");
}
