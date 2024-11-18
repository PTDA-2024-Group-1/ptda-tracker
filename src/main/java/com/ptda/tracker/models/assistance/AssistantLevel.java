package com.ptda.tracker.models.assistance;

import jakarta.persistence.Entity;
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
public class AssistantLevel {

    @Id
    private Long id;

    private String name;

    private int processedTickets;

}
