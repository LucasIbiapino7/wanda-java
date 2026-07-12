package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.dto.game.GameDto;
import com.cosmo.wanda_web.dto.users.UserDTO;

public class ReplayDto {
    private GameDto game;
    private String gameName;
    private Long matchId;
    private UserDTO playerA;
    private UserDTO playerB;
    private UserDTO winner;
    private Object payload;

    public ReplayDto() {}

    public ReplayDto(GameDto game, Object payload) {
        this.game = game;
        this.gameName = game != null ? game.getName() : null;
        this.payload = payload;
    }

    public ReplayDto(Long matchId, GameDto game, UserDTO playerA, UserDTO playerB, UserDTO winner, Object payload) {
        this.matchId = matchId;
        this.game = game;
        this.gameName = game != null ? game.getName() : null;
        this.playerA = playerA;
        this.playerB = playerB;
        this.winner = winner;
        this.payload = payload;
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
        this.gameName = game != null ? game.getName() : null;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public UserDTO getPlayerA() {
        return playerA;
    }

    public void setPlayerA(UserDTO playerA) {
        this.playerA = playerA;
    }

    public UserDTO getPlayerB() {
        return playerB;
    }

    public void setPlayerB(UserDTO playerB) {
        this.playerB = playerB;
    }

    public UserDTO getWinner() {
        return winner;
    }

    public void setWinner(UserDTO winner) {
        this.winner = winner;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
