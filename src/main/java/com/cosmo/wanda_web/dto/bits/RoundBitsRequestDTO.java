package com.cosmo.wanda_web.dto.bits;

import java.util.List;

public class RoundBitsRequestDTO {
    private String player1Function;
    private List<Object> player1Parameters;
    private String player2Function;
    private List<Object> player2Parameters;

    public RoundBitsRequestDTO() {
    }

    public RoundBitsRequestDTO(String player1Function, List<Object> player1Parameters, String player2Function, List<Object> player2Parameters) {
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

    public List<Object> getPlayer1Parameters() {
        return player1Parameters;
    }

    public void setPlayer1Parameters(List<Object> player1Parameters) {
        this.player1Parameters = player1Parameters;
    }

    public String getPlayer2Function() {
        return player2Function;
    }

    public void setPlayer2Function(String player2Function) {
        this.player2Function = player2Function;
    }

    public List<Object> getPlayer2Parameters() {
        return player2Parameters;
    }

    public void setPlayer2Parameters(List<Object> player2Parameters) {
        this.player2Parameters = player2Parameters;
    }
}
