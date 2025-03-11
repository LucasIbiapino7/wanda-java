package com.cosmo.wanda_web.projections;

import java.time.Instant;

public interface FindAllPendingChallengerProjection {
    Long getId();
    Long getChallengerId();
    String getChallengedName();
    String getChallengerName();
    Instant getCreatedAt();
}
