package com.cosmo.wanda_web.services.utils;

public class RoundInformation {
    private int player1TurnWins;
    private int player2TurnWins;
    private int turnTies;

    public RoundInformation() {
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
        return "RoundInformation{" +
                "player1TurnWins=" + player1TurnWins +
                ", player2TurnWins=" + player2TurnWins +
                ", turnTies=" + turnTies +
                '}';
    }

    public void update(Integer roundWinner) {
        if (roundWinner == 0){
            this.addTie();
        } else if (roundWinner == 1) {
            this.player1Win();
        } else if (roundWinner == 2) {
            this.player2Win();
        }
    }
}
