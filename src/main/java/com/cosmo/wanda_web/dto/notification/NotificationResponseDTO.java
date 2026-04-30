package com.cosmo.wanda_web.dto.notification;

import com.cosmo.wanda_web.entities.Notification;
import com.cosmo.wanda_web.entities.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;
    private NotificationType type;
    private Long referenceId;
    private LocalDateTime createdAt;

    public NotificationResponseDTO(Notification n) {
        this.id = n.getId();
        this.type = n.getType();
        this.referenceId = n.getReferenceId();
        this.createdAt = n.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}