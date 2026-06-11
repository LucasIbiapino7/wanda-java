package com.cosmo.wanda_web.infra;

import com.cosmo.wanda_web.dto.match.*;
import com.cosmo.wanda_web.dto.python.*;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.infra.dtos.FunctionInfo;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.services.MatchService;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.exceptions.MatchExecutionException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.CurrentScore;
import com.cosmo.wanda_web.services.utils.Matches;
import com.cosmo.wanda_web.services.utils.RoundInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JokenpoEngine implements GameEngine {

    private final FunctionRepository functionRepository;
    private final PythonClient pythonClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public JokenpoEngine(FunctionRepository functionRepository, PythonClient pythonClient) {
        this.functionRepository = functionRepository;
        this.pythonClient = pythonClient;
    }

    @Override
    public String gameName() {
        return "jokenpo";
    }

    @Override
    public List<FunctionInfo> functions() {
        return List.of(
                new FunctionInfo("jokenpo1", "Estratégia do Round 1", List.of("card1", "card2", "card3")),
                new FunctionInfo("jokenpo2", "Estratégia do Round 2", List.of("card1", "card2", "opponentCard1", "opponentCard2"))
        );
    }

    @Override
    public boolean hasAllFunctions(Long userId, FunctionRepository repository) {
        return functions().stream()
                .allMatch(f -> repository.findByUserIdAndName(userId, f.getName()).isPresent());
    }

    @Override
    public MatchResult run(User player1, User player2) {

        DuelResponseDTO duelResponseDTO = new DuelResponseDTO();
        duelResponseDTO.setPlayer1(new UserDTO(player1));
        duelResponseDTO.setPlayer2(new UserDTO(player2));

        // busca as funções dos players
        Function functionJokenpo1Player1 = functionRepository.findByUserIdAndName(player1.getId(), "jokenpo1")
                .orElseThrow(() -> new ResourceNotFoundException("O aluno " + player1.getName() + " não tem função 1 cadastrada"));

        Function functionJokenpo2Player1 = functionRepository.findByUserIdAndName(player1.getId(), "jokenpo2")
                .orElseThrow(() -> new ResourceNotFoundException("O aluno " + player1.getName() + " não tem função 2 cadastrada"));

        Function functionJokenpo1Player2 = functionRepository.findByUserIdAndName(player2.getId(), "jokenpo1")
                .orElseThrow(() -> new ResourceNotFoundException("O aluno " + player2.getName() + " não tem função 1 cadastrada"));

        Function functionJokenpo2Player2 = functionRepository.findByUserIdAndName(player2.getId(), "jokenpo2")
                .orElseThrow(() -> new ResourceNotFoundException("O aluno " + player2.getName() + " não tem função 2 cadastrada"));

        Matches matches = new Matches(player1.getId(), player2.getId(), 21);
        RoundInformation roundInfo = new RoundInformation();
        CurrentScore score = new CurrentScore();

        List<String> cardsPlayer1 = new ArrayList<>();
        List<String> cardsPlayer2 = new ArrayList<>();

        // abre duas sessões — uma pra cada função (round 1 e round 2)
        // round 3 é calculado internamente, não chama o Python
        String sessionIdRound1 = pythonClient.createSession(
                new SessionCreateRequest(functionJokenpo1Player1.getFunction(), functionJokenpo1Player2.getFunction())
        ).getSessionId();

        String sessionIdRound2 = pythonClient.createSession(
                new SessionCreateRequest(functionJokenpo2Player1.getFunction(), functionJokenpo2Player2.getFunction())
        ).getSessionId();

        try {
            int countMatch = 1;
            while (countMatch <= matches.getMatches()) {

                matches.instanceCards();
                MatchInformationDTO matchInformationDTO = new MatchInformationDTO();
                matchInformationDTO.setMatchNumber(countMatch);
                int round = 1;

                // Round 1 — função 1 via sessão dedicada
                matches.updateParametersRound1();
                SessionExecuteResponse round1 = pythonClient.executeRound(
                        new SessionExecuteRequest(sessionIdRound1, new ArrayList<>(matches.getParametersPlayer1()), new ArrayList<>(matches.getParametersPlayer2()))
                );

                // verifica se houve erro no container — timeout ou exception na estratégia do aluno
                if (round1.getError() != null) {
                    throw new MatchExecutionException(
                            "Erro no round 1: " + round1.getError() + " — " + round1.getErrorDetail()
                    );
                }

                String cardRound1Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round1.getPlayer1Choice());
                String cardRound1Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round1.getPlayer2Choice());
                cardsPlayer1.add(cardRound1Player1);
                cardsPlayer2.add(cardRound1Player2);

                Integer winnerTurn = matches.conflict(cardRound1Player1, cardRound1Player2);
                RoundsDTO playsDTO = new RoundsDTO();
                playsDTO.setRoundNumber(round);
                playsDTO.setPlayerCard1(cardRound1Player1);
                playsDTO.setPlayerCard2(cardRound1Player2);
                playsDTO.setWinnerOfPlay(winnerTurn);
                matchInformationDTO.getPlays().add(playsDTO);
                roundInfo.update(winnerTurn);

                matches.updateCardsPlayer(matches.getCardsPlayer1(), cardRound1Player1);
                matches.updateCardsPlayer(matches.getCardsPlayer2(), cardRound1Player2);

                // Round 2 — função 2 via sessão dedicada
                matches.updateParametersRound2();
                SessionExecuteResponse round2 = pythonClient.executeRound(
                        new SessionExecuteRequest(sessionIdRound2, new ArrayList<>(matches.getParametersPlayer1()), new ArrayList<>(matches.getParametersPlayer2()))
                );

                // verifica se houve erro no container — timeout ou exception na estratégia do aluno
                if (round2.getError() != null) {
                    throw new MatchExecutionException(
                            "Erro no round 2: " + round2.getError() + " — " + round2.getErrorDetail()
                    );
                }

                String cardRound2Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round2.getPlayer1Choice());
                String cardRound2Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round2.getPlayer2Choice());
                cardsPlayer1.add(cardRound2Player1);
                cardsPlayer2.add(cardRound2Player2);

                winnerTurn = matches.conflict(cardRound2Player1, cardRound2Player2);
                RoundsDTO playsDTORound2 = new RoundsDTO();
                round++;
                playsDTORound2.setRoundNumber(round);
                playsDTORound2.setPlayerCard1(cardRound2Player1);
                playsDTORound2.setPlayerCard2(cardRound2Player2);
                playsDTORound2.setWinnerOfPlay(winnerTurn);
                matchInformationDTO.getPlays().add(playsDTORound2);
                roundInfo.update(winnerTurn);

                matches.updateCardsPlayer(matches.getCardsPlayer1(), cardRound2Player1);
                matches.updateCardsPlayer(matches.getCardsPlayer2(), cardRound2Player2);

                // Round 3 — carta restante, calculado internamente sem chamar o Python
                String cardRound3Player1 = matches.getRound3Player1();
                String cardRound3Player2 = matches.getRound3Player2();
                cardsPlayer1.add(cardRound3Player1);
                cardsPlayer2.add(cardRound3Player2);

                winnerTurn = matches.conflict(cardRound3Player1, cardRound3Player2);
                RoundsDTO playsDTORound3 = new RoundsDTO();
                round++;
                playsDTORound3.setRoundNumber(round);
                playsDTORound3.setPlayerCard1(cardRound3Player1);
                playsDTORound3.setPlayerCard2(cardRound3Player2);
                playsDTORound3.setWinnerOfPlay(winnerTurn);
                matchInformationDTO.getPlays().add(playsDTORound3);
                roundInfo.update(winnerTurn);

                matches.roundWinner(roundInfo, score);
                matchInformationDTO.update(roundInfo);
                matchInformationDTO.getPlayer1cards().addAll(cardsPlayer1);
                cardsPlayer1.clear();
                matchInformationDTO.getPlayer2cards().addAll(cardsPlayer2);
                cardsPlayer2.clear();
                matchInformationDTO.setCurrentScore(new CurrentScoreDTO(score));
                duelResponseDTO.getMatches().add(matchInformationDTO);

                roundInfo.restart();
                countMatch++;
            }

        } finally {
            // encerra as duas sessões — containers são destruídos independente de sucesso ou erro
            pythonClient.closeSession(sessionIdRound1);
            pythonClient.closeSession(sessionIdRound2);
        }

        // determina o vencedor
        User winner = null;
        if (matches.getPlayer1RoundsVictories() >= matches.getPlayer2RoundsVictories()) {
            winner = player1;
        } else if (matches.getPlayer2RoundsVictories() > matches.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        duelResponseDTO.setPlayerWinner((winner == null) ? null : new UserDTO(winner));

        try {
            String replayJson = mapper.writeValueAsString(duelResponseDTO);
            return new MatchResult(winner, replayJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar replay do Jokenpo", e);
        }
    }

    @Override
    public Object parseReplay(String replayJson) {
        try {
            return mapper.readValue(replayJson, DuelResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao deserializar replay do Jokenpo", e);
        }
    }
    @Override
    public String walkoverReplayJson(User player1, User player2, User winner, String reason) {
        DuelResponseDTO duelResponseDTO = new DuelResponseDTO();
        duelResponseDTO.setPlayer1(new UserDTO(player1));
        duelResponseDTO.setPlayer2(new UserDTO(player2));
        duelResponseDTO.setPlayerWinner(winner == null ? null : new UserDTO(winner));
        duelResponseDTO.setType("WALKOVER");
        duelResponseDTO.setReason(reason);
        // matches fica vazio: nenhuma partida foi disputada
        try {
            return mapper.writeValueAsString(duelResponseDTO);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar replay de WALKOVER do jokenpo", e);
        }
    }

}
