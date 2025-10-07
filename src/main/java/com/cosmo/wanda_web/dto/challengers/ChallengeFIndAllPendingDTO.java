package com.cosmo.wanda_web.dto.challengers;

import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;

import java.time.LocalDateTime;

public class ChallengeFIndAllPendingDTO {
    private Long id;
    private Long challengerId;
    private String challengedName;
    private String challengerName;
    private LocalDateTime createdAt;
    private String gameName;

    public ChallengeFIndAllPendingDTO() {
    }


    public ChallengeFIndAllPendingDTO(FindAllPendingChallengerProjection projection) {
        id = projection.getId();
        challengerId = projection.getChallengerId();
        challengedName = projection.getChallengedName();
        challengerName = projection.getChallengerName();
        createdAt = LocalDateTime.now();
        gameName = projection.getGameName();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
