package com.cosmo.wanda_web.dto.classroom;

import com.cosmo.wanda_web.entities.Match;
import java.time.LocalDateTime;

public class DashboardMatchDTO {

    private Long matchId;
    private Long player1Id;
    private String player1Name;
    private Long player2Id;
    private String player2Name;
    private Long winnerId;
    private String winnerName;
    private LocalDateTime moment;

    public DashboardMatchDTO(Match match) {
        this.matchId = match.getId();
        this.player1Id = match.getPlayer1().getId();
        this.player1Name = match.getPlayer1().getName();
        this.player2Id = match.getPlayer2().getId();
        this.player2Name = match.getPlayer2().getName();
        this.winnerId = match.getWinner() != null ? match.getWinner().getId() : null;
        this.winnerName = match.getWinner() != null ? match.getWinner().getName() : null;
        this.moment = match.getMoment();
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
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

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }
}