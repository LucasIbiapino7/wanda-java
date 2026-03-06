package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.bits.*;
import com.cosmo.wanda_web.dto.function.FunctionResponseDto;
import com.cosmo.wanda_web.dto.game.GameDto;
import com.cosmo.wanda_web.dto.match.*;
import com.cosmo.wanda_web.dto.python.RoundRequestDTO;
import com.cosmo.wanda_web.dto.python.TurnResponseDTO;
import com.cosmo.wanda_web.dto.users.UserDTO;
import com.cosmo.wanda_web.entities.Function;
import com.cosmo.wanda_web.entities.Game;
import com.cosmo.wanda_web.entities.Match;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.repositories.FunctionRepository;
import com.cosmo.wanda_web.repositories.GameRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.client.PythonClient;
import com.cosmo.wanda_web.services.utils.*;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PythonClient pythonClient;

    @Autowired
    private JsonConverter jsonConverter;

    // ENGINE DO JOKENPO
    @Transactional
    public Long RunMatch(PlayedMatchDTO dto){

        log.info("Partida Jokenpo iniciada. player1Id={}, player2Id={}", dto.getPlayerId1(), dto.getPlayerId2());

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
        Matches matches = new Matches(dto.getPlayerId1(), dto.getPlayerId2(), 21);

        // Informações sobre o turno
        RoundInformation RoundInfo = new RoundInformation();

        int countMatch = 1;
        CurrentScore score = new CurrentScore(); // Informacoes sobre o placar

        // Vai armazenar a jogada de cada player por partida
        List<String> cardsPlayer1 = new ArrayList<>();
        List<String> cardsPlayer2 = new ArrayList<>();

        // Cada iteração é uma Partida
        while (countMatch <= matches.getMatches()){

            //instancia os cards para começar um round
            matches.instanceCards();

            MatchInformationDTO matchInformationDTO = new MatchInformationDTO();
            matchInformationDTO.setMatchNumber(countMatch);

            int round = 1;

            // 1 Round -> função 1 (card1, card2, card3)

            // Instancia os parametros do Round 1 - as próprias cartas do jogador
            matches.updateParametersRound1();

            // Monta o payload da requisição do 1 round
            RoundRequestDTO roundRequestDTO = new RoundRequestDTO(functionJokenpo1Player1.getFunction(),
                    matches.getParametersPlayer1(),
                    functionJokenpo1Player2.getFunction(),
                    matches.getParametersPlayer2());

            // Faz a requisição
            TurnResponseDTO round1 = pythonClient.round(roundRequestDTO);

            // Valida e escolhe a carta que vai ser usada
            String cardRound1Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round1.getPlayer1Choice());
            String cardRound1Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round1.getPlayer2Choice());

            // Adiciona na lista de jogadas da partida!
            cardsPlayer1.add(cardRound1Player1);
            cardsPlayer2.add(cardRound1Player2);

            // Verifica quem ganhou o round1  de acordo com as regras de conflict()
            Integer winnerTurn = matches.conflict(cardRound1Player1, cardRound1Player2);

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

            RoundRequestDTO roundRequestDTORound2 = new RoundRequestDTO(functionJokenpo2Player1.getFunction(),
                    matches.getParametersPlayer1(),
                    functionJokenpo2Player2.getFunction(),
                    matches.getParametersPlayer2());

            TurnResponseDTO round2 = pythonClient.round(roundRequestDTORound2);

            // Valida e escolhe a carta que vai ser usada
            String cardRound2Player1 = matches.validateCardPlayer(matches.getCardsPlayer1(), round2.getPlayer1Choice());
            String cardRound2Player2 = matches.validateCardPlayer(matches.getCardsPlayer2(), round2.getPlayer2Choice());

            // Adiciona na lista de jogadas da partida!
            cardsPlayer1.add(cardRound2Player1);
            cardsPlayer2.add(cardRound2Player2);

            // Verifica quem ganhou o round1  de acordo com as regras de conflict()
            winnerTurn = matches.conflict(cardRound2Player1, cardRound2Player2);

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

            winnerTurn = matches.conflict(cardRound3Player1, cardRound3Player2);

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
        User winner = null;

        if (matches.getPlayer1RoundsVictories() >= matches.getPlayer2RoundsVictories()){
            winner = player1;
        } else if (matches.getPlayer2RoundsVictories() > matches.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        duelResponseDTO.setPlayerWinner((winner == null) ? null : new UserDTO(winner));

        String matchData = jsonConverter.converter(duelResponseDTO);

        Game game = gameRepository.findByNameIgnoreCase("jokenpo").orElseThrow(
                () -> new ResourceNotFoundException("Jogo não encontrado!")
        );

        Match match = new Match(player1, player2, LocalDateTime.now(), winner, matchData, game);

        matchRepository.save(match);

        playerService.updateWinners(player1, player2, match);

        log.info("Partida Jokenpo finalizada. matchId={}, vencedor={}, player1Vitorias={}, player2Vitorias={}",
                match.getId(),
                winner != null ? winner.getName() : "empate",
                matches.getPlayer1RoundsVictories(),
                matches.getPlayer2RoundsVictories());

        return match.getId();
    }

    @Transactional
    public Long RunMatchBits(PlayedMatchDTO dto){

        log.info("Partida BITS iniciada. player1Id={}, player2Id={}", dto.getPlayerId1(), dto.getPlayerId2());
        DuelDTO duelDto = new DuelDTO();

        // Verifica se o Id do primeiro aluno é válido
        User player1 = userRepository.findById(dto.getPlayerId1()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado: " + dto.getPlayerId1()));
        duelDto.setPlayer1(new UserDTO(player1));

        // Verifica se o Id do segundo aluno é válido
        User player2 = userRepository.findById(dto.getPlayerId2()).orElseThrow(
                () -> new ResourceNotFoundException("Aluno não encontrado" + dto.getPlayerId2()));
        duelDto.setPlayer2(new UserDTO(player2));

        // Pegar as funcoes de cada aluno
        FunctionResponseDto player1function = functionRepository.findByUserIdAndGameName(dto.getPlayerId1(), "bits").orElseThrow(
                () -> new ResourceNotFoundException("o usuário " + player1.getName() + "nao tem a funcao cadastrada"));

        FunctionResponseDto player2function = functionRepository.findByUserIdAndGameName(dto.getPlayerId2(), "bits").orElseThrow(
                () -> new ResourceNotFoundException("o usuário " + player2.getName() + "nao tem a funcao cadastrada"));

        MatchesBits match = new MatchesBits(dto.getPlayerId1(), dto.getPlayerId2(), 11);
        RoundInformation roundInformation = new RoundInformation();
        CurrentScore currentScore = new CurrentScore();

        // Vai armazenar a jogada de cada player por partida - lembrar de adicionar algo parecido no JSON do replay
        List<String> cardsPlayedPlayer1 = new ArrayList<>();
        List<String> cardsPlayedPlayer2 = new ArrayList<>();

        int countMatch = 1;
        while (match.endMatch(countMatch)){
            match.instanceInitialCards();
            MatchBitsDTO matchBitsDTO = new MatchBitsDTO();
            matchBitsDTO.setNumberMatch(countMatch);
            for (int i = 0; i <= 3; i++){
                // armazena infos da play
                PlaysBitsDTO playsDto = new PlaysBitsDTO(i + 1);
                // Preparar o DTO pra enviar na requisição - Parâmetros são enviados em uma List<Object>
                RoundBitsRequestDTO request = new RoundBitsRequestDTO(player1function.getCode(),
                        match.getParamsPlayer1(),
                        player2function.getCode(),
                        match.getParamsPlayer2());
                // Fazer a requisição - mock da request por enquanto
                TurnResponseDTO round = pythonClient.roundBits(request);
                // Validando a resposta
                String cardPlayer1 = match.validateChoice(match.getCardsPlayer1(), round.getPlayer1Choice());
                String cardPlayer2 = match.validateChoice(match.getCardsPlayer2(), round.getPlayer2Choice());

                // Lidando com o ReturnBitsDTO - vai ser armazenado no replay
                playsDto.resolveReturnedPlayer1(MatchesBits.ORDEM_MAO, round.getPlayer1Choice(), cardPlayer1);
                playsDto.resolveReturnedPlayer2(MatchesBits.ORDEM_MAO, round.getPlayer2Choice(), cardPlayer2);

                // Verificar o vencedor do round de acordo com as regras do jogo.
                // 0 - empate, 1 - player1 venceu, 2 - player2 venceu
                Integer roundWinner = match.conflict(cardPlayer1, cardPlayer2);
                // atualizar cartas e parametros
                match.updatePlayerCards(cardPlayer1, cardPlayer2);
                // atualizar as infos do round
                roundInformation.update(roundWinner);
                // atualizar a lista de cartas jogadas
                cardsPlayedPlayer1.add(cardPlayer1);
                cardsPlayedPlayer2.add(cardPlayer2);

                // atualiza o resolved da Play
                playsDto.updateResolved(cardPlayer1, cardPlayer2, roundWinner);

                // adiciona a play no array da partida
                matchBitsDTO.getPlays().add(playsDto);
            }
            // Adiciona a lista de jogadas ao DTO de replay e limpa a lista para o próximo round
            matchBitsDTO.setPlayer1Plays(new ArrayList<>(cardsPlayedPlayer1));
            cardsPlayedPlayer1.clear();
            matchBitsDTO.setPlayer2Plays(new ArrayList<>(cardsPlayedPlayer2));
            cardsPlayedPlayer2.clear();

            MatchDecisionBitsDTO decision = match.roundWinner(roundInformation, currentScore, matchBitsDTO.getPlays());
            matchBitsDTO.setDecision(decision);
            matchBitsDTO.setSummary(new SummaryBitsDTO(roundInformation));
            matchBitsDTO.setCumulativeScore(new ScoreBitsDTO(currentScore));

            duelDto.getMatches().add(matchBitsDTO);
            // Reseta os valores do round
            roundInformation.restart();

            countMatch++;
        }
        // Verifica quem venceu a partida e retorna esse valor
        User winner = null;

        if (match.getPlayer1RoundsVictories() >= match.getPlayer2RoundsVictories()){
            winner = player1;
        } else if (match.getPlayer2RoundsVictories() > match.getPlayer1RoundsVictories()) {
            winner = player2;
        }

        String matchData = jsonConverter.converterBits(duelDto);

        duelDto.setDuelWInner((winner == null) ? null : new UserDTO(winner));

        Game game = gameRepository.findByNameIgnoreCase("bits").orElseThrow(
                () -> new ResourceNotFoundException("Jogo não encontrado!")
        );

        Match matchResult = new Match(player1, player2, LocalDateTime.now(), winner, matchData, game);

        matchRepository.save(matchResult);

        playerService.updateWinners(player1, player2, matchResult);

        log.info("Partida BITS finalizada. matchId={}, vencedor={}, player1Vitorias={}, player2Vitorias={}",
                matchResult.getId(),
                winner != null ? winner.getName() : "empate",
                match.getPlayer1RoundsVictories(),
                match.getPlayer2RoundsVictories());

        return matchResult.getId();
    }

    // Possiveis retornos - BITS8, BIT16, BIT32, FIREWALL, STR
    private TurnResponseDTO mockRequestBits(RoundBitsRequestDTO request) {
        return new TurnResponseDTO("BIT8", "BIT16");
    }

    @Transactional(readOnly = true)
    public ReplayDto getReplayById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        String matchData = match.getMatchData();
        if (matchData == null || matchData.isBlank()) {
            throw new IllegalStateException("MatchData está vazio para o match " + id);
        }
        String gameName = match.getGame() != null ? match.getGame().getName() : null;
        if (gameName == null || gameName.isBlank()) {
            throw new IllegalStateException("Match sem game associado (match " + id + ")");
        }
        GameDto gameDto = new GameDto(match.getGame());
        var fillCharacter = (java.util.function.Consumer<UserDTO>) (u) -> {
            if (u == null || u.getId() == null) return;
            String character = playerService.findCharacterByUser(u.getId());
            u.setCharacter_url(character);
        };

        if ("bits".equalsIgnoreCase(gameName)) {
            DuelDTO bitsDto = jsonConverter.converterToBitsDuelDto(matchData);
            fillCharacter.accept(bitsDto.getPlayer1());
            fillCharacter.accept(bitsDto.getPlayer2());
            if (bitsDto.getDuelWInner() != null) {
                fillCharacter.accept(bitsDto.getDuelWInner());
            }

            return new ReplayDto(gameDto, bitsDto);
        }
        DuelResponseDTO jokenpoDto = jsonConverter.converterToDuelResponseDto(matchData);
        fillCharacter.accept(jokenpoDto.getPlayer1());
        fillCharacter.accept(jokenpoDto.getPlayer2());
        jokenpoDto.setGameDto(gameDto);
        return new ReplayDto(gameDto, jokenpoDto);
    }

    public Long winnerOfMatch(Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        return match.getWinner().getId();
    }

    public Page<MatchMinDTO> getAll(Pageable pageable) {
        User user = userService.authenticated();
        Page<Match> result = matchRepository.searchAllById(user.getId(), pageable);
        return result.map(MatchMinDTO::new);
    }
}
