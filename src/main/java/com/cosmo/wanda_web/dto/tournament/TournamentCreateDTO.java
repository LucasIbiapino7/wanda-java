package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.entities.Tournament;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TournamentCreateDTO {
    private Long id;

    @NotBlank(message = "campo requerido!")
    @Size(min = 3, max = 40, message = "O nome precisa ter entre 3 e 40 caracteres!")
    private String name;
    @Size(max = 80, message = "A descrição deve possuir no máximo 80 caracteres!")
    private String description;
    @NotNull(message = "Data de início é obrigatória!")
    private LocalDateTime startTime;
    private Boolean asPrivate;
    private String password;
    @Positive(message = "O número de participantes deve ser positivo!")
    private int maxParticipants;
    private int currentParticipants;
    private Long creatorId;
    private Long winnerId;
    private String gameName;
    private Long classroomId;

    public TournamentCreateDTO() {
    }

    public TournamentCreateDTO(Tournament entity) {
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
        creatorId = entity.getCreator().getId();
        winnerId = null;
        gameName = entity.getGame().getName();
        classroomId = (entity.getClassroom() != null) ? entity.getClassroom().getId() : null;
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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }
}
