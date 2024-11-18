package com.ptda.tracker.models.dispute;

import com.ptda.tracker.models.tracker.Expense;
import com.ptda.tracker.models.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subdivision {

    @Id
    private Long id;

    private double amount;

    private double percentage;

    @ManyToOne
    private Expense expense;

    @ManyToOne
    private Dispute dispute;

    @ManyToOne
    private User user;

    @ManyToOne
    private User createdBy;

}
