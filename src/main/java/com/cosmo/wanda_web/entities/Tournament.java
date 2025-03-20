package com.cosmo.wanda_web.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private Boolean isPrivate;
    private String password;
    private int maxParticipants;
    private int currentParticipants;
    private Long creatorId;
    private Long winnerId;

    @ManyToMany(mappedBy = "tournaments")
    private Set<User> users = new HashSet<>();

    public Tournament() {
    }
}
