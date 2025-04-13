package com.cosmo.wanda_web.dto.match;

public class RoundsDTO {
    private Integer roundNumber;
    private String playerCard1;
    private String playerCard2;
    private Integer winnerOfPlay;
    private Boolean tie;

    public RoundsDTO() {
    }

    public RoundsDTO(Integer playNumber, String playerCard1, String playerCard2, Integer winnerOfPlay, Boolean tie) {
        this.roundNumber = playNumber;
        this.playerCard1 = playerCard1;
        this.playerCard2 = playerCard2;
        this.winnerOfPlay = winnerOfPlay;
        this.tie = tie;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getPlayerCard1() {
        return playerCard1;
    }

    public void setPlayerCard1(String playerCard1) {
        this.playerCard1 = playerCard1;
    }

    public String getPlayerCard2() {
        return playerCard2;
    }

    public void setPlayerCard2(String playerCard2) {
        this.playerCard2 = playerCard2;
    }

    public Integer getWinnerOfPlay() {
        return winnerOfPlay;
    }

    public void setWinnerOfPlay(Integer winnerOfPlay) {
        this.winnerOfPlay = winnerOfPlay;
        if (winnerOfPlay == 0){
            this.tie = true;
        } else {
            this.tie = false;
        }
    }

    public Boolean getTie() {
        return tie;
    }

    public void setTie(Boolean tie) {
        this.tie = tie;
    }
}
