package com.ptda.tracker.models.dispute;

import com.ptda.tracker.models.tracker.Expense;
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
public class Subdivision {

    @Id
    @GeneratedValue
//            (strategy = GenerationType.SEQUENCE, generator = "subdivision_seq")
//    @SequenceGenerator(name = "subdivision_seq", sequenceName = "subdivision_seq", allocationSize = 1)
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