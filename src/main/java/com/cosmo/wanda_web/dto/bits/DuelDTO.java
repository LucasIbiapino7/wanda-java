package com.cosmo.wanda_web.dto.bits;

import com.cosmo.wanda_web.dto.users.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class DuelDTO {
    private String game;
    private UserDTO player1;
    private UserDTO player2;
    private UserDTO duelWInner;
    private List<MatchBitsDTO> matches = new ArrayList<>();
    private String type; // null em replay normal; "WALKOVER" quando decidido por sorteio
    private String reason; // motivo, quando WALKOVER

    public DuelDTO() {
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
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

    public UserDTO getDuelWInner() {
        return duelWInner;
    }

    public void setDuelWInner(UserDTO duelWInner) {
        this.duelWInner = duelWInner;
    }

    public List<MatchBitsDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchBitsDTO> matches) {
        this.matches = matches;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
