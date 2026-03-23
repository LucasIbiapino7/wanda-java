package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.entities.Match;

import java.time.LocalDateTime;

public class AuditMatchDTO {
    private String playerName1;
    private String emailPlayer1;
    private String playerName2;
    private String emailPlayer2;
    private String gameName;
    private String winnerName;
    private LocalDateTime moment;

    public AuditMatchDTO() {
    }

    public AuditMatchDTO(Match entity) {
        playerName1 = entity.getPlayer1().getName();
        emailPlayer1 = entity.getPlayer1().getEmail();
        playerName2 = entity.getPlayer2().getName();
        emailPlayer2 = entity.getPlayer2().getEmail();
        gameName = entity.getGame().getName();
        winnerName = entity.getWinner() != null ? entity.getWinner().getName() : "";
        moment = entity.getMoment();
    }

    public String getPlayerName1() {
        return playerName1;
    }

    public void setPlayerName1(String playerName1) {
        this.playerName1 = playerName1;
    }

    public String getEmailPlayer1() {
        return emailPlayer1;
    }

    public void setEmailPlayer1(String emailPlayer1) {
        this.emailPlayer1 = emailPlayer1;
    }

    public String getPlayerName2() {
        return playerName2;
    }

    public void setPlayerName2(String playerName2) {
        this.playerName2 = playerName2;
    }

    public String getEmailPlayer2() {
        return emailPlayer2;
    }

    public void setEmailPlayer2(String emailPlayer2) {
        this.emailPlayer2 = emailPlayer2;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }
}
