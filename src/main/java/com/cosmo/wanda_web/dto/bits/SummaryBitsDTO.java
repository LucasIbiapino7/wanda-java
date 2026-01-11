package com.cosmo.wanda_web.dto.bits;

import com.cosmo.wanda_web.services.utils.RoundInformation;

public class SummaryBitsDTO {
    private int p1Wins;
    private int p2Wins;
    private int ties;
    private int winnerOfMatch; //0=empate,1=player1,2=player2

    public SummaryBitsDTO() {
    }

    public SummaryBitsDTO(RoundInformation entity) {
        p1Wins = entity.getPlayer1TurnWins();
        p2Wins = entity.getPlayer2TurnWins();
        ties = entity.getTurnTies();
        System.out.println("SUMMARY DEBUG -> p1Wins=" + p1Wins + ", p2Wins=" + p2Wins + ", ties=" + ties);
        if (p1Wins > p2Wins){
            winnerOfMatch = 1;
        } else if (p2Wins > p1Wins) {
            winnerOfMatch = 2;
        }else {
            winnerOfMatch = 0;
        }
    }

    public SummaryBitsDTO(int p1Wins, int p2Wins, int ties, int winnerOfMatch) {
        this.p1Wins = p1Wins;
        this.p2Wins = p2Wins;
        this.ties = ties;
        this.winnerOfMatch = winnerOfMatch;
    }

    public int getP1Wins() {
        return p1Wins;
    }

    public void setP1Wins(int p1Wins) {
        this.p1Wins = p1Wins;
    }

    public int getP2Wins() {
        return p2Wins;
    }

    public void setP2Wins(int p2Wins) {
        this.p2Wins = p2Wins;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getWinnerOfMatch() {
        return winnerOfMatch;
    }

    public void setWinnerOfMatch(int winnerOfMatch) {
        this.winnerOfMatch = winnerOfMatch;
    }
}
