package com.cosmo.wanda_web.dto.users;

import com.cosmo.wanda_web.entities.ProfileType;

public class UpdateProfileTypeDto {
    private Long userId;
    private ProfileType type;

    public UpdateProfileTypeDto(Long userId, ProfileType type) {
        this.userId = userId;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProfileType getType() {
        return type;
    }

    public void setType(ProfileType type) {
        this.type = type;
    }
}
