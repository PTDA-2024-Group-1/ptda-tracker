package com.ptda.tracker.models.user;

import com.ptda.tracker.models.tracker.BudgetActionType;
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
public class TierLimit {

    @Id
    @GeneratedValue
    private Long id;

    private BudgetActionType budgetActionType;

    @Column(name = "`limit`")
    private int limit;

    private boolean isPermanent;

    private long resetTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Tier tier;

}
