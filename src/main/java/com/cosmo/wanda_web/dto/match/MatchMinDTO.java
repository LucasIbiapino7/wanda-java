package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.dto.game.GameDto;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.projections.MatchSummary;

public class MatchMinDTO {
    private Long id;
    private UserDTO player1;
    private UserDTO player2;
    private UserDTO winner;
    private GameDto game;

    public MatchMinDTO() {
    }

    public MatchMinDTO(Match entity) {
        id = entity.getId();
        player1 = new UserDTO(entity.getPlayer1());
        player2 = new UserDTO(entity.getPlayer2());
        winner = new UserDTO(entity.getWinner());
        game = new GameDto(entity.getGame());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserDTO getWinner() {
        return winner;
    }

    public void setWinner(UserDTO winner) {
        this.winner = winner;
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }
}
