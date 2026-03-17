package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.entities.Function;

import java.time.LocalDateTime;

public class AuditFunctionDTO {
    private String playerName;
    private String playerEmail;
    private String gameName;
    private String functionName;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public AuditFunctionDTO() {
    }

    public AuditFunctionDTO(Function entity) {
        playerName = entity.getPlayer().getName();
        playerEmail = entity.getPlayer().getEmail();
        gameName = entity.getGame().getName();
        functionName = entity.getName();
        createdAt = entity.getPlayer().getCreatedAt();
        updateAt = entity.getUpdatedAt();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
