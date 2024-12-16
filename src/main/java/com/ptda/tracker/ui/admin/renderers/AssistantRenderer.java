package com.ptda.tracker.ui.admin.renderers;

import com.ptda.tracker.models.assistance.Assistant;

import javax.swing.*;
import java.awt.*;

public class AssistantRenderer extends JLabel implements ListCellRenderer<Assistant> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Assistant> list, Assistant value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value != null ? value.getName() : "");
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}