package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.dto.bits.*;
import com.cosmo.wanda_web.dto.function.FunctionResponseDto;
import com.cosmo.wanda_web.dto.python.SessionCreateRequest;
import com.cosmo.wanda_web.dto.python.SessionExecuteRequest;
import com.cosmo.wanda_web.dto.python.SessionExecuteResponse;
import com.cosmo.wanda_web.dto.python.TurnResponseDTO;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.dtos.FunctionInfo;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.services.MatchService;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.MatchExecutionException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.CurrentScore;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import com.cosmo.wanda_web.services.utils.MatchesBits;
import com.cosmo.wanda_web.services.utils.RoundInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BitsEngine implements GameEngine {

    private final FunctionRepository functionRepository;
    private final PythonClient pythonClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public BitsEngine(FunctionRepository functionRepository, PythonClient pythonClient) {
        this.functionRepository = functionRepository;
        this.pythonClient = pythonClient;
    }

    @Override
    public String gameName() {
        return "bits";
    }

    @Override
    public List<FunctionInfo> functions() {
        return List.of(new FunctionInfo("bits", "função usada no Round 1 do Jogo bits"
                , List.of("bit8", "bit16", "bit32", "firewall", "opp_last")));
    }

    @Override
    public boolean hasAllFunctions(Long userId, FunctionRepository repository) {
        return functions().stream().allMatch(f -> repository.findByUserIdAndName(userId, f.getName()).isPresent());
    }

    @Override
    public MatchResult run(User player1, User player2) {

        DuelDTO duelDto = new DuelDTO();
        duelDto.setPlayer1(new UserDTO(player1));
        duelDto.setPlayer2(new UserDTO(player2));

        // busca as funções dos players
        FunctionResponseDto player1function = functionRepository
                .findByUserIdAndGameName(player1.getId(), "bits")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "o usuário " + player1.getName() + " não tem a função cadastrada"));

        FunctionResponseDto player2function = functionRepository
                .findByUserIdAndGameName(player2.getId(), "bits")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "o usuário " + player2.getName() + " não tem a função cadastrada"));

        MatchesBits match = new MatchesBits(player1.getId(), player2.getId(), 11);
        RoundInformation roundInformation = new RoundInformation();
        CurrentScore currentScore = new CurrentScore();

        List<String> cardsPlayedPlayer1 = new ArrayList<>();
        List<String> cardsPlayedPlayer2 = new ArrayList<>();

        // abre a sessão no Python. container sobe com os códigos dos dois jogadores
        String sessionId = pythonClient.createSession(new SessionCreateRequest(player1function.getCode(), player2function.getCode())
        ).getSessionId();

        try {
            int countMatch = 1;
            while (match.endMatch(countMatch)) {
                match.instanceInitialCards();
                MatchBitsDTO matchBitsDTO = new MatchBitsDTO();
                matchBitsDTO.setNumberMatch(countMatch);

                for (int i = 0; i <= 3; i++) {
                    PlaysBitsDTO playsDto = new PlaysBitsDTO(i + 1);

                    // monta o request com sessionId e parâmetros do round — sem reenviar os códigos
                    SessionExecuteRequest request = new SessionExecuteRequest(sessionId,match.getParamsPlayer1(),
                            match.getParamsPlayer2());

                    // executa o round no container da sessão
                    SessionExecuteResponse round = pythonClient.executeRound(request);

                    // verifica se houve erro no container. timeout ou exception na estratégia do aluno
                    if (round.getError() != null) {
                        throw new MatchExecutionException("Erro durante a partida: " + round.getError() + " — " + round.getErrorDetail());
                    }

                    String cardPlayer1 = match.validateChoice(match.getCardsPlayer1(), round.getPlayer1Choice());
                    String cardPlayer2 = match.validateChoice(match.getCardsPlayer2(), round.getPlayer2Choice());

                    playsDto.resolveReturnedPlayer1(MatchesBits.ORDEM_MAO, round.getPlayer1Choice(), cardPlayer1);
                    playsDto.resolveReturnedPlayer2(MatchesBits.ORDEM_MAO, round.getPlayer2Choice(), cardPlayer2);

                    Integer roundWinner = match.conflict(cardPlayer1, cardPlayer2);
                    match.updatePlayerCards(cardPlayer1, cardPlayer2);
                    roundInformation.update(roundWinner);
                    cardsPlayedPlayer1.add(cardPlayer1);
                    cardsPlayedPlayer2.add(cardPlayer2);
                    playsDto.updateResolved(cardPlayer1, cardPlayer2, roundWinner);
                    matchBitsDTO.getPlays().add(playsDto);
                }

                matchBitsDTO.setPlayer1Plays(new ArrayList<>(cardsPlayedPlayer1));
                cardsPlayedPlayer1.clear();
                matchBitsDTO.setPlayer2Plays(new ArrayList<>(cardsPlayedPlayer2));
                cardsPlayedPlayer2.clear();

                MatchDecisionBitsDTO decision = match.roundWinner(roundInformation, currentScore, matchBitsDTO.getPlays());
                matchBitsDTO.setDecision(decision);
                matchBitsDTO.setSummary(new SummaryBitsDTO(roundInformation));
                matchBitsDTO.setCumulativeScore(new ScoreBitsDTO(currentScore));

                duelDto.getMatches().add(matchBitsDTO);
                roundInformation.restart();
                countMatch++;
            }

        } finally {
            // encerra a sessão, container é destruído independente de sucesso ou erro
            pythonClient.closeSession(sessionId);
        }

        // determina o vencedor
        User winner = null;
        if (match.getPlayer1RoundsVictories() >= match.getPlayer2RoundsVictories()) {
            winner = player1;
        } else if (match.getPlayer2RoundsVictories() > match.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        String replayJson;
        duelDto.setDuelWInner((winner == null) ? null : new UserDTO(winner));
        try {
            replayJson = mapper.writeValueAsString(duelDto);
            return new MatchResult(winner, replayJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar replay do BITS", e);
        }

    }

    @Override
    public Object parseReplay(String replayJson) {
        try {
            return mapper.readValue(replayJson, DuelDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao deserializar replay do BITS", e);
        }
    }

    @Override
    public String walkoverReplayJson(User player1, User player2, User winner, String reason) {
        DuelDTO duelDto = new DuelDTO();
        duelDto.setGame("bits");
        duelDto.setPlayer1(new UserDTO(player1));
        duelDto.setPlayer2(new UserDTO(player2));
        duelDto.setDuelWInner(winner == null ? null : new UserDTO(winner));
        duelDto.setType("WALKOVER");
        duelDto.setReason(reason);
        // matches fica vazio: nenhuma partida foi disputada
        try {
            return mapper.writeValueAsString(duelDto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar replay de WALKOVER do BITS", e);
        }
    }

}
