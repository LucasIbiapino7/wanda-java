package com.cosmo.wanda_web.dto.tournament;

public class MatchResultTournamentDTO {
    private Long player1Id;
    private String player1Name;
    private Long player2Id;
    private String player2Name;
    private Long matchId;
    private Long winnerId;
    private String winnerNameId;
    private Integer player1Score;
    private Integer player2Score;
    private Integer ties;
    private boolean walkover;  // true quando o confronto foi decidido por sorteio
    private String reason;

    public MatchResultTournamentDTO() {
    }

    public MatchResultTournamentDTO(Long player1Id, String player1Name, Long player2Id, String player2Name, Long matchId, Long winnerId, String winnerNameId) {
        this.player1Id = player1Id;
        this.player1Name = player1Name;
        this.player2Id = player2Id;
        this.player2Name = player2Name;
        this.matchId = matchId;
        this.winnerId = winnerId;
        this.winnerNameId = winnerNameId;
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

    public String getWinnerNameId() {
        return winnerNameId;
    }

    public void setWinnerNameId(String winnerNameId) {
        this.winnerNameId = winnerNameId;
    }

    public String getWinnerName() {
        return winnerNameId;
    }

    public void setWinnerName(String winnerName) {
        this.winnerNameId = winnerName;
    }

    public Integer getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }

    public Integer getTies() {
        return ties;
    }

    public void setTies(Integer ties) {
        this.ties = ties;
    }

    public boolean isWalkover() {
        return walkover;
    }

    public void setWalkover(boolean walkover) {
        this.walkover = walkover;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
