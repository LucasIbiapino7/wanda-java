package com.cosmo.wanda_web.dto.game;

import com.cosmo.wanda_web.entities.Game;

public class GameDto {
    private Long id;
    private String name;
    private String description;

    public GameDto() {
    }

    public GameDto(Game entity) {
       id = entity.getId();
       name = entity.getName();
       description = entity.getDescription();
    }

    public GameDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
