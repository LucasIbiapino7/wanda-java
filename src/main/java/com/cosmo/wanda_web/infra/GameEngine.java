package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.dtos.FunctionInfo;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.FunctionRepository;

import java.util.List;

public interface GameEngine {
    String gameName(); // retorna o nome
    List<FunctionInfo> functions();
    boolean hasAllFunctions(Long userId, FunctionRepository repository);
    MatchResult run(User player1, User player2);
    Object parseReplay(String replayJson);

}
