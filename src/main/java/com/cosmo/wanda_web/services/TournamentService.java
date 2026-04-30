package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.*;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.ClassroomRepository;
import com.cosmo.wanda_web.repositories.GameRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MatchOrchestrator matchOrchestrator;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Transactional
    public TournamentCreateDTO create(TournamentCreateDTO dto) {
        User user = userService.authenticated();

        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("O jogo nao foi encontrado!")
        );

        if (dto.getStartTime().isBefore(LocalDateTime.now().plusMinutes(15))) {
            throw new ResourceNotFoundException("Start Time inválido! Coloque uma de pelo menos 15min acima do momento atual.");
        }

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        tournament.setStartTime(dto.getStartTime());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setCreator(user);
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setCurrentParticipants(0);
        tournament.setStatus(TournamentStatus.OPEN);
        tournament.setAsPrivate(dto.getAsPrivate());
        tournament.setWinner(null);
        if (dto.getAsPrivate()) {
            tournament.setPassword(dto.getPassword());
        }
        tournament.setGame(game);

        // vinculo com turma
        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findByIdWithDetails(dto.getClassroomId()).orElseThrow(
                    () -> new ResourceNotFoundException("Turma não encontrada")
            );
            if (!classroom.getInstructor().getId().equals(user.getId())) {
                throw new TournamentException("Você não é o instructor desta turma");
            }
            if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
                throw new TournamentException("Não é possível criar torneios em uma turma arquivada");
            }
            tournament.setClassroom(classroom);
        }

        tournament = tournamentRepository.save(tournament);

        log.info("Torneio criado. nome={}, jogo={}, maxParticipantes={}, privado={}, turmaId={}",
                dto.getName(), dto.getGameName(), dto.getMaxParticipants(), dto.getAsPrivate(), dto.getClassroomId());

        return new TournamentCreateDTO(tournament);
    }

    @Transactional(readOnly = true)
    public TournamentWithParticipantsDTO findByIdWithParticipants(Long id) {
        Tournament tournament = tournamentRepository.findByIdWithParticipants(id);
        if (tournament == null){
            throw new TournamentException("O torneio não existe!");
        }
        return new TournamentWithParticipantsDTO(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAll(String searchTerm, Pageable pageable) {
        Page<Tournament> result = tournamentRepository.findByNameWithOrdering(searchTerm, pageable);
        return result.map(TournamentMinDTO::new);
    }

    @Transactional
    public TournamentMinDTO subscribeTournament(SubscribeTournamentDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.tournamentId()).orElseThrow(
                () -> new TournamentException("Torneio nao encontrado!"));

        if (!tournament.getStatus().toString().equals("OPEN")){
            throw new TournamentException("Torneio não está aberto");
        }
        User participant = userService.authenticated();
        if (!functionService.verifyFunctionsByGame(participant, tournament.getGame().getName())){
            throw new TournamentException("Você não tem as funções necessárias submetidas para participar de um torneio desse jogo!");
        }
        if(tournamentRepository.isUserInTournament(dto.tournamentId(), participant.getId())){
            throw new TournamentException("Você já está participando desse torneio!");
        }
        if (tournament.getAsPrivate() && !dto.password().equals(tournament.getPassword())){
            throw new TournamentException("A senha do torneio está incorreta!");
        }

        int inscrito = tournamentRepository.trySubscribe(dto.tournamentId());
        if (inscrito == 0) {
            throw new TournamentException("Torneio com o número máximo de participantes!");
        }

        tournament.getUsers().add(participant);
        participant.getTournaments().add(tournament);
        tournament = tournamentRepository.save(tournament);
        return new TournamentMinDTO(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAllParticipating(Pageable pageable) {
        User user = userService.authenticated();
        Page<Tournament> result = tournamentRepository.findAllByUser(user.getId(), pageable);
        Page<TournamentMinDTO> mapDto = result.map(TournamentMinDTO::new);
        for (TournamentMinDTO dto : mapDto) {
            if (dto.getStatus().toString().equals("OPEN") && Objects.equals(dto.getCreator().getId(), user.getId()) && dto.getCurrentParticipants() >= dto.getMaxParticipants()){
                dto.setCanReady(true);
            }
        }
        return mapDto;
    }

    @Transactional(readOnly = true)
    public BracketTournament getTournamentBracketById(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Match not found"));
        String tournamentData = tournament.getBracketJson();
        BracketTournament bracketTournament = jsonConverter.converterToBracketDto(tournamentData);
        return bracketTournament;
    }

    @Transactional
    public void run(Long id){
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio nao encontrado!"));
        User user = userService.authenticated();
        if (!Objects.equals(user.getId(), tournament.getCreator().getId())){
            throw new TournamentException("Voce nao eh o criador do torneio");
        }
        int changed = tournamentRepository.tryStart(id);
        if (changed == 0) {
            throw new TournamentException("Este torneio já foi iniciado ou não está aberto.");
        }
        tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio nao encontrado!"));
        running(tournament);
    }

    @Transactional
    public void startTournament(Long id) {
        int changed = tournamentRepository.tryStart(id);
        if (changed == 0) {
            throw new TournamentException("Este torneio já foi iniciado ou não está aberto.");
        }
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio nao encontrado!"));
        running(tournament);
    }

    private void running(Tournament tournament){
        log.info("Torneio iniciado. torneoId={}, jogo={}, participantes={}",
                tournament.getId(), tournament.getGame().getName(), tournament.getUsers().size());
        Map<Long, User> players = tournament.getUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Game game = tournament.getGame();

        List<Long> participants = new ArrayList<>(players.keySet());
        Collections.shuffle(participants);

        BracketTournament bracket = new BracketTournament();
        int roundNum = participants.size();
        int fase = 1;
        List<Long> currentParticipants = participants;

        while (currentParticipants.size() > 1){
            RoundTournamentDTO round = new RoundTournamentDTO();
            round.setName(describeRound(currentParticipants.size()));
            List<Long> nextRound = new ArrayList<>();

            for (int i = 0; i < currentParticipants.size(); i += 2) {
                Long player1 = currentParticipants.get(i);
                Long player2 = currentParticipants.get(i + 1);
                User userPlayer1 = players.get(player1);
                User userPlayer2 = players.get(player2);
                MatchResult result;
                try {
                    result = matchOrchestrator.run(userPlayer1, userPlayer2, game);
                } catch (Exception e) {
                    String errorContext = String.format(
                            "Erro na fase %d | %s (id=%d) vs %s (id=%d) | %s", fase, userPlayer1.getName(), player1,
                            userPlayer2.getName(), player2,e.getMessage()
                    );
                    log.error("Falha durante execução do torneio. torneoId={}, errorContext={}",tournament.getId(), errorContext, e);
                    if (!bracket.getRounds().isEmpty()) {
                        tournament.setBracketJson(jsonConverter.converterBracket(bracket));
                    }
                    tournament.setStatus(TournamentStatus.ERROR);
                    tournament.setErrorContext(errorContext);
                    tournamentRepository.save(tournament);
                    return;
                }

                Match match = new Match(userPlayer1, userPlayer2, LocalDateTime.now(),
                        result.getWinner(), result.getReplayJson(), game);
                if (tournament.getClassroom() != null) {
                    match.setClassroom(tournament.getClassroom());
                }
                matchRepository.save(match);
                playerService.updateWinners(userPlayer1, userPlayer2, match);

                Long matchId = match.getId();
                Long winnerId = result.getWinner().getId();

                log.info("Rodada do torneio. fase={}, player1Id={}, player2Id={}, matchId={}, vencedorId={}", fase, player1, player2, matchId, winnerId);

                MatchResultTournamentDTO matchResult = new MatchResultTournamentDTO();
                matchResult.setPlayer1Id(player1);
                matchResult.setPlayer2Id(player2);
                matchResult.setPlayer1Name(players.get(player1).getName());
                matchResult.setPlayer2Name(players.get(player2).getName());
                matchResult.setMatchId(matchId);
                matchResult.setWinnerId(winnerId);
                matchResult.setWinnerNameId(players.get(winnerId).getName());

                round.getMatches().add(matchResult);
                nextRound.add(winnerId);
            }

            bracket.getRounds().add(round);
            currentParticipants = nextRound;
            fase++;
        }

        String jsonData = jsonConverter.converterBracket(bracket);
        tournament.setBracketJson(jsonData);
        // Vou precisar mudar a lógica aqui! A ideia, é que agora setamos um User no Winner
        // e não mais apenas o Id
        log.info("Torneio finalizado. torneoId={}, vencedorId={}",
                tournament.getId(), currentParticipants.get(0));
        tournament.setWinner(players.get(currentParticipants.get(0)));
        playerService.updateWinnerTournament(currentParticipants.get(0));
        tournament.setStatus(TournamentStatus.FINISHED);
        tournamentRepository.save(tournament);
    }

    @Transactional
    public TournamentMinDTO update(Long id, TournamentUpdateDTO dto) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio não encontrado!"));
        User user = userService.authenticated();
        if (!Objects.equals(user.getId(), tournament.getCreator().getId())) {
            throw new TournamentException("Você não é o criador deste torneio.");
        }
        if (tournament.getStatus() != TournamentStatus.OPEN) {
            throw new TournamentException("Só é possível editar torneios com status OPEN.");
        }
        if (dto.getStartTime() != null && dto.getStartTime().isBefore(LocalDateTime.now().plusMinutes(15))) {
            throw new TournamentException("Start Time inválido! Coloque uma data de pelo menos 15min acima do momento atual.");
        }
        tournament.setName(dto.getName());
        tournament.setDescription(dto.getDescription());
        if (dto.getStartTime() != null) {
            tournament.setStartTime(dto.getStartTime());
        }
        tournament = tournamentRepository.save(tournament);
        log.info("Torneio atualizado. torneoId={}, novoNome={}, novoStartTime={}",
                tournament.getId(), tournament.getName(), tournament.getStartTime());
        return new TournamentMinDTO(tournament);
    }

    @Transactional
    public void cancel(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio não encontrado!"));
        User user = userService.authenticated();
        if (!Objects.equals(user.getId(), tournament.getCreator().getId())) {
            throw new TournamentException("Você não é o criador deste torneio.");
        }
        if (tournament.getStatus() != TournamentStatus.OPEN && tournament.getStatus() != TournamentStatus.ERROR) {
            throw new TournamentException("Só é possível cancelar torneios com status OPEN ou ERROR.");
        }
        tournament.setStatus(TournamentStatus.CANCELLED);
        tournamentRepository.save(tournament);
        log.info("Torneio cancelado. torneoId={}", tournament.getId());
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findByClassroom(Long classroomId, Pageable pageable) {
        Page<Tournament> result = tournamentRepository.findByClassroomId(classroomId, pageable);
        return result.map(TournamentMinDTO::new);
    }

    private String describeRound(int players) {
        return switch (players) {
            case 32 -> "Primeiras Fases";
            case 16 -> "Oitavas de Final";
            case 8  -> "Quartas de Final";
            case 4  -> "Semifinal";
            case 2  -> "Final";
            default -> players + " players";
        };
    }
}
