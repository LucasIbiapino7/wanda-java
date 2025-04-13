package com.cosmo.wanda_web.dto.users;

import com.cosmo.wanda_web.entities.User;

public class UserDTO {
    private Long id;
    private String name;
    private String character_url;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String character_url) {
        this.id = id;
        this.name = name;
        this.character_url = character_url;
    }

    public UserDTO(User user) {
        id = user.getId();
        name = user.getName();
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

    public String getCharacter_url() {
        return character_url;
    }

    public void setCharacter_url(String character_url) {
        this.character_url = character_url;
    }
}
