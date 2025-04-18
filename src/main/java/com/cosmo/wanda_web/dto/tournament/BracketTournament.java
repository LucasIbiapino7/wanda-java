package com.cosmo.wanda_web.dto.tournament;

import java.util.ArrayList;
import java.util.List;

public class BracketTournament {
    private List<RoundTournamentDTO> rounds = new ArrayList<>();

    public BracketTournament() {
    }

    public List<RoundTournamentDTO> getRounds() {
        return rounds;
    }
}
