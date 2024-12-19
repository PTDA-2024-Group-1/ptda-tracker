package com.ptda.tracker.ui.user.components.cellEditors;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Date;

public class DatePickerEditor extends AbstractCellEditor implements TableCellEditor {
    private final JXDatePicker datePicker = new JXDatePicker();

    public DatePickerEditor() {
        datePicker.getEditor().setEditable(false); // Disable text editing
    }

    @Override
    public Object getCellEditorValue() {
        return datePicker.getDate();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Date) {
            datePicker.setDate((Date) value);
        } else {
            datePicker.setDate(null);
        }
        return datePicker;
    }
}
