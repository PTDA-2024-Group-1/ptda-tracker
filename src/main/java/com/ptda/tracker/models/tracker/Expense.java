package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import com.ptda.tracker.util.UserSession;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Expense {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private double amount;

    //private long dateTime;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String description;

    @Enumerated
    private ExpenseCategory category;

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
        if (this.category == null) {
            this.category = ExpenseCategory.OTHER;
        }
        this.createdAt = System.currentTimeMillis();
        this.createdBy = UserSession.getInstance().getUser();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
        this.updatedBy = UserSession.getInstance().getUser();
    }

}
