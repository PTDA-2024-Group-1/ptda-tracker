package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import com.ptda.tracker.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceHibernateImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllByUser(User user) {
        return ticketRepository.findAllByCreatedBy(user);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }

    @Override
    public void update(Ticket ticket) {
        if (ticketRepository.existsById(ticket.getId())) {
            ticketRepository.save(ticket);
        }
    }
}