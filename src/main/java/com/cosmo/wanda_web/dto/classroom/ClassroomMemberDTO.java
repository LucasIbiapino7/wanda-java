package com.cosmo.wanda_web.dto.classroom;

import com.cosmo.wanda_web.entities.ClassroomStudent;

import java.time.LocalDateTime;

public class ClassroomMemberDTO {

    private Long userId;
    private String name;
    private String email;
    private LocalDateTime joinedAt;
    private boolean hasSubmitted;

    public ClassroomMemberDTO(ClassroomStudent cs, boolean hasSubmitted) {
        this.userId = cs.getStudent().getId();
        this.name = cs.getStudent().getName();
        this.email = cs.getStudent().getEmail();
        this.joinedAt = cs.getJoinedAt();
        this.hasSubmitted = hasSubmitted;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public boolean isHasSubmitted() { return hasSubmitted; }
}