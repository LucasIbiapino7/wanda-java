package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.services.MatchService;
import org.springframework.stereotype.Component;

@Component
public class BitsEngine implements GameEngine {

    private final MatchService matchService;

    public BitsEngine(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public String gameName() {
        return "bits";
    }

    @Override
    public Long run(Long playerId1, Long playerId2) {
        var dto = new PlayedMatchDTO(
                playerId1,
                playerId2
        );
        return matchService.RunMatchBits(dto);
    }
}
