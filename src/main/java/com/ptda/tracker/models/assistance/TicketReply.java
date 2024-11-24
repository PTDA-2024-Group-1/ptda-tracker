package com.ptda.tracker.models.assistance;

import com.ptda.tracker.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketReply {

    @Id
    @GeneratedValue
    private Long id;

    private String body;

    @ManyToOne
    private Assistant assistant;

    @ManyToOne
    private Ticket ticket;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = System.currentTimeMillis();
    }

}
