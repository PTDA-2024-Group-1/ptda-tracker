package com.ptda.tracker.services.user;

import com.ptda.tracker.models.user.TierLimit;

import java.util.List;

public interface TierLimitService {

    List<TierLimit> getAll();

    List<TierLimit> getByTierId(Long tierId);

    TierLimit create(TierLimit tierLimits);

    List<TierLimit> create(List<TierLimit> tierLimits);

    TierLimit update(TierLimit tierLimits);

    boolean deleteById(Long id);

    boolean deleteAllByTierId(Long tierId);

}