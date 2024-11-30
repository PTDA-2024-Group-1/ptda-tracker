package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByCreatedBy(User createdBy);

    List<Ticket> findAllByIsClosedIsFalseAndCreatedBy(User createdBy);

}