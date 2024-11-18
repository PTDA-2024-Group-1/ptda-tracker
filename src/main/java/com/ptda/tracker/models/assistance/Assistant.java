package com.ptda.tracker.models.assistance;

import com.ptda.tracker.models.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ASSISTANT")
@EqualsAndHashCode(callSuper = true)
@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assistant extends User {

    @ManyToOne
    private AssistantLevel assistantLevel;

}
