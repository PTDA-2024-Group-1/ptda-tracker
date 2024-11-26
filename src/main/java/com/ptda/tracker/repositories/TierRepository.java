package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierRepository extends JpaRepository<Tier, Long> {
    List<Tier> findByPointsLessThanEqual(int points);
    Optional<Tier> findTopByPointsLessThanEqualOrderByPointsDesc(int points);
}
