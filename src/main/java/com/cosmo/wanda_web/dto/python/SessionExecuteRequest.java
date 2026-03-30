package com.cosmo.wanda_web.dto.python;

import java.util.List;

public class SessionExecuteRequest {
    private String sessionId;
    private List<Object> player1Parameters;
    private List<Object> player2Parameters;

    public SessionExecuteRequest() {
    }

    public SessionExecuteRequest(String sessionId, List<Object> player1Parameters, List<Object> player2Parameters) {
        this.sessionId = sessionId;
        this.player1Parameters = player1Parameters;
        this.player2Parameters = player2Parameters;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Object> getPlayer1Parameters() {
        return player1Parameters;
    }

    public void setPlayer1Parameters(List<Object> player1Parameters) {
        this.player1Parameters = player1Parameters;
    }

    public List<Object> getPlayer2Parameters() {
        return player2Parameters;
    }

    public void setPlayer2Parameters(List<Object> player2Parameters) {
        this.player2Parameters = player2Parameters;
    }
}
