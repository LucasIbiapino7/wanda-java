package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.dto.users.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class DuelResponseDTO {
    private UserDTO player1;
    private UserDTO player2;
    private List<MatchInformationDTO> matches = new ArrayList<>();
    private UserDTO playerWinner;

    public DuelResponseDTO() {
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

    public List<MatchInformationDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchInformationDTO> matches) {
        this.matches = matches;
    }

    public UserDTO getPlayerWinner() {
        return playerWinner;
    }

    public void setPlayerWinner(UserDTO playerWinner) {
        this.playerWinner = playerWinner;
    }
}
