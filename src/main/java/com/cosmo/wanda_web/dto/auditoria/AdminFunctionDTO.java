package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.entities.Function;
import java.time.LocalDateTime;

public class AdminFunctionDTO {
    private Long id;
    private String functionName;
    private String code;
    private String gameName;
    private LocalDateTime updatedAt;

    public AdminFunctionDTO(Function entity) {
        this.id = entity.getId();
        this.functionName = entity.getName();
        this.code = entity.getFunction();
        this.gameName = entity.getGame() != null ? entity.getGame().getName() : null;
        this.updatedAt = entity.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}