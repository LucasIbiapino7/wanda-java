package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.Game;

public interface GameEngine {
    String gameName(); // retorna o nome
    Long run(Long playerId1, Long playerId2); // Roda a engine e retorna o ID do Match (ou lança exceção)
}
