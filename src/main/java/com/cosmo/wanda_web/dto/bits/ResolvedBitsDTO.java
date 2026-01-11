package com.cosmo.wanda_web.dto.bits;

public class ResolvedBitsDTO {
    private String card1;
    private String card2;
    private int winnerOfPlay; // 0=empate,1=player1,2=player2
    private boolean tie;
    private String reason; // efeitos pro front

    public ResolvedBitsDTO() {
    }

    public ResolvedBitsDTO(String card1, String card2, int winnerOfPlay, boolean tie, String reason) {
        this.card1 = card1;
        this.card2 = card2;
        this.winnerOfPlay = winnerOfPlay;
        this.tie = tie;
        this.reason = reason;
    }

    public String getCard1() {
        return card1;
    }

    public void setCard1(String card1) {
        this.card1 = card1;
    }

    public String getCard2() {
        return card2;
    }

    public void setCard2(String card2) {
        this.card2 = card2;
    }

    public int getWinnerOfPlay() {
        return winnerOfPlay;
    }

    public void setWinnerOfPlay(int winnerOfPlay) {
        this.winnerOfPlay = winnerOfPlay;
    }

    public boolean isTie() {
        return tie;
    }

    public void setTie(boolean tie) {
        this.tie = tie;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
