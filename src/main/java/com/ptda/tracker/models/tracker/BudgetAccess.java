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

    private boolean isFavorite;

    @ManyToOne
    private Budget budget;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        updateOwner();
    }

    @PreUpdate
    protected void onUpdate() {
        updateOwner();
    }

    @PreRemove
    protected void onDelete() {
        updateOwner();
    }

    private void updateOwner() {
        budget.setUpdatedAt(System.currentTimeMillis());
        budget.setUpdatedBy(UserSession.getInstance().getUser());
    }
}
