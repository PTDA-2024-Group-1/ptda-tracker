package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.TicketReply;

import java.util.List;

public interface TicketReplyService {

    List<TicketReply> getAllByTicketId(Long ticketId);

    TicketReply findById(Long id);

    TicketReply create(TicketReply ticketReply);

    void deleteById(Long id);

}
