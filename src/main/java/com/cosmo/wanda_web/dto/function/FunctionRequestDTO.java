package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.services.utils.AssistantStyle;

public class FunctionRequestDTO {
    private String code;
    private String assistantStyle;
    private String functionName; // Ajuda na representação de Jogos que tem mais de uma função, ex: Jokenpo1 e Jokenpo2!
    private String gameName; // Representa o nome do Jogo (usado na busca no BD)

    public FunctionRequestDTO() {
    }

    public FunctionRequestDTO(String code) {
        this.code = code;
        this.assistantStyle = null;
    }

    public FunctionRequestDTO(String code, String assistantStyle, String functionName, String gameName) {
        this.code = code;
        this.assistantStyle = assistantStyle;
        this.functionName = functionName;
        this.gameName = gameName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAssistantStyle() {
        return assistantStyle;
    }

    public void setAssistantStyle(String assistantStyle) {
        this.assistantStyle = assistantStyle;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
