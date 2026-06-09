package com.cosmo.wanda_web.projections.dashboard;

import com.cosmo.wanda_web.services.utils.InteractionType;

public interface UserInteractionTypeProjection {
    Long getUserId();
    InteractionType getInteractionType();
    Long getTotal();
}