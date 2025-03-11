package com.cosmo.wanda_web.dto.challengers;

public class ChallengeIsAcceptedDTO {
    private Long challengeId;
    private Boolean accepted;

    public ChallengeIsAcceptedDTO() {
    }

    public ChallengeIsAcceptedDTO(Long challengeId, boolean accepted) {
        this.challengeId = challengeId;
        this.accepted = accepted;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
