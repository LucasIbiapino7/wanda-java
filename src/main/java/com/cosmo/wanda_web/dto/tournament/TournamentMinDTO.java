package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.dto.game.GameDto;
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
    private UserDTO creator;
    private UserDTO winnerId;
    private Boolean canReady;
    private GameDto game;

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
        creator = new UserDTO(entity.getCreator());
        winnerId = (entity.getWinner() != null) ? new UserDTO(entity.getWinner()) : null;
        canReady = false;
        game = new GameDto(entity.getGame());
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

    public UserDTO getCreator() {
        return creator;
    }

    public GameDto getGame() {
        return game;
    }

    public UserDTO getWinnerId() {
        return winnerId;
    }

    public Boolean getCanReady() {
        return canReady;
    }

    public void setCanReady(Boolean canReady) {
        this.canReady = canReady;
    }
}
