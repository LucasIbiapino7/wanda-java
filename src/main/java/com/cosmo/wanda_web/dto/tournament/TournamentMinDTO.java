package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentMinDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime startTime;
    private TournamentStatus status;
    private Boolean asPrivate;
    private String password;
    private int maxParticipants;
    private int currentParticipants;
    private Long creatorId;
    private Long winnerId;

    public TournamentMinDTO() {
    }

    public TournamentMinDTO(Tournament entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        startTime = entity.getStartTime();
        asPrivate = entity.getAsPrivate();
        if (entity.getAsPrivate()){
            password = entity.getPassword();
        }
        status = entity.getStatus();
        createdAt = entity.getCreatedAt();
        maxParticipants = entity.getMaxParticipants();
        currentParticipants = entity.getCurrentParticipants();
        creatorId = entity.getCreatorId();
        winnerId = (entity.getCreatorId() != null) ? entity.getWinnerId() : null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public Boolean getAsPrivate() {
        return asPrivate;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getWinnerId() {
        return winnerId;
    }
}
