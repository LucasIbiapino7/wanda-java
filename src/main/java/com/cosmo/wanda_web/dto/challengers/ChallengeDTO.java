package com.cosmo.wanda_web.dto.challengers;

public class ChallengeDTO {
    private Long challengedId;

    public ChallengeDTO() {
    }

    public ChallengeDTO(Long challengedId) {
        this.challengedId = challengedId;
    }

    public Long getChallengedId() {
        return challengedId;
    }

    public void setChallengedId(Long challengedId) {
        this.challengedId = challengedId;
    }
}
