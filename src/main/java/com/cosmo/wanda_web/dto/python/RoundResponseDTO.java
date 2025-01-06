package com.cosmo.wanda_web.dto.python;

public class RoundResponseDTO {

    private String player1Choice;
    private String player2Choice;

    public RoundResponseDTO() {
    }

    public RoundResponseDTO(String player1Choice, String player2Choice) {
        this.player1Choice = player1Choice;
        this.player2Choice = player2Choice;
    }

    public String getPlayer1Choice() {
        return player1Choice;
    }

    public void setPlayer1Choice(String player1Choice) {
        this.player1Choice = player1Choice;
    }

    public String getPlayer2Choice() {
        return player2Choice;
    }

    public void setPlayer2Choice(String player2Choice) {
        this.player2Choice = player2Choice;
    }

    @Override
    public String toString() {
        return "RoundResponseDTO{" +
                "player1Choice='" + player1Choice + '\'' +
                ", player2Choice='" + player2Choice + '\'' +
                '}';
    }
}
