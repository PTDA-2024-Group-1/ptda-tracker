package com.ptda.tracker.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("USER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private boolean isActive;

    private boolean isEmailVerified;

    @ManyToOne
    private Tier tier;

    private long createdAt;

    @PrePersist
    protected void onCreate() {
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
    }
}
