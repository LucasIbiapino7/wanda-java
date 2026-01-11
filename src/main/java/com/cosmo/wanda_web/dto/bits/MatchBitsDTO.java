package com.cosmo.wanda_web.dto.bits;

import java.util.ArrayList;
import java.util.List;

public class MatchBitsDTO {
    private int numberMatch;
    private InitialHandsBitsDTO initialHands; // pensando em não usar, melhor ter a ordem das jogadas
    private List<PlaysBitsDTO> plays = new ArrayList<>();
    private List<String> player1Plays;
    private List<String> Player2Plays;
    private SummaryBitsDTO summary;
    private ScoreBitsDTO cumulativeScore;
    private MatchDecisionBitsDTO decision;

    public MatchBitsDTO() {
    }

    public int getNumberMatch() {
        return numberMatch;
    }

    public void setNumberMatch(int numberMatch) {
        this.numberMatch = numberMatch;
    }

    public InitialHandsBitsDTO getInitialHands() {
        return initialHands;
    }

    public void setInitialHands(InitialHandsBitsDTO initialHands) {
        this.initialHands = initialHands;
    }

    public List<PlaysBitsDTO> getPlays() {
        return plays;
    }

    public void setPlays(List<PlaysBitsDTO> plays) {
        this.plays = plays;
    }

    public SummaryBitsDTO getSummary() {
        return summary;
    }

    public void setSummary(SummaryBitsDTO summary) {
        this.summary = summary;
    }

    public ScoreBitsDTO getCumulativeScore() {
        return cumulativeScore;
    }

    public void setCumulativeScore(ScoreBitsDTO cumulativeScore) {
        this.cumulativeScore = cumulativeScore;
    }

    public List<String> getPlayer1Plays() {
        return player1Plays;
    }

    public void setPlayer1Plays(List<String> player1Plays) {
        this.player1Plays = player1Plays;
    }

    public List<String> getPlayer2Plays() {
        return Player2Plays;
    }

    public void setPlayer2Plays(List<String> player2Plays) {
        Player2Plays = player2Plays;
    }

    public MatchDecisionBitsDTO getDecision() {
        return decision;
    }

    public void setDecision(MatchDecisionBitsDTO decision) {
        this.decision = decision;
    }
}
