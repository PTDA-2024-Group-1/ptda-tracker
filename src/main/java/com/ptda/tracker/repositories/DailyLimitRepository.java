package com.ptda.tracker.repositories;

import com.ptda.tracker.models.user.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyLimitRepository extends JpaRepository<DailyLimit, Long> {
}
