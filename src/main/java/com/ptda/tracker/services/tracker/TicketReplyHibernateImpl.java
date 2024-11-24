package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.assistance.TicketReply;
import com.ptda.tracker.repositories.TicketReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketReplyHibernateImpl implements TicketReplyService {

    private final TicketReplyRepository ticketReplyRepository;

    @Autowired
    public TicketReplyHibernateImpl(TicketReplyRepository ticketReplyRepository) {
        this.ticketReplyRepository = ticketReplyRepository;
    }

    @Override
    public TicketReply save(TicketReply ticketReply) {
        return ticketReplyRepository.save(ticketReply);
    }

    @Override
    public TicketReply findById(Long id) {
        Optional<TicketReply> optionalTicketReply = ticketReplyRepository.findById(id);
        return optionalTicketReply.orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        ticketReplyRepository.deleteById(id);
    }

    @Override
    public List<TicketReply> getAllByTicketId(Long ticketId) {
        return ticketReplyRepository.findAllByTicketId(ticketId); // Utiliza o método do repositório
    }
}
