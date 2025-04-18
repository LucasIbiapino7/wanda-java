package com.cosmo.wanda_web.dto.tournament;

import java.util.ArrayList;
import java.util.List;

public class RoundTournamentDTO {
    private String name;
    private List<MatchResultTournamentDTO> matches = new ArrayList<>();

    public RoundTournamentDTO() {
    }

    public RoundTournamentDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MatchResultTournamentDTO> getMatches() {
        return matches;
    }
}
