package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;

import lombok.*;

import java.util.Objects;

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

    @PrePersist
    public void prePersist() {
        updateOwners();
    }

    @PreUpdate
    public void preUpdate() {
        updateOwners();
    }

    @PreRemove
    public void preRemove() {
        updateOwners();
    }

    private void updateOwners() {
        expense.setUpdatedAt(System.currentTimeMillis());
        expense.setUpdatedBy(UserSession.getInstance().getUser());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseDivision that = (ExpenseDivision) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(expense.getId(), that.expense.getId()) &&
                Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expense.getId(), user.getId());
    }
}