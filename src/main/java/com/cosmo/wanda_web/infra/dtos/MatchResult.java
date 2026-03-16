package com.cosmo.wanda_web.infra.dtos;

import com.cosmo.wanda_web.entities.User;

public class MatchResult {
    private User winner;
    private String replayJson;

    public MatchResult() {
    }

    public MatchResult(User winner, String replayJson) {
        this.winner = winner;
        this.replayJson = replayJson;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public String getReplayJson() {
        return replayJson;
    }

    public void setReplayJson(String replayJson) {
        this.replayJson = replayJson;
    }
}
