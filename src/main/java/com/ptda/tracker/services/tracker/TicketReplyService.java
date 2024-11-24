package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.assistance.TicketReply;

import java.util.List;

public interface TicketReplyService {
    TicketReply save(TicketReply ticketReply);

    TicketReply findById(Long id);

    void deleteById(Long id);

    List<TicketReply> getAllByTicketId(Long ticketId); // Novo método
}
