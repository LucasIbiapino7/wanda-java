package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.dto.game.GameDto;

public class FunctionResponseDto {
    private String code;
    private String functionName;
    private GameDto game;

    public FunctionResponseDto() {
    }

    public FunctionResponseDto(String code, String functionName, GameDto game) {
        this.code = code;
        this.functionName = functionName;
        this.game = game;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }
}
