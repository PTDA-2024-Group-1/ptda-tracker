package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.assistance.TicketReply;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class TicketReplyRenderer extends DefaultListCellRenderer {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof TicketReply) {
            TicketReply reply = (TicketReply) value;

            // Format the createdAt date
            String formattedDate = DATE_FORMAT.format(reply.getCreatedAt());

            // Display reply details
            setText(String.format("<html><b>%s</b><br/>%s<br/><i>%s</i></html>",
                    reply.getCreatedBy().getName(), // Creator's name
                    formattedDate, // Formatted creation date
                    reply.getBody())); // Reply body

            // Background color for selection
            if (isSelected) {
              //  setBackground(new Color(200, 200, 255)); // Light blue
            } else {
               // setBackground(index % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE); // Alternating colors
            }
        }

        return this;
    }
}