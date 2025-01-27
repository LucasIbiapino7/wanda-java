package com.cosmo.wanda_web.services.utils;

public class TurnInformation {

    private int player1TurnWins;
    private int player2TurnWins;
    private int turnTies;

    public TurnInformation() {
        this.player1TurnWins = 0;
        this.player2TurnWins = 0;
        this.turnTies = 0;
    }

    public int getPlayer1TurnWins() {
        return player1TurnWins;
    }

    public int getPlayer2TurnWins() {
        return player2TurnWins;
    }

    public int getTurnTies() {
        return turnTies;
    }

    public void addTie() {
        this.turnTies++;
    }

    public void player1Win() {
        this.player1TurnWins++;
    }

    public void player2Win() {
        this.player2TurnWins++;
    }

    public void restart() {
        this.turnTies = 0;
        this.player1TurnWins = 0;
        this.player2TurnWins = 0;
    }

    @Override
    public String toString() {
        return "TurnInformation{" +
                "player1TurnWins=" + player1TurnWins +
                ", player2TurnWins=" + player2TurnWins +
                ", turnTies=" + turnTies +
                '}';
    }
}
