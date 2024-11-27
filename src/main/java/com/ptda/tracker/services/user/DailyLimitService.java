package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.DailyLimit;
import java.util.List;

public interface DailyLimitService {
    List<DailyLimit> getAll();
    void create(List<DailyLimit> dailyLimits);
}