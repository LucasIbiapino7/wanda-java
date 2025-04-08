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

        MatchResponseDTO matchResponseDTO = new MatchResponseDTO();

        // Verifica se o Id do primeiro aluno é válido
        User player1 = userRepository.findById(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado: " + dto.getPlayerId1()));

        matchResponseDTO.setPlayer1(new UserDTO(player1)); // Coloca o player 1 na resposta

        // Verifica se o Id do segundo aluno é válido
        User player2 = userRepository.findById(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado" + dto.getPlayerId2()));

        matchResponseDTO.setPlayer2(new UserDTO(player2)); // Coloca o player 2 na resposta

        // Pegar a função do primeiro aluno
        Function functionPlayer1 = functionRepository.findByPlayerId(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player1.getName() + " não tem função cadastrada"));

        // Pegar a função do segundo aluno
        Function functionPlayer2 = functionRepository.findByPlayerId(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("O aluno " + player2.getName() + " não tem função cadastrada"));

        // Objeto que vai ajudar a controlar uma partida
        Matches matches = new Matches(dto.getPlayerId1(), dto.getPlayerId2(), 5);

        // Informações sobre o turno
        RoundInformation turnInfo = new RoundInformation();

        int countRound = 1;
        CurrentScore score = new CurrentScore(); // Informacoes sobre o placar

        // Cada iteração é um round
        while (!matches.victory()){

            System.out.println("ROUND: " + countRound);

            //instancia os cards para começar um round
            matches.instanceCards();

            RoundInformationDTO roundInformationDTO = new RoundInformationDTO();
            roundInformationDTO.setTurnNumber(countRound);
            roundInformationDTO.getPlayer1cards().addAll(matches.getCardsPlayer1());
            roundInformationDTO.getPlayer2cards().addAll(matches.getCardsPlayer2());

            int turno;
            for (int i = 0; i < 3; i++){
                turno = i + 1;
                System.out.println("Turno " + turno); // depuração

                // instanciando os parametros
                matches.updateParametersPlayer(matches.getParametersPlayer1());
                matches.updateParametersPlayer(matches.getParametersPlayer2());

                System.out.println("Parametros do player 1");
                System.out.println(matches.getParametersPlayer1());

                System.out.println("Parametros do player 2");
                System.out.println(matches.getParametersPlayer2());

                // Montar o payload com a requisição
                RoundRequestDTO roundRequestDTO = new RoundRequestDTO(functionPlayer1.getFunction(),
                        matches.getParametersPlayer1(),
                        functionPlayer2.getFunction(),
                        matches.getParametersPlayer2());

                System.out.println("Payload:"); // aqui, também é enviada a função, mas não estou
                // printando para melhor visualizaçaõ
                System.out.println(roundRequestDTO.getPlayer1Parameters());
                System.out.println(roundRequestDTO.getPlayer2Parameters());

                // Faz a chamada da requisição
                TurnResponseDTO response = pythonClient.round(roundRequestDTO);

                // Simulando uma resposta apenas para teste
                //TurnResponseDTO response = new TurnResponseDTO("pedra", "papel");

                System.out.println("RESPOSTA DA REQUISIÇÃO");
                System.out.println(response);



                // Vefificar se a carta é válida ou não
                String cardTurnPlayer1 = matches.validateCardPlayer(matches.getCardsPlayer1(), response.getPlayer1Choice());
                String cardTurnPlayer2 = matches.validateCardPlayer(matches.getCardsPlayer2(), response.getPlayer2Choice());

                System.out.println("escolha do player 1: " + cardTurnPlayer1);
                System.out.println("escolha do player 2: " + cardTurnPlayer2);

                // Verifica quem ganhou o turno de acordo com as regras de conflict()
                Integer winnerTurn = matches.conflict(cardTurnPlayer1, cardTurnPlayer2);

                System.out.println("winner turn: " + winnerTurn);

                // PlayDTO
                PlaysDTO playsDTO = new PlaysDTO();
                playsDTO.setPlayNumber(turno);
                playsDTO.setPlayer1LogicChoice(response.getPlayer1Choice());
                playsDTO.setPlayer2LogicChoice(response.getPlayer2Choice());
                playsDTO.setPlayerCard1(cardTurnPlayer1);
                playsDTO.setPlayerCard2(cardTurnPlayer2);
                playsDTO.setWinnerOfPlay(winnerTurn);

                roundInformationDTO.getPlays().add(playsDTO);

                // Verifica o vencedor da jogada
                if (winnerTurn == 0){
                    turnInfo.addTie();
                } else if (winnerTurn == 1) {
                    turnInfo.player1Win();
                } else if (winnerTurn == 2) {
                    turnInfo.player2Win();
                }

                // Atualiza as cartas ao final de um turno -> coloca null nas cartas jogadas
                matches.updateCardsPlayer(matches.getCardsPlayer1(), cardTurnPlayer1);
                matches.updateCardsPlayer(matches.getCardsPlayer2(), cardTurnPlayer2);
            }

            // Um round tem três turnos, podendo dar empate ou vitória de um dos dois player

            System.out.println("Fim");
            System.out.println("empates: " + turnInfo.getTurnTies());
            System.out.println("player 1: " + turnInfo.getPlayer1TurnWins());
            System.out.println("player 2: " + turnInfo.getPlayer2TurnWins());

            matches.roundWinner(turnInfo, score);

            roundInformationDTO.update(turnInfo);
            roundInformationDTO.setCurrentScore(new CurrentScoreDTO(score));
            matchResponseDTO.getRounds().add(roundInformationDTO);

            turnInfo.restart();

            countRound++;
        }

        System.out.println("RESULTADO:");
        System.out.println("player 1: " + matches.getPlayer1RoundsVictories());
        System.out.println("player 2: " + matches.getPlayer2RoundsVictories());
        System.out.println("empates: " + matches.getTie());

        User winner = null;

        if (matches.getPlayer1RoundsVictories() > matches.getPlayer2RoundsVictories()){
            winner = player1;
        } else if (matches.getPlayer2RoundsVictories() > matches.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        matchResponseDTO.setPlayerWinner((winner == null) ? null : new UserDTO(winner));

        String matchData = jsonConverter.converter(matchResponseDTO);

        Match match = new Match(player1, player2, Instant.now(), winner, matchData);

        matchRepository.save(match);

        playerService.updateWinners(player1, player2, match);

        return match.getId();
    }

    @Transactional(readOnly = true)
    public MatchResponseDTO getReplayById(Long id) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        String matchData = match.getMatchData();
        MatchResponseDTO matchResponseDTO = jsonConverter.converterToDto(matchData);
        return matchResponseDTO;
    }
}
