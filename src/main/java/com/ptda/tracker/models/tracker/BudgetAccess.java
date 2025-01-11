package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAccess {

    @Id
    @GeneratedValue
    private Long id;

    private BudgetAccessLevel accessLevel;

    @ManyToOne
    private Budget budget;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        createdBy = UserSession.getInstance().getUser();
        budget.setUpdatedAt(System.currentTimeMillis());
        budget.setUpdatedBy(UserSession.getInstance().getUser());
    }

    @PreUpdate
    protected void onUpdate() {
        budget.setUpdatedAt(System.currentTimeMillis());
        budget.setUpdatedBy(UserSession.getInstance().getUser());
    }

    @PreRemove
    protected void onDelete() {
        budget.setUpdatedAt(System.currentTimeMillis());
        budget.setUpdatedBy(UserSession.getInstance().getUser());
    }
}
