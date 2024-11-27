package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.DailyLimit;
import com.ptda.tracker.repositories.DailyLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyLimitServiceHibernateImpl implements DailyLimitService {
    private final DailyLimitRepository dailyLimitRepository;

    @Override
    public List<DailyLimit> getAll() {
        return dailyLimitRepository.findAll();
    }

    @Override
    public void create(List<DailyLimit> dailyLimits) {
        dailyLimitRepository.saveAll(dailyLimits);
    }
}
