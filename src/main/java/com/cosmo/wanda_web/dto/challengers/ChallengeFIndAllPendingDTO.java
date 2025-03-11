package com.cosmo.wanda_web.dto.challengers;

import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ChallengeFIndAllPendingDTO {
    private Long id;
    private Long challengerId;
    private String challengedName;
    private String challengerName;
    private OffsetDateTime createdAt;

    public ChallengeFIndAllPendingDTO() {
    }


    public ChallengeFIndAllPendingDTO(FindAllPendingChallengerProjection projection) {
        id = projection.getId();
        challengerId = projection.getChallengerId();
        challengedName = projection.getChallengedName();
        challengerName = projection.getChallengerName();
        Instant instant = projection.getCreatedAt();
        ZoneOffset offset = ZoneOffset.of("-03:00");
        createdAt = instant.atOffset(offset);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallengerId() {
        return challengerId;
    }

    public void setChallengerId(Long challengerId) {
        this.challengerId = challengerId;
    }

    public String getChallengedName() {
        return challengedName;
    }

    public void setChallengedName(String challengedName) {
        this.challengedName = challengedName;
    }

    public String getChallengerName() {
        return challengerName;
    }

    public void setChallengerName(String challengerName) {
        this.challengerName = challengerName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
