package com.ptda.tracker.repositories;

import com.ptda.tracker.models.assistance.Ticket;
import com.ptda.tracker.models.assistance.TicketReply;
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
public class TicketReplyRepositoryTest {

    private final TicketReplyRepository ticketReplyRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Test
    void testSaveAndFindByTicketId() {
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

        TicketReply ticketReply = TicketReply.builder()
                .body("Test Reply")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(ticketReply);
        assertThat(ticketReply.getId()).isNotNull();

        List<TicketReply> replies = ticketReplyRepository.findAllByTicketId(ticket.getId());
        assertThat(replies).isNotEmpty();
        assertThat(replies.get(0).getBody()).isEqualTo("Test Reply");
    }

    @Test
    void testFindByTicketIdForNonExistingTicket() {
        List<TicketReply> replies = ticketReplyRepository.findAllByTicketId(-1L); // ID inexistente
        assertThat(replies).isEmpty();
    }

    @Test
    void testFindMultipleRepliesByTicketId() {
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
                .build();
        ticketRepository.save(ticket);

        TicketReply reply1 = TicketReply.builder()
                .body("Reply 1")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(reply1);

        TicketReply reply2 = TicketReply.builder()
                .body("Reply 2")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(reply2);

        List<TicketReply> replies = ticketReplyRepository.findAllByTicketId(ticket.getId());
        assertThat(replies).hasSize(2);
        assertThat(replies.get(0).getBody()).isEqualTo("Reply 1");
        assertThat(replies.get(1).getBody()).isEqualTo("Reply 2");
    }

    @Test
    void testDeleteRepliesByTicketId() {
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
                .build();
        ticketRepository.save(ticket);

        TicketReply reply1 = TicketReply.builder()
                .body("Reply 1")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(reply1);

        TicketReply reply2 = TicketReply.builder()
                .body("Reply 2")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(reply2);

        ticketReplyRepository.deleteAll(ticketReplyRepository.findAllByTicketId(ticket.getId()));

        List<TicketReply> replies = ticketReplyRepository.findAllByTicketId(ticket.getId());
        assertThat(replies).isEmpty();
    }

    @Test
    void testCreatedAtIsSetAutomatically() {
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
                .build();
        ticketRepository.save(ticket);

        TicketReply ticketReply = TicketReply.builder()
                .body("Test Reply")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(ticketReply);

        assertThat(ticketReply.getCreatedAt()).isGreaterThan(0L);
    }

    @Test
    void testGetById() {
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
                .build();
        ticketRepository.save(ticket);

        TicketReply ticketReply = TicketReply.builder()
                .body("Test Reply")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(ticketReply);

        TicketReply foundReply = ticketReplyRepository.findById(ticketReply.getId()).orElse(null);
        assertThat(foundReply).isNotNull();
        assertThat(foundReply.getBody()).isEqualTo("Test Reply");
    }

    @Test
    void testDeleteById() {
        User user = User.builder()
                .name("Test User")
                .email("email@test.com")
                .password("password")
                .build();
        userRepository.save(user);

        Ticket ticket = Ticket.builder()
                .title("Test Ticket")
                .body("Test Body")
                .createdBy(user)
                .build();
        ticketRepository.save(ticket);

        TicketReply ticketReply = TicketReply.builder()
                .body("Test Reply")
                .ticket(ticket)
                .createdBy(user)
                .build();
        ticketReplyRepository.save(ticketReply);

        ticketReplyRepository.deleteById(ticketReply.getId());

        TicketReply foundReply = ticketReplyRepository.findById(ticketReply.getId()).orElse(null);
        assertThat(foundReply).isNull();

        List<TicketReply> replies = ticketReplyRepository.findAllByTicketId(ticket.getId());
        assertThat(replies).isEmpty();

        List<TicketReply> allReplies = ticketReplyRepository.findAll();
        assertThat(allReplies).isEmpty();

        List<Ticket> allTickets = ticketRepository.findAll();

        assertThat(allTickets).isNotEmpty();
    }
}