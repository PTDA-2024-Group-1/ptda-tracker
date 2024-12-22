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
            String wrappedBody = wrapText(reply.getBody(), 30); // Limite de 30 caracteres por linha

            boolean isAdmin = UserSession.getInstance().getUser().getUserType().equals("ADMIN");
            boolean isAssistant = reply.getCreatedBy().getUserType().equals("ASSISTANT");
            boolean isCurrentUser = reply.getCreatedBy().getId().equals(UserSession.getInstance().getUser().getId());

            // Define alinhamento do cabeçalho e corpo
            String alignment = isCurrentUser || (isAdmin && isAssistant) ? "right" : "left";

            // Formata o texto com nome, data e mensagem
            String replyText = String.format(
                    "<html>" +
                            "<div style='text-align:%s;'><b>%s</b></div>" + // Nome
                            "<div style='text-align:%s; font-size:small; color:gray;'>%s</div>" + // Data
                            "<div style='text-align:%s; margin-top:5px;'>%s</div>" + // Mensagem
                            "</html>",
                    alignment, reply.getCreatedBy().getName(), // Nome
                    alignment, formattedDate, // Data
                    alignment, wrappedBody // Corpo da mensagem
            );

            setText(replyText);

            // Ajusta alinhamento do componente Swing
            setHorizontalAlignment(isCurrentUser || (isAdmin && isAssistant) ? SwingConstants.RIGHT : SwingConstants.LEFT);

            // Adiciona padding para melhorar visualmente
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        return this;
    }

    /**
     * Método para dividir o texto em linhas com base no limite de caracteres.
     *
     * @param text   Texto original.
     * @param limit  Número máximo de caracteres por linha.
     * @return Texto com quebras de linha inseridas.
     */
    private String wrapText(String text, int limit) {
        StringBuilder wrappedText = new StringBuilder();

        int i = 0;
        while (i < text.length()) {
            int end = Math.min(i + limit, text.length());
            wrappedText.append(text, i, end).append("<br/>");
            i = end;
        }

        return wrappedText.toString();
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
}
