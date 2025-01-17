package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @ManyToOne
    private User updatedBy;

    private long updatedAt;

    @Transient
    private boolean isFavorite;
    
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        if (createdBy == null) {
            createdBy = UserSession.getInstance().getUser();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
        this.updatedBy = UserSession.getInstance().getUser();
    }

}
