package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.match.*;
import com.cosmo.wanda_web.dto.python.RoundRequestDTO;
import com.cosmo.wanda_web.dto.python.TurnResponseDTO;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.utils.CurrentScore;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import com.cosmo.wanda_web.services.utils.RoundInformation;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.utils.Matches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PythonClient pythonClient;

    @Autowired
    private JsonConverter jsonConverter;

    @Transactional
    public Long RunMatch(PlayedMatchDTO dto){

        DuelResponseDTO duelResponseDTO = new DuelResponseDTO();

        // Verifica se o Id do primeiro aluno é válido
        User player1 = userRepository.findById(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado: " + dto.getPlayerId1()));

        duelResponseDTO.setPlayer1(new UserDTO(player1)); // Coloca o player 1 na resposta

        // Verifica se o Id do segundo aluno é válido
        User player2 = userRepository.findById(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado" + dto.getPlayerId2()));

        duelResponseDTO.setPlayer2(new UserDTO(player2)); // Coloca o player 2 na resposta

        // Pegar a função jokenpo 1 do primeiro aluno
        Function functionJokenpo1Player1 = functionRepository.findJokenpo1ByPlayerId(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player1.getName() + " não tem função 1 cadastrada"));
        // Pegar a função jokenpo  do primeiro aluno
        Function functionJokenpo2Player1 = functionRepository.findJokenpo2ByPlayerId(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player1.getName() + " não tem função 2 cadastrada"));


        // // Pegar a função jokenpo 1 do segundo aluno
        Function functionJokenpo1Player2 = functionRepository.findJokenpo1ByPlayerId(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player2.getName() + " não tem função 1 cadastrada"));
        // // Pegar a função jokenpo 2 do segundo aluno
        Function functionJokenpo2Player2 = functionRepository.findJokenpo2ByPlayerId(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player2.getName() + " não tem função 2 cadastrada"));

        // Objeto que vai ajudar a controlar uma partida
        Matches matches = new Matches(dto.getPlayerId1(), dto.getPlayerId2(), 51);

        // Informações sobre o turno
        RoundInformation RoundInfo = new RoundInformation();

        int countMatch = 1;
        CurrentScore score = new CurrentScore(); // Informacoes sobre o placar

        // Vai armazenar a jogada de cada player por partida
        List<String> cardsPlayer1 = new ArrayList<>();
        List<String> cardsPlayer2 = new ArrayList<>();

        // Cada iteração é uma Partida
        while (countMatch <= matches.getMatches()){

            System.out.println("PARTIDA: " + countMatch);

            //instancia os cards para começar um round
            matches.instanceCards();

            MatchInformationDTO matchInformationDTO = new MatchInformationDTO();
            matchInformationDTO.setMatchNumber(countMatch);

            System.out.println("Cartas do jogador 1 na partida " + countMatch);
            System.out.println(matches.getCardsPlayer1());

            System.out.println("Cartas do jogador 2 na partida " + countMatch);
            System.out.println(matches.getCardsPlayer2());

            int round = 1;

            System.out.println("Round " + round); // depuração

            // 1 Round -> função 1 (card1, card2, card3)

            // Instancia os parametros do Round 1 - as próprias cartas do jogador
            matches.updateParametersRound1();

            // Depuração
            System.out.println("Parametros do player 1 no round 1");
            System.out.println(matches.getParametersPlayer1());

            // Depuração
            System.out.println("Parametros do player 2 no round 1");
            System.out.println(matches.getParametersPlayer2());

            // Monta o payload da requisição do 1 round
            RoundRequestDTO roundRequestDTO = new RoundRequestDTO(functionJokenpo1Player1.getFunction(),
                    matches.getParametersPlayer1(),
                    functionJokenpo1Player2.getFunction(),
                    matches.getParametersPlayer2());

            // Faz a requisição
            TurnResponseDTO round1 = pythonClient.round(roundRequestDTO);

            // Depuração
            System.out.println("RESPOSTA DA REQUISIÇÃO");
            System.out.println(round1);

            // Valida e escolhe a carta que vai ser usada
            String cardRound1Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round1.getPlayer1Choice());
            String cardRound1Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round1.getPlayer2Choice());

            // Depuração
            System.out.println("escolha do player 1 no round1: " + cardRound1Player1);
            System.out.println("escolha do player 2 no round1: " + cardRound1Player2);

            // Adiciona na lista de jogadas da partida!
            cardsPlayer1.add(cardRound1Player1);
            cardsPlayer2.add(cardRound1Player2);

            // Verifica quem ganhou o round1  de acordo com as regras de conflict()
            Integer winnerTurn = matches.conflict(cardRound1Player1, cardRound1Player2);

            System.out.println("winner round1: " + winnerTurn);
            // PlayDTO
            RoundsDTO playsDTO = new RoundsDTO();
            playsDTO.setRoundNumber(round);
            playsDTO.setPlayerCard1(cardRound1Player1);
            playsDTO.setPlayerCard2(cardRound1Player2);
            playsDTO.setWinnerOfPlay(winnerTurn);

            matchInformationDTO.getPlays().add(playsDTO);

            // Verifica o vencedor da jogada
            if (winnerTurn == 0){
                RoundInfo.addTie();
            } else if (winnerTurn == 1) {
                RoundInfo.player1Win();
            } else if (winnerTurn == 2) {
                RoundInfo.player2Win();
            }

            // Atualiza as cartas ao final do round 1 -> coloca null nas cartas jogadas
            matches.updateCardsPlayer(matches.getCardsPlayer1(), cardRound1Player1);
            matches.updateCardsPlayer(matches.getCardsPlayer2(), cardRound1Player2);

            // Round 2 -> função 2 (card1, card2, opponentCard1, opponentCard2)

            matches.updateParametersRound2();

            // Depuração
            System.out.println("Parametros do player 1 no round 2");
            System.out.println(matches.getParametersPlayer1());

            // Depuração
            System.out.println("Parametros do player 2 no round 2");
            System.out.println(matches.getParametersPlayer2());

            RoundRequestDTO roundRequestDTORound2 = new RoundRequestDTO(functionJokenpo2Player1.getFunction(),
                    matches.getParametersPlayer1(),
                    functionJokenpo2Player2.getFunction(),
                    matches.getParametersPlayer2());

            TurnResponseDTO round2 = pythonClient.round(roundRequestDTORound2);

            System.out.println("RESPOSTA DA REQUISIÇÃO NO ROUND 2");
            System.out.println(round2);

            // Valida e escolhe a carta que vai ser usada
            String cardRound2Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round2.getPlayer1Choice());
            String cardRound2Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round2.getPlayer2Choice());

            // Depuração
            System.out.println("escolha do player 1 no round2: " + cardRound2Player1);
            System.out.println("escolha do player 2 no round2: " + cardRound2Player2);

            // Adiciona na lista de jogadas da partida!
            cardsPlayer1.add(cardRound2Player1);
            cardsPlayer2.add(cardRound2Player2);

            // Verifica quem ganhou o round1  de acordo com as regras de conflict()
            winnerTurn = matches.conflict(cardRound2Player1, cardRound2Player2);

            System.out.println("winner round2: " + winnerTurn);
            // PlayDTO
            RoundsDTO playsDTORound2 = new RoundsDTO();
            round++;
            playsDTORound2.setRoundNumber(round);
            playsDTORound2.setPlayerCard1(cardRound2Player1);
            playsDTORound2.setPlayerCard2(cardRound2Player2);
            playsDTORound2.setWinnerOfPlay(winnerTurn);

            matchInformationDTO.getPlays().add(playsDTORound2);

            // Verifica o vencedor da jogada
            if (winnerTurn == 0){
                RoundInfo.addTie();
            } else if (winnerTurn == 1) {
                RoundInfo.player1Win();
            } else if (winnerTurn == 2) {
                RoundInfo.player2Win();
            }

            // Atualiza as cartas ao final do round 2 -> coloca null nas cartas jogadas
            matches.updateCardsPlayer(matches.getCardsPlayer1(), cardRound2Player1);
            matches.updateCardsPlayer(matches.getCardsPlayer2(), cardRound2Player2);

            // Round 3 - as cartas que sobrarem

            String cardRound3Player1 = matches.getRound3Player1();
            String cardRound3Player2 = matches.getRound3Player2();

            cardsPlayer1.add(cardRound3Player1);
            cardsPlayer2.add(cardRound3Player2);

            System.out.println("escolha do player 1 no round3: " + cardRound3Player1);
            System.out.println("escolha do player 2 no round3: " + cardRound3Player2);

            winnerTurn = matches.conflict(cardRound3Player1, cardRound3Player2);

            System.out.println("winner round3: " + winnerTurn);
            // PlayDTO
            RoundsDTO playsDTORound3 = new RoundsDTO();
            round++;
            playsDTORound3.setRoundNumber(round);
            playsDTORound3.setPlayerCard1(cardRound3Player1);
            playsDTORound3.setPlayerCard2(cardRound3Player2);
            playsDTORound3.setWinnerOfPlay(winnerTurn);

            matchInformationDTO.getPlays().add(playsDTORound3);

            // Verifica o vencedor da jogada
            if (winnerTurn == 0){
                RoundInfo.addTie();
            } else if (winnerTurn == 1) {
                RoundInfo.player1Win();
            } else if (winnerTurn == 2) {
                RoundInfo.player2Win();
            }

            // Final da partida (loop while)

            System.out.println("Fim");
            System.out.println("empates: " + RoundInfo.getTurnTies());
            System.out.println("player 1: " + RoundInfo.getPlayer1TurnWins());
            System.out.println("player 2: " + RoundInfo.getPlayer2TurnWins());

            matches.roundWinner(RoundInfo, score);

            matchInformationDTO.update(RoundInfo);
            matchInformationDTO.getPlayer1cards().addAll(cardsPlayer1);
            cardsPlayer1.clear();
            matchInformationDTO.getPlayer2cards().addAll(cardsPlayer2);
            cardsPlayer2.clear();
            matchInformationDTO.setCurrentScore(new CurrentScoreDTO(score));
            duelResponseDTO.getMatches().add(matchInformationDTO);

            RoundInfo.restart();

            countMatch++;
        }

        System.out.println("RESULTADO:");
        System.out.println("player 1: " + matches.getPlayer1RoundsVictories());
        System.out.println("player 2: " + matches.getPlayer2RoundsVictories());
        System.out.println("empates: " + matches.getTie());

        User winner = null;

        if (matches.getPlayer1RoundsVictories() >= matches.getPlayer2RoundsVictories()){
            winner = player1;
        } else if (matches.getPlayer2RoundsVictories() > matches.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        duelResponseDTO.setPlayerWinner((winner == null) ? null : new UserDTO(winner));

        String matchData = jsonConverter.converter(duelResponseDTO);

        Match match = new Match(player1, player2, Instant.now(), winner, matchData);

        matchRepository.save(match);

        playerService.updateWinners(player1, player2, match);

        return match.getId();
    }

    @Transactional(readOnly = true)
    public DuelResponseDTO getReplayById(Long id) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        String matchData = match.getMatchData();
        DuelResponseDTO matchResponseDTO = jsonConverter.converterToDto(matchData);
        String characterPlayer1 = playerService.findCharacterByUser(matchResponseDTO.getPlayer1().getId());
        String characterPlayer2 = playerService.findCharacterByUser(matchResponseDTO.getPlayer2().getId());
        matchResponseDTO.getPlayer1().setCharacter_url(characterPlayer1);
        matchResponseDTO.getPlayer2().setCharacter_url(characterPlayer2);
        return matchResponseDTO;
    }

    public Long winnerOfMatch(Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        return match.getWinner().getId();
    }
}
