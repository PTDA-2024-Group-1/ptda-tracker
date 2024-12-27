package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.user.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketRepositoryTest {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Test
    void testSaveAndFindByCreatedBy() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);
        assertThat(user.getId()).isNotNull();

        Ticket ticket = Ticket.builder()
                .title("Test Ticket")
                .body("Test Body")
                .createdBy(user)
                .build();
        ticketRepository.save(ticket);
        assertThat(ticket.getId()).isNotNull();

        List<Ticket> tickets = ticketRepository.findAllByCreatedById(user.getId());
        assertThat(tickets).isNotEmpty();
        assertThat(tickets.get(0).getTitle()).isEqualTo("Test Ticket");
    }

    @Test
    void testFindAllByIsClosedIsFalseAndCreatedById() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Ticket ticket1 = Ticket.builder()
                .title("Open Ticket")
                .body("Open Body")
                .createdBy(user)
                .isClosed(false)
                .build();
        ticketRepository.save(ticket1);

        Ticket ticket2 = Ticket.builder()
                .title("Closed Ticket")
                .body("Closed Body")
                .createdBy(user)
                .isClosed(true)
                .build();
        ticketRepository.save(ticket2);

        List<Ticket> openTickets = ticketRepository.findAllByIsClosedIsFalseAndCreatedById(user.getId());
        assertThat(openTickets).hasSize(1);
        assertThat(openTickets.get(0).getTitle()).isEqualTo("Open Ticket");
    }

    @Test
    void testUpdateTicketStatus() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Ticket ticket = Ticket.builder()
                .title("Test Ticket")
                .body("Test Body")
                .createdBy(user)
                .isClosed(false)
                .build();
        ticketRepository.save(ticket);

        ticket.setClosed(true);
        ticketRepository.save(ticket);

        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).orElseThrow();
        assertThat(updatedTicket.isClosed()).isTrue();
    }

    @Test
    void testFindAllTickets() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();
        userRepository.save(user);

        Ticket ticket1 = Ticket.builder()
                .title("Open Ticket")
                .body("Open Body")
                .createdBy(user)
                .isClosed(false)
                .build();
        ticketRepository.save(ticket1);
        Ticket ticket2 = Ticket.builder()
                .title("Closed Ticket")
                .body("Closed Body")
                .createdBy(user)
                .isClosed(true)
                .build();
        ticketRepository.save(ticket2);

        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(2);

    }
}