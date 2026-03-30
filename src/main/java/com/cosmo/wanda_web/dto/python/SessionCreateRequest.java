package com.cosmo.wanda_web.dto.python;

public class SessionCreateRequest {
    private String player1Function;
    private String player2Function;

    public SessionCreateRequest() {
    }

    public SessionCreateRequest(String player1Function, String player2Function) {
        this.player1Function = player1Function;
        this.player2Function = player2Function;
    }

    public String getPlayer1Function() {
        return player1Function;
    }

    public void setPlayer1Function(String player1Function) {
        this.player1Function = player1Function;
    }

    public String getPlayer2Function() {
        return player2Function;
    }

    public void setPlayer2Function(String player2Function) {
        this.player2Function = player2Function;
    }
}
