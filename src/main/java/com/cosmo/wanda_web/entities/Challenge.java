package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "challenger_id")
    private User challenger;

    @ManyToOne
    @JoinColumn(name = "challenged_id")
    private User challenged;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private Instant createdAt;

    @OneToOne
    @JoinColumn(name = "match_id")
    private Match match;

    public Challenge() {
    }

    public Challenge(Long id, User challenger, User challenged, ChallengeStatus status, Instant createdAt, Match match) {
        this.id = id;
        this.challenger = challenger;
        this.challenged = challenged;
        this.status = status;
        this.createdAt = createdAt;
        this.match = match;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getChallenger() {
        return challenger;
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    public User getChallenged() {
        return challenged;
    }

    public void setChallenged(User challenged) {
        this.challenged = challenged;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Challenge challenge = (Challenge) o;

        return Objects.equals(id, challenge.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
