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

    private boolean equalDivision;

    private double paidAmount;

    private boolean paidAll;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    public void prePersist() {
        if (createdBy == null) {
            createdBy = UserSession.getInstance().getUser();
        }
        if (createdAt == 0) {
            createdAt = System.currentTimeMillis();
        }
        updateOwners();
    }

    @PreUpdate
    public void preUpdate() {
        if (createdBy == null) {
            createdBy = UserSession.getInstance().getUser();
        }
        if (createdAt == 0) {
            createdAt = System.currentTimeMillis();
        }
        updateOwners();
    }

    @PreRemove
    public void preRemove() {
        updateOwners();
    }

    private void updateOwners() {
        expense.setUpdatedAt(System.currentTimeMillis());
        expense.setUpdatedBy(UserSession.getInstance().getUser());
//        expense.getBudget().setUpdatedAt(System.currentTimeMillis());
//        expense.getBudget().setUpdatedBy(UserSession.getInstance().getUser());
    }

}