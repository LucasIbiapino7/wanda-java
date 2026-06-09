package com.cosmo.wanda_web.dto.challengers;

public class ChallengeDTO {
    private Long challengedId;
    private String gameName;
    private Long classroomId;

    public ChallengeDTO() {
    }

    public ChallengeDTO(Long challengedId, String gameName) {
        this.challengedId = challengedId;
        this.gameName = gameName;
    }

    public Long getChallengedId() {
        return challengedId;
    }

    public void setChallengedId(Long challengedId) {
        this.challengedId = challengedId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }
}
