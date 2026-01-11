package com.cosmo.wanda_web.dto.match;

import com.cosmo.wanda_web.dto.game.GameDto;

public class ReplayDto {
    private GameDto game;
    private Object payload;

    public ReplayDto() {}

    public ReplayDto(GameDto game, Object payload) {
        this.game = game;
        this.payload = payload;
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
