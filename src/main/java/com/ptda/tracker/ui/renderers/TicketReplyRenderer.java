package com.ptda.tracker.ui.renderers;

import com.ptda.tracker.models.assistance.TicketReply;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class TicketReplyRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof TicketReply reply) {

            // Format the createdAt date
            String formattedDate = DATE_FORMAT.format(reply.getCreatedAt());

            // Display reply details
            setText(String.format("<html><b>%s</b><br/>%s<br/><i>%s</i></html>",
                    reply.getCreatedBy().getName(), // Creator's name
                    formattedDate, // Formatted creation date
                    reply.getBody())); // Reply body
        }

        return this;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

}