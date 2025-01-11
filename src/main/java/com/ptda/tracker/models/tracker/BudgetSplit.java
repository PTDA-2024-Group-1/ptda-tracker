package com.ptda.tracker.models.tracker;

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
public class BudgetSplit {

    @Id
    @GeneratedValue
    private Long id;

    private double amount;

    private double paidAmount;

    @ManyToOne
    private User user;

    @ManyToOne
    private Budget budget;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        createdAt = System.currentTimeMillis();
    }
}
