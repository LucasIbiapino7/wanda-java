package com.cosmo.wanda_web.dto.bits;

import java.util.List;

public class PlaysBitsDTO {
    private int roundNumber;
    private ReturnBitsDTO returns;
    private ResolvedBitsDTO resolved;

    public PlaysBitsDTO() {
    }

    public PlaysBitsDTO(int roundNumber) {
        this.roundNumber = roundNumber;
        returns = new ReturnBitsDTO();
        resolved = new ResolvedBitsDTO();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public ReturnBitsDTO getReturns() {
        return returns;
    }

    public void setReturns(ReturnBitsDTO returns) {
        this.returns = returns;
    }

    public ResolvedBitsDTO getResolved() {
        return resolved;
    }

    public void setResolved(ResolvedBitsDTO resolved) {
        this.resolved = resolved;
    }

    public void resolveReturnedPlayer1(List<String> cardsGame, String returned, String choice){
        ReturnsObjectsDTO obj = new ReturnsObjectsDTO();
        obj.setRaw(returned);
        obj.setInvalidReturn(!cardsGame.contains(returned));
        obj.setFallbackTo((returned.equalsIgnoreCase(choice) ? null : choice));
        this.returns.setP1(obj);
    }

    public void resolveReturnedPlayer2(List<String> cardsGame, String returned, String choice){
        ReturnsObjectsDTO obj = new ReturnsObjectsDTO();
        obj.setRaw(returned);
        obj.setInvalidReturn(!cardsGame.contains(returned));
        obj.setFallbackTo((returned.equalsIgnoreCase(choice) ? null : choice));
        this.returns.setP2(obj);
    }

    public void updateResolved(String cardPlayer1, String cardPlayer2, Integer roundWinner) {
        resolved.setCard1(cardPlayer1);
        resolved.setCard2(cardPlayer2);
        resolved.setWinnerOfPlay(roundWinner);
        resolved.setTie(roundWinner == 0);
        resolved.setReason("");
    }
}
