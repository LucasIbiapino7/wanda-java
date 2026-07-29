package com.cosmo.wanda_web.dto.tournament;

import com.cosmo.wanda_web.entities.Tournament;
import com.cosmo.wanda_web.entities.TournamentStatus;

import java.util.ArrayList;
import java.util.List;

public class BracketTournament {
    private Long id;
    private String name;
    private TournamentStatus status;
    private String errorContext;
    private List<RoundTournamentDTO> rounds = new ArrayList<>();

    public BracketTournament() {
    }

    public BracketTournament(Tournament tournament) {
        id = tournament.getId();
        name = tournament.getName();
        status = tournament.getStatus();
        errorContext = tournament.getErrorContext();
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

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public String getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(String errorContext) {
        this.errorContext = errorContext;
    }

    public List<RoundTournamentDTO> getRounds() {
        return rounds;
    }

    public void setRounds(List<RoundTournamentDTO> rounds) {
        this.rounds = rounds != null ? rounds : new ArrayList<>();
    }
}
