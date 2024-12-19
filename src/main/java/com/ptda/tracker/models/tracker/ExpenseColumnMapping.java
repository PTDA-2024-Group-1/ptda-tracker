package com.ptda.tracker.models.tracker;

import com.ptda.tracker.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseColumnMapping {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "column_mapping_entries", joinColumns = @JoinColumn(name = "mapping_id"))
    @MapKeyColumn(name = "column_name")
    @Column(name = "mapped_field")
    private Map<String, String> columnMapping;

    @ManyToOne
    private User createdBy;

}
