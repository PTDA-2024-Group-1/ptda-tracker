package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    List<TicketReply> findAllByTicketId(Long ticketId); // Consulta derivada para obter os Replies por Ticket
}
