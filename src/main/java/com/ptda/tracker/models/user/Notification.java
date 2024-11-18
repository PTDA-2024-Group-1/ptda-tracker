package com.ptda.tracker.models.user;

import com.ptda.tracker.models.tracker.BudgetAlert;
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
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String message;

    @ManyToOne
    private BudgetAlert budgetAlert;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = System.currentTimeMillis();
    }

}
