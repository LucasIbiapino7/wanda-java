package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.services.MatchService;
import org.springframework.stereotype.Component;

@Component
public class JokenpoEngine implements GameEngine{

    private final MatchService matchService;

    public JokenpoEngine(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public String gameName() {
        return "jokenpo";
    }

    @Override
    public Long run(Long playerId1, Long playerId2) {
        var dto = new PlayedMatchDTO(
                playerId1,
                playerId2
        );
        return matchService.RunMatch(dto);
    }
}
