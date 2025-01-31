package com.cosmo.wanda_web.dto.match;

public class PlaysDTO {
    private Integer playNumber;
    private String playerCard1;
    private String playerCard2;
    private Integer winnerOfPlay;
    private Boolean tie;

    public PlaysDTO() {
    }

    public PlaysDTO(Integer playNumber, String playerCard1, String playerCard2, Integer winnerOfPlay, Boolean tie) {
        this.playNumber = playNumber;
        this.playerCard1 = playerCard1;
        this.playerCard2 = playerCard2;
        this.winnerOfPlay = winnerOfPlay;
        this.tie = tie;
    }

    public Integer getPlayNumber() {
        return playNumber;
    }

    public void setPlayNumber(Integer playNumber) {
        this.playNumber = playNumber;
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
