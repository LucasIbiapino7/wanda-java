package com.cosmo.wanda_web.dto.challengers;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ChallengeFIndAllPendingDTO {
    private Long id;
    private Long challengerId;
    private Long challengedId;
    private String challengedName;
    private String challengerName;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
    private LocalDateTime finishedAt;
    private String status;
    private String gameName;
    private Long classroomId;
    private Long matchId;
    private Long winnerId;
    private String winnerName;
    private String resultSummary;

    public ChallengeFIndAllPendingDTO() {
    }


    public ChallengeFIndAllPendingDTO(FindAllPendingChallengerProjection projection) {
        id = projection.getId();
        challengerId = projection.getChallengerId();
        challengedName = projection.getChallengedName();
        challengerName = projection.getChallengerName();
        createdAt = projection.getCreatedAt() == null
                ? null
                : LocalDateTime.ofInstant(projection.getCreatedAt(), ZoneId.systemDefault());
        gameName = projection.getGameName();
    }

    public ChallengeFIndAllPendingDTO(Challenge challenge) {
        id = challenge.getId();
        challengerId = challenge.getChallenger() != null ? challenge.getChallenger().getId() : null;
        challengerName = challenge.getChallenger() != null ? challenge.getChallenger().getName() : null;
        challengedId = challenge.getChallenged() != null ? challenge.getChallenged().getId() : null;
        challengedName = challenge.getChallenged() != null ? challenge.getChallenged().getName() : null;
        createdAt = challenge.getCreatedAt();
        answeredAt = challenge.getAnsweredAt();
        finishedAt = challenge.getAnsweredAt();
        status = challenge.getStatus() != null ? challenge.getStatus().name() : null;
        gameName = challenge.getGame() != null ? challenge.getGame().getName() : null;
        classroomId = challenge.getClassroom() != null ? challenge.getClassroom().getId() : null;

        if (challenge.getMatch() != null) {
            matchId = challenge.getMatch().getId();
            if (challenge.getMatch().getWinner() != null) {
                winnerId = challenge.getMatch().getWinner().getId();
                winnerName = challenge.getMatch().getWinner().getName();
                resultSummary = winnerName + " venceu";
            } else {
                resultSummary = "Empate";
            }
        } else if ("DECLINED".equals(status)) {
            resultSummary = "Recusado";
        } else if ("PENDING".equals(status)) {
            resultSummary = "Pendente";
        }
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

    public Long getChallengedId() {
        return challengedId;
    }

    public void setChallengedId(Long challengedId) {
        this.challengedId = challengedId;
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

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }
}
