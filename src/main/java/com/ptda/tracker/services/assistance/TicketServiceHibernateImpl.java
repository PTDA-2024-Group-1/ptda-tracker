package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceHibernateImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllByUserId(Long userId) {
        return ticketRepository.findAllByCreatedById(userId);
    }

    @Override
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getOpenTicketsByUserId(Long createdById) {
        return ticketRepository.findAllByIsClosedIsFalseAndCreatedById(createdById);
    }

    @Override
    public int getCountByUserIdAndStatus(Long userId, boolean isClosed) {
        return ticketRepository.countByCreatedByIdAndIsClosed(userId, isClosed);
    }

    @Override
    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }
}