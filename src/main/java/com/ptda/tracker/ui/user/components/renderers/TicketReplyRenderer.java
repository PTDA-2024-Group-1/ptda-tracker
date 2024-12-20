package com.ptda.tracker.ui.user.components.renderers;

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

            boolean isAdmin = UserSession.getInstance().getUser().getUserType().equals("ADMIN");
            boolean isAssistant = reply.getCreatedBy().getUserType().equals("ASSISTANT");
            boolean isCurrentUser = reply.getCreatedBy().getId().equals(UserSession.getInstance().getUser().getId());

            if (isCurrentUser || (isAdmin && isAssistant)) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
        }

        return this;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
}