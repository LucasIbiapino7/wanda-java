package com.cosmo.wanda_web.dto.python;

import java.util.List;

public class RoundRequestDTO {

    private String player1Function;
    private List<String> player1Parameters;
    private String player2Function;
    private List<String> player2Parameters;

    public RoundRequestDTO() {
    }

    public RoundRequestDTO(String player1Function, List<String> player1Parameters, String player2Function, List<String> player2Parameters) {
        this.player1Function = player1Function;
        this.player1Parameters = player1Parameters;
        this.player2Function = player2Function;
        this.player2Parameters = player2Parameters;
    }

    public String getPlayer1Function() {
        return player1Function;
    }

    public void setPlayer1Function(String player1Function) {
        this.player1Function = player1Function;
    }

    public List<String> getPlayer1Parameters() {
        return player1Parameters;
    }

    public void setPlayer1Parameters(List<String> player1Parameters) {
        this.player1Parameters = player1Parameters;
    }

    public String getPlayer2Function() {
        return player2Function;
    }

    public void setPlayer2Function(String player2Function) {
        this.player2Function = player2Function;
    }

    public List<String> getPlayer2Parameters() {
        return player2Parameters;
    }

    public void setPlayer2Parameters(List<String> player2Parameters) {
        this.player2Parameters = player2Parameters;
    }

    @Override
    public String toString() {
        return "RoundRequestDTO{" +
                "player1Function='" + player1Function + '\'' +
                ", player1Parameters=" + player1Parameters +
                ", player2Function='" + player2Function + '\'' +
                ", player2Parameters=" + player2Parameters +
                '}';
    }
}
