package com.ptda.tracker.models.dispute;

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
public class DisputeResolution {

    @Id
    private Long id;

    private boolean resolution;

    @OneToOne
    private Dispute dispute;

    @ManyToOne
    private User createdBy;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = System.currentTimeMillis();
    }

}
