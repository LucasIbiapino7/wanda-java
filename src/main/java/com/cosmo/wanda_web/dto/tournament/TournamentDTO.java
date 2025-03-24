package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.entities.Tournament;

import java.time.LocalDateTime;

public class TournamentDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private Boolean asPrivate;
    private String password;
    private int maxParticipants;
    private int currentParticipants;
    private Long creatorId;
    private Long winnerId;

    public TournamentDTO() {
    }

    public TournamentDTO(Tournament entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        startTime = entity.getStartTime();
        asPrivate = entity.getAsPrivate();
        if (entity.getAsPrivate()){
            password = entity.getPassword();
        }
        maxParticipants = entity.getMaxParticipants();
        currentParticipants = entity.getCurrentParticipants();
        creatorId = entity.getCreatorId();
        winnerId = (entity.getCreatorId() != null) ? entity.getWinnerId() : null;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Boolean getAsPrivate() {
        return asPrivate;
    }

    public void setAsPrivate(Boolean asPrivate) {
        this.asPrivate = asPrivate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
}
