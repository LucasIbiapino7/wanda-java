package com.cosmo.wanda_web.dto.players;

import com.cosmo.wanda_web.entities.Badge;

public class BadgeDTO {
    private Long id;
    private String name;
    private String iconUrl;

    public BadgeDTO() {
    }

    public BadgeDTO(Long id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    public BadgeDTO(Badge badge) {
        this.id = badge.getId();
        this.name = badge.getName();
        this.iconUrl = badge.getIconUrl();
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
