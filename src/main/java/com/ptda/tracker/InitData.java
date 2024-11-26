package com.ptda.tracker;

import com.ptda.tracker.models.user.Tier;
import com.ptda.tracker.services.user.TierService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitData {
    private final TierService tierService;

    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");

        if (tierService.getAll().isEmpty()) {
            System.out.println("No tiers found, creating default tiers...");
            createDefaultTiers();
        }
    }

    private void createDefaultTiers() {
        List<Tier> tiers = List.of(
                Tier.builder().name("Bronze").points(0).build(),
                Tier.builder().name("Silver").points(100).build(),
                Tier.builder().name("Gold").points(200).build(),
                Tier.builder().name("Platinum").points(300).build()
        );
        tierService.create(tiers);
    }
}
