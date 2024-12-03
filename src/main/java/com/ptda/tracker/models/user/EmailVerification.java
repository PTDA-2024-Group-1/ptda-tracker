package com.ptda.tracker.models.user;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private boolean isUsed;

    private String email;

    private long createdAt;

    @PostConstruct
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }

}
