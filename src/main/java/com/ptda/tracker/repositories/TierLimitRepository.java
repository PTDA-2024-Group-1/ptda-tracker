package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.TierLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierLimitRepository extends JpaRepository<TierLimit, Long> {

    List<TierLimit> findByTierId(Long tierId);

    void deleteAllByTierId(Long tierId);

}
