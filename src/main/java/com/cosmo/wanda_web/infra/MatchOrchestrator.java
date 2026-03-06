package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.services.ChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MatchOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    private final Map<String, GameEngine> enginesByName;

    // A lista é de todos os componentes que implementam essa interface
    public MatchOrchestrator(java.util.List<GameEngine> engines) {
        this.enginesByName = engines.stream()
                .collect(Collectors.toMap(
                        e -> e.gameName().toLowerCase(),
                        e -> e
                ));
    }

//    public Long run(Challenge challenge) {
//        var gameName = String.valueOf(challenge.getGame().getName()).toLowerCase();
//        var engine = enginesByName.get(gameName);
//        if (engine == null) {
//            throw new IllegalArgumentException("Engine não encontrada para o jogo: " + gameName);
//        }
//        return engine.run(challenge);
//    }

    public Long run(Long playerId1, Long playerId2, Game game) {
        var gameName = String.valueOf(game.getName()).toLowerCase();
        var engine = enginesByName.get(gameName);
        if (engine == null) {
            log.error("Engine não encontrada para o jogo. gameName={}, enginesDisponiveis={}",
                    gameName, enginesByName.keySet());
            throw new IllegalArgumentException("Engine não encontrada para o jogo: " + gameName);
        }
        return engine.run(playerId1, playerId2);
    }
}
