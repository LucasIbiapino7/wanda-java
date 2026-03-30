package com.cosmo.wanda_web.dto.python;

public class SessionExecuteResponse {
    private String player1Choice;
    private String player2Choice;
    private String error;
    private String errorDetail;

    public SessionExecuteResponse() {
    }

    public SessionExecuteResponse(String player1Choice, String player2Choice, String error, String errorDetail) {
        this.player1Choice = player1Choice;
        this.player2Choice = player2Choice;
        this.error = error;
        this.errorDetail = errorDetail;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}
