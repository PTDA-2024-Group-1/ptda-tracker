package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.repositories.TicketReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketReplyServiceHibernateImpl implements TicketReplyService {

    private final TicketReplyRepository ticketReplyRepository;

    @Override
    public List<TicketReply> getAllByTicketId(Long ticketId) {
        return ticketReplyRepository.findAllByTicketId(ticketId); // Utiliza o método do repositório
    }

    @Override
    public TicketReply findById(Long id) {
        Optional<TicketReply> optionalTicketReply = ticketReplyRepository.findById(id);
        return optionalTicketReply.orElse(null);
    }

    @Override
    public TicketReply create(TicketReply ticketReply) {
        return ticketReplyRepository.save(ticketReply);
    }

    @Override
    public void deleteById(Long id) {
        ticketReplyRepository.deleteById(id);
    }
}
