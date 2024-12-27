package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;

import java.util.List;

public interface TicketService {

    List<Ticket> getAllByUserId(Long userId);

    List<Ticket> getAll();

    List<Ticket> getOpenTicketsByUserId(Long createdById);

    int getCountByUserIdAndStatus(Long userId, boolean isClosed);

    Ticket create(Ticket ticket);

    Ticket update(Ticket ticket);

    void delete(Ticket ticket);

}