package com.ptda.tracker.ui.user.components.tables;

import com.ptda.tracker.models.tracker.ExpenseCategory;
import com.ptda.tracker.util.ExpensesImportSharedData;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoriesTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Imported Category", "Mapped Category"};
    private final Object[][] data;

    public CategoriesTableModel() {
        ExpensesImportSharedData sharedData = ExpensesImportSharedData.getInstance();
        Map<String, ExpenseCategory> categoryMapping = sharedData.getCategoryMapping();
        List<String[]> rawData = sharedData.getRawData();

        // Extract unique categories
        List<String> uniqueCategories = rawData.stream()
                .skip(sharedData.isHasHeader() ? 1 : 0) // Skip header row if applicable
                .map(row -> row[sharedData.getColumnMapping().get("CATEGORY")])
                .distinct()
                .collect(Collectors.toList());

        data = new Object[uniqueCategories.size()][2];
        for (int i = 0; i < uniqueCategories.size(); i++) {
            String importedCategory = uniqueCategories.get(i);
            data[i][0] = importedCategory;
            data[i][1] = categoryMapping.getOrDefault(importedCategory, ExpenseCategory.OTHER);
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
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) { // Allow editing only in the "Mapped Category" column
            data[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1; // Only "Mapped Category" column is editable
    }

    public Map<String, ExpenseCategory> getUpdatedMapping() {
        ExpensesImportSharedData sharedData = ExpensesImportSharedData.getInstance();
        Map<String, ExpenseCategory> updatedMapping = sharedData.getCategoryMapping();
        for (Object[] row : data) {
            updatedMapping.put((String) row[0], (ExpenseCategory) row[1]);
        }
        return updatedMapping;
    }
}
