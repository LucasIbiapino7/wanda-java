package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.services.utils.RoundInformation;

import java.util.ArrayList;
import java.util.List;

public class MatchInformationDTO {
    private Integer matchNumber;
    private List<String> player1cards = new ArrayList<>();
    private List<String> player2cards = new ArrayList<>();
    private List<RoundsDTO> plays = new ArrayList<>();
    private int tie;
    private int player1Winners;
    private int player2Winners;
    private int playerWinTurn;
    private CurrentScoreDTO currentScore;


    public MatchInformationDTO() {

    }

    public MatchInformationDTO(Integer matchNumber, List<RoundsDTO> plays, int tie, int player1Winners, int player2Winners, int playerWinTurn, CurrentScoreDTO currentScore) {
        this.matchNumber = matchNumber;
        this.plays = plays;
        this.tie = tie;
        this.player1Winners = player1Winners;
        this.player2Winners = player2Winners;
        this.playerWinTurn = playerWinTurn;
        this.currentScore = currentScore;
    }

    public MatchInformationDTO(RoundInformation turn) {
        tie = turn.getTurnTies();
        player1Winners = turn.getPlayer1TurnWins();
        player2Winners = turn.getPlayer2TurnWins();
        if (turn.getPlayer1TurnWins() == turn.getPlayer2TurnWins()){
            playerWinTurn = 0;
        } else if (turn.getPlayer1TurnWins() > turn.getPlayer2TurnWins()) {
            playerWinTurn = 1;
        }else {
            playerWinTurn = 2;
        }
    }

    public List<RoundsDTO> getPlays() {
        return plays;
    }

    public void setPlays(List<RoundsDTO> plays) {
        this.plays = plays;
    }

    public int getTie() {
        return tie;
    }

    public void setTie(int tie) {
        this.tie = tie;
    }

    public int getPlayer1Winners() {
        return player1Winners;
    }

    public void setPlayer1Winners(int player1Winners) {
        this.player1Winners = player1Winners;
    }

    public int getPlayer2Winners() {
        return player2Winners;
    }

    public void setPlayer2Winners(int player2Winners) {
        this.player2Winners = player2Winners;
    }

    public int getPlayerWinTurn() {
        return playerWinTurn;
    }

    public void setPlayerWinTurn(int playerWinTurn) {
        this.playerWinTurn = playerWinTurn;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public CurrentScoreDTO getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(CurrentScoreDTO currentScore) {
        this.currentScore = currentScore;
    }

    public List<String> getPlayer1cards() {
        return player1cards;
    }

    public void setPlayer1cards(List<String> player1cards) {
        this.player1cards = player1cards;
    }

    public List<String> getPlayer2cards() {
        return player2cards;
    }

    public void setPlayer2cards(List<String> player2cards) {
        this.player2cards = player2cards;
    }

    public void update(RoundInformation turn) {
        tie = turn.getTurnTies();
        player1Winners = turn.getPlayer1TurnWins();
        player2Winners = turn.getPlayer2TurnWins();
        if (turn.getPlayer1TurnWins() == turn.getPlayer2TurnWins()){
            playerWinTurn = 0;
        } else if (turn.getPlayer1TurnWins() > turn.getPlayer2TurnWins()) {
            playerWinTurn = 1;
        }else {
            playerWinTurn = 2;
        }
    }
}
