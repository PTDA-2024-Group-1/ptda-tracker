package com.ptda.tracker.ui.user.components.cellEditors;

import com.ptda.tracker.models.tracker.Budget;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.ptda.tracker.ui.user.forms.ExpensesEditForm.createBudgetMap;

public class BudgetDropdownCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final Map<String, Budget> budgetMap;
    private JComboBox<String> comboBox;

    public BudgetDropdownCellEditor(List<Budget> budgets) {
        this.budgetMap = createBudgetMap(budgets);
    }

    @Override
    public Object getCellEditorValue() {
        // Retrieve the selected budget object
        String selectedName = (String) comboBox.getSelectedItem();
        return budgetMap.get(selectedName);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Create a new combo box for this cell
        comboBox = new JComboBox<>(budgetMap.keySet().toArray(new String[0]));

        // Set the current value in the combo box
        if (value instanceof Budget) {
            Budget currentBudget = (Budget) value;
            comboBox.setSelectedItem(currentBudget.getName());
        }

        return comboBox;
    }

    @Override
    public boolean stopCellEditing() {
        // Commit the value to the table's data model
        fireEditingStopped();
        return true;
    }
}
