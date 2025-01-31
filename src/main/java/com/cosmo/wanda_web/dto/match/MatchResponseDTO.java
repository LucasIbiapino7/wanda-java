package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.dto.users.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class MatchResponseDTO {
    private Integer matchId;
    private UserDTO player1;
    private UserDTO player2;
    private List<TurnInformationDTO> turns = new ArrayList<>();
    private UserDTO playerWinner;

    public MatchResponseDTO() {
    }

    public UserDTO getPlayer1() {
        return player1;
    }

    public void setPlayer1(UserDTO player1) {
        this.player1 = player1;
    }

    public UserDTO getPlayer2() {
        return player2;
    }

    public void setPlayer2(UserDTO player2) {
        this.player2 = player2;
    }

    public List<TurnInformationDTO> getTurns() {
        return turns;
    }

    public void setTurns(List<TurnInformationDTO> turns) {
        this.turns = turns;
    }

    public UserDTO getPlayerWinner() {
        return playerWinner;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public void setPlayerWinner(UserDTO playerWinner) {
        this.playerWinner = playerWinner;
    }
}
