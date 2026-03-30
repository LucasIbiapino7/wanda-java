package com.cosmo.wanda_web.dto.python;

public class SessionCreateResponse {
    private String sessionId;

    public SessionCreateResponse() {
    }

    public SessionCreateResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
