package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.entities.User;

import java.time.LocalDateTime;

public class AuditUserDTO {
    private String name;
    private String email;
    private String profileType;
    private LocalDateTime createdAt;

    public AuditUserDTO() {
    }

    public AuditUserDTO(User entity) {
        name = entity.getName();
        email = entity.getEmail();
        profileType = entity.getProfileType().toString();
        createdAt = entity.getCreatedAt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
