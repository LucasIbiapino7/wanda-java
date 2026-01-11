package com.cosmo.wanda_web.dto.bits;

import com.cosmo.wanda_web.services.utils.CurrentScore;

public class ScoreBitsDTO {
    private int player1;
    private int player2;
    private int ties;

    public ScoreBitsDTO() {
    }

    public ScoreBitsDTO(CurrentScore entity) {
        player1 = entity.getPlayer1();
        player2 = entity.getPlayer2();
        ties = entity.getTie();
    }

    public ScoreBitsDTO(int player1, int player2, int ties) {
        this.player1 = player1;
        this.player2 = player2;
        this.ties = ties;
    }

    public int getPlayer1() {
        return player1;
    }

    public void setPlayer1(int player1) {
        this.player1 = player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public void setPlayer2(int player2) {
        this.player2 = player2;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }
}
