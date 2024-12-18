package com.ptda.tracker.ui.user.components.tables;

import com.ptda.tracker.models.tracker.Expense;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ExpensesTableModel extends AbstractTableModel {
    private final List<Expense> expenses;
    private final String[] columns = {"Title", "Amount", "Date", "Category", "Budget", "Description"};

    public ExpensesTableModel(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public int getRowCount() {
        return expenses.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return expenses.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // Make all cells editable
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Expense expense = expenses.get(rowIndex);
        // Update expense based on columnIndex and aValue
    }
}
