package com.cosmo.wanda_web.services.utils;

public class CurrentScore {
    private Integer player1;
    private Integer player2;
    private Integer tie;

    public CurrentScore() {
        player1 = 0;
        player2 = 0;
        tie = 0;
    }

    public CurrentScore(Integer player1, Integer player2, Integer tie) {
        this.player1 = player1;
        this.player2 = player2;
        this.tie = tie;
    }

    public Integer getPlayer1() {
        return player1;
    }

    public void setPlayer1(Integer player1) {
        this.player1 = player1;
    }

    public Integer getPlayer2() {
        return player2;
    }

    public void setPlayer2(Integer player2) {
        this.player2 = player2;
    }

    public Integer getTie() {
        return tie;
    }

    public void setTie(Integer tie) {
        this.tie = tie;
    }

    public void player1win(){
        player1++;
    }

    public void player2win(){
        player2++;
    }

    public void tie(){
        tie++;
    }

}
