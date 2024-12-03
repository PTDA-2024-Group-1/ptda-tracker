package com.ptda.tracker.models.user;

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
public class TierHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Tier tier;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == 0) {
            createdAt = System.currentTimeMillis();
        }
    }

}
