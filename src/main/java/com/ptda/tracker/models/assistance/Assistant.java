package com.ptda.tracker.models.assistance;

import com.ptda.tracker.models.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@DiscriminatorValue("ASSISTANT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Assistant extends User {

    @ManyToOne
    private AssistantLevel assistantLevel;

}
