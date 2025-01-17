package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.Date;

@Entity
@Audited
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private double amount;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category = ExpenseCategory.OTHER;

    @ManyToOne
    private Budget budget;

    @ManyToOne
    private User updatedBy;

    private long updatedAt;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == 0) {
            createdAt = System.currentTimeMillis();
        }
        if (createdBy == null) {
            createdBy = UserSession.getInstance().getUser();
        }
        updateOwner();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
        this.updatedBy = UserSession.getInstance().getUser();
        updateOwner();
    }

    @PreRemove
    protected void onDelete() {
        updateOwner();
    }

    private void updateOwner() {
        if (budget != null) {
            budget.setUpdatedAt(System.currentTimeMillis());
            budget.setUpdatedBy(UserSession.getInstance().getUser());
        }
    }

}
