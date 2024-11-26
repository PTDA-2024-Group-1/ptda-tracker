package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = System.currentTimeMillis();
        this.createdBy = UserSession.getInstance().getUser();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
        this.updatedBy = UserSession.getInstance().getUser();
    }

    public String toString() {
        return name;
    }

}
