package com.ptda.tracker.services.assistance;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;

import java.util.List;

public interface TicketService {

    List<Ticket> getAllByUser(User user);

    List<Ticket> getAll();


    List<Ticket> getOpenTicketsByUser(User createdBy);

    Ticket create(Ticket ticket);

    Ticket update(Ticket ticket);

    void delete(Ticket ticket);

}