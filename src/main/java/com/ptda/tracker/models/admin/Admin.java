package com.ptda.tracker.models.admin;

import com.ptda.tracker.models.assistance.Assistant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Assistant {

    private AdminAccessLevel adminAccessLevel;

}
