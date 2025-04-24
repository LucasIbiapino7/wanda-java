package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime startTime;
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;
    private Boolean asPrivate;
    private String password;
    private int maxParticipants;
    private int currentParticipants;
    @Column(name = "bracket_json", columnDefinition = "TEXT")
    private String bracketJson;
    private Long creatorId;
    private Long winnerId;

    @ManyToMany(mappedBy = "tournaments")
    private Set<User> users = new HashSet<>();

    public Tournament() {
    }

    public Tournament(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime startTime, TournamentStatus status, Boolean isPrivate, String password, int maxParticipants, int currentParticipants, String bracketJson, Long creatorId, Long winnerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.startTime = startTime;
        this.status = status;
        this.asPrivate = isPrivate;
        this.password = password;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.bracketJson = bracketJson;
        this.creatorId = creatorId;
        this.winnerId = winnerId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
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

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public String getBracketJson() {
        return bracketJson;
    }

    public void setBracketJson(String bracketJson) {
        this.bracketJson = bracketJson;
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

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tournament that = (Tournament) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
