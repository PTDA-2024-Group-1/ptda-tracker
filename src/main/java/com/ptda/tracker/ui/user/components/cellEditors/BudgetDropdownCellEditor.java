package com.ptda.tracker.ui.user.components.cellEditors;

import com.ptda.tracker.models.tracker.Budget;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.ptda.tracker.ui.user.forms.ExpensesEditForm.createBudgetMap;

public class BudgetDropdownCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JComboBox<String> comboBox;
    private final Map<String, Budget> budgetMap;

    public BudgetDropdownCellEditor(List<Budget> budgets) {
        this.budgetMap = createBudgetMap(budgets);
        this.comboBox = new JComboBox<>(budgetMap.keySet().toArray(new String[0]));
    }

    @Override
    public Object getCellEditorValue() {
        return budgetMap.get(comboBox.getSelectedItem());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Budget budget) {
            comboBox.setSelectedItem(budget.getName());
        }
        return comboBox;
    }
}
