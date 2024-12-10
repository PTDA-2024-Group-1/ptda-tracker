package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
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
public class ExpenseDivision {

    @Id
    @GeneratedValue
    private Long id;

    private double amount;

    private double percentage;

    @ManyToOne
    private Expense expense;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private User createdBy;

    @PrePersist
    public void prePersist() {
        createdBy = UserSession.getInstance().getUser();
    }

}