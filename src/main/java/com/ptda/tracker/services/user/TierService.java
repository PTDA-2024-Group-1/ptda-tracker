package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.Tier;

import java.util.List;
import java.util.Optional;

public interface TierService {

    List<Tier> getAll();

    Optional<Tier> getById(Long id);

    Tier getByName(String name);

    List<Tier> getTiersByPoints(int points);

    Optional<Tier> getTopTierByPoints(int points);

    Tier create(Tier tier);

    List<Tier> create(List<Tier> tiers);

    Tier update(Tier tier);

    boolean delete(Tier tier);

}
