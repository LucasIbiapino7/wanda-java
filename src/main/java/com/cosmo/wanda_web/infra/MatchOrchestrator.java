package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MatchOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(MatchOrchestrator.class);

    private final Map<String, GameEngine> enginesByName;

    public MatchOrchestrator(List<GameEngine> engines) {
        this.enginesByName = engines.stream()
                .collect(Collectors.toMap(
                        e -> e.gameName().toLowerCase(),
                        e -> e
                ));
    }

    public MatchResult run(User player1, User player2, Game game) {
        var gameName = game.getName().toLowerCase();
        var engine = enginesByName.get(gameName);
        if (engine == null) {
            log.error("Engine não encontrada. gameName={}, enginesDisponiveis={}",
                    gameName, enginesByName.keySet());
            throw new IllegalArgumentException("Engine não encontrada para o jogo: " + gameName);
        }
        return engine.run(player1, player2);
    }

    public GameEngine getEngine(String gameName) {
        GameEngine engine = enginesByName.get(gameName.toLowerCase());
        if (engine == null) {
            throw new IllegalArgumentException("Engine não encontrada para o jogo: " + gameName);
        }
        return engine;
    }

    public List<GameEngine> getAllEngines() {
        return new ArrayList<>(enginesByName.values());
    }
}