package com.cosmo.wanda_web.dto.users;

import com.cosmo.wanda_web.entities.ProfileType;
import com.cosmo.wanda_web.entities.User;

public class UserMinDto {
    private Long id;
    private String name;
    private String email;
    private ProfileType profileType;

    public UserMinDto(User entity) {
        id = entity.getId();
        name = entity.getName();
        profileType = entity.getProfileType();
        email = entity.getEmail();
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

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
