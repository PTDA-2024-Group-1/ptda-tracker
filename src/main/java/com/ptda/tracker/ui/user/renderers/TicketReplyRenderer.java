package com.ptda.tracker.ui.user.renderers;

import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class TicketReplyRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof TicketReply reply) {
            String formattedDate = DATE_FORMAT.format(reply.getCreatedAt());
            String replyText = String.format("<html><b>%s</b><br/>%s<br/><i>%s</i></html>",
                    reply.getCreatedBy().getName(),
                    formattedDate,
                    reply.getBody());

            setText(replyText);

            if (reply.getCreatedBy().getId().equals(UserSession.getInstance().getUser().getId())) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
        }

        return this;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
}