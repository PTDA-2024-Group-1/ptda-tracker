package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByCreatedBy(User createdBy);
    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :createdBy AND t.isClosed = false")
    List<Ticket> getOpenTicketsByUser(@Param("createdBy") User createdBy);
}