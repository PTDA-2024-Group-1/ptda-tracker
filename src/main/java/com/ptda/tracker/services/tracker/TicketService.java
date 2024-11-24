package com.ptda.tracker.services.tracker;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;

import java.util.List;

public interface TicketService {

        List<Ticket> getAllByUser(User user);

        Ticket save(Ticket ticket);

        void delete(Ticket ticket);

        void update(Ticket ticket);
}
