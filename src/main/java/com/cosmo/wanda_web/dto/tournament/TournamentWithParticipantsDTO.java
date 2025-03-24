package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;
import com.cosmo.wanda_web.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentWithParticipantsDTO {
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
    private List<UserDTO> participants = new ArrayList<>();

    public TournamentWithParticipantsDTO() {
    }

    public TournamentWithParticipantsDTO(Tournament entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        createdAt = entity.getCreatedAt();
        startTime = entity.getStartTime();
        status = entity.getStatus();
        asPrivate = entity.getAsPrivate();
        password = (entity.getAsPrivate() ? entity.getPassword() : null);
        maxParticipants = entity.getMaxParticipants();
        currentParticipants = entity.getCurrentParticipants();
        creatorId = entity.getCreatorId();
        winnerId = (entity.getWinnerId() != null) ? entity.getWinnerId() : null;
        for (User participant : entity.getUsers()) {
            participants.add(new UserDTO(participant));
        }
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

    public List<UserDTO> getParticipants() {
        return participants;
    }
}
