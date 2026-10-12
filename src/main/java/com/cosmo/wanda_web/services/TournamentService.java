package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.tournament.*;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.repositories.ClassroomRepository;
import com.cosmo.wanda_web.repositories.ClassroomStudentRepository;
import com.cosmo.wanda_web.repositories.GameRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.TournamentRepository;
import com.cosmo.wanda_web.services.exceptions.MatchExecutionException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import com.cosmo.wanda_web.services.exceptions.TournamentException;
import com.cosmo.wanda_web.services.utils.JsonConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    // Número de tentativas de uma partida antes de decidir por sorteio (W.O.)
    private static final int MATCH_RETRY_ATTEMPTS = 3;

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

    @Autowired
    private ClassroomStudentRepository classroomStudentRepository;

    @Autowired
    private NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    // Fonte de aleatoriedade do sorteio de W.O. Campo próprio para permitir
    // que os testes controlem o resultado (ex.: via spy de sortearVencedor).
    private final Random random = new Random();

    // Funções auxiliares
    // Define nomes para as fases
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

    // Cuida da formatação da descrição
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    // Valida quantidade de participantes
    private void validateParticipantQuantity(int maxParticipants) {
        List<Integer> allowedQuantities = List.of(4, 8, 16, 32);

        if (!allowedQuantities.contains(maxParticipants)) {
            throw new TournamentException("A quantidade de participantes deve ser 4, 8, 16 ou 32.");
        }
    }

    // Valida quantidade de participantes para início de torneio
    // Esse método fica pra não precisar mudar em todo o código
    private void validateTournamentReadyToStart(Tournament tournament){
        validateTournamentReadyToStart(tournament, false);
    }

    private void validateTournamentReadyToStart(Tournament tournament, boolean incomplete){
        if (tournament.getStatus() != TournamentStatus.OPEN){
            throw new TournamentException("Torneio já iniciado ou fechado.");
        }

        validateParticipantQuantity(tournament.getMaxParticipants());

        int participants = tournament.getUsers().size();
        int capacity = tournament.getMaxParticipants();

        if (participants == 0){
            throw new TournamentException("O torneio precisa de pelo menos um jogador real.");
        }

        if (participants > capacity){
            throw new TournamentException("A quantidade de participantes excedeu o limite desse torneio.");
        }

        // Torneio só inicia após confirmação por parte do instrutor
        if (participants < capacity && !incomplete){
            throw new TournamentException("Confirme o início do torneio com jogadores fictícios.");
        }

    }


    private boolean isAdmin(User user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean canUserManageTournament(Tournament tournament, User user) {
        if (Objects.equals(tournament.getCreator().getId(), user.getId()) || isAdmin(user)) {
            return true;
        }

        Classroom classroom = tournament.getClassroom();
        return classroom != null && classroom.getInstructor().getId().equals(user.getId());
    }

    private boolean canUserStartTournament(Tournament tournament, User user) {
        int participants = tournament.getCurrentParticipants();

        return tournament.getStatus() == TournamentStatus.OPEN
                && participants >= 1
                && participants <= tournament.getMaxParticipants()
                && canUserManageTournament(tournament, user);
    }

    private boolean isUserParticipant(Tournament tournament, User user) {
        return tournamentRepository.isUserInTournament(tournament.getId(), user.getId());
    }

    private TournamentMinDTO toMinDtoWithCanReady(Tournament tournament, User user) {
        TournamentMinDTO dto = new TournamentMinDTO(tournament);
        dto.setCanReady(canUserStartTournament(tournament, user));
        dto.setIsParticipant(isUserParticipant(tournament, user));
        return dto;
    }

    // Somente alunos e instrutores da turma podem acessar os torneios dela
    private void validateUserCanAccessClassroomTournament(Tournament tournament, User user) {
        Classroom classroom = tournament.getClassroom();

        if (classroom == null) {
            return;
        }

        boolean isInstructor = classroom.getInstructor().getId().equals(user.getId());

        boolean isStudent = classroomStudentRepository.existsByClassroomAndStudent(
                classroom.getId(),
                user.getId()
        );

        if (!isInstructor && !isStudent) {
            throw new TournamentException("Você não tem acesso a este torneio de turma.");
        }
    }

    @Transactional
    public TournamentCreateDTO create(TournamentCreateDTO dto) {
        User user = userService.authenticated();

        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("O jogo nao foi encontrado!")
        );

        validateParticipantQuantity(dto.getMaxParticipants());

        if (dto.getStartTime().isBefore(LocalDateTime.now().plusMinutes(5))) {
            throw new ResourceNotFoundException("Start Time inválido! Coloque uma de pelo menos 5 min acima do momento atual.");
        }

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setDescription(normalizeDescription(dto.getDescription()));
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

        User user = userService.authenticated();
        validateUserCanAccessClassroomTournament(tournament, user);

        return new TournamentWithParticipantsDTO(tournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAll(String searchTerm, Pageable pageable) {
        User user = userService.authenticated();

        Page<Tournament> result = tournamentRepository.findAvailableOpenTournaments(
                searchTerm,
                user.getId(),
                isAdmin(user),
                pageable
        );

        return result.map(tournament -> toMinDtoWithCanReady(tournament, user));
    }

    @Transactional
    public TournamentMinDTO subscribeTournament(SubscribeTournamentDTO dto) {
        Tournament tournament = tournamentRepository.findById(dto.tournamentId()).orElseThrow(
                () -> new TournamentException("Torneio nao encontrado!"));

        if (!tournament.getStatus().toString().equals("OPEN")){
            throw new TournamentException("Torneio não está aberto");
        }
        User participant = userService.authenticated();

        validateUserCanAccessClassroomTournament(tournament, participant);

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
        entityManager.flush();
        entityManager.refresh(tournament);
        return toMinDtoWithCanReady(tournament, participant);
    }

    @Transactional(readOnly = true)
    public Page<TournamentMinDTO> findAllParticipating(Pageable pageable) {
        User user = userService.authenticated();
        Page<Tournament> result = tournamentRepository.findAllByUser(user.getId(), pageable);
        return result.map(tournament -> toMinDtoWithCanReady(tournament, user));
    }

    @Transactional(readOnly = true)
    public BracketTournament getTournamentBracketById(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Torneio nao encontrado!"));
        String tournamentData = tournament.getBracketJson();
        BracketTournament bracketTournament;
        try {
            bracketTournament = (tournamentData == null || tournamentData.isBlank())
                    ? new BracketTournament()
                    : jsonConverter.converterToBracketDto(tournamentData);
        } catch (IllegalStateException e) {
            throw new TournamentException("O bracket deste torneio esta invalido ou incompleto.");
        }
        bracketTournament.setId(tournament.getId());
        bracketTournament.setName(tournament.getName());
        bracketTournament.setStatus(tournament.getStatus());
        bracketTournament.setErrorContext(tournament.getErrorContext());
        return bracketTournament;
    }

    // Método run deve iniciar torneio seguindo regras de jogadores fakes
    @Transactional
    public void run(Long id){
        run(id, false);
    }
        
    @Transactional 
    public void run(Long id, boolean incomplete){
        Tournament tournament = tournamentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Torneio não encontrado"));
        
        User user = userService.authenticated();

        if (!canUserManageTournament(tournament, user)){
            throw new TournamentException("Você não tem permissão para iniciar esse torneio.");
        }

        // Validação da quantidade de usuários
        validateTournamentReadyToStart(tournament, incomplete);

        int changed = tournamentRepository.tryStart((id));

        if (changed == 0){
            throw new TournamentException("Este torneio já foi iniciado ou está fechado.");
        }

        tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Torneio não encontrado."));

        running(tournament);
    }
    
    @Transactional
    public void startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Torneio nao encontrado!"));
            
        validateTournamentReadyToStart(tournament);

        int changed = tournamentRepository.tryStart(id);
        if (changed == 0) {
            throw new TournamentException("Este torneio já foi iniciado ou não está aberto.");
        }

        running(tournament);
    }

    private void running(Tournament tournament){
        log.info("Torneio iniciado. torneoId={}, jogo={}, participantes={}",
                tournament.getId(), tournament.getGame().getName(), tournament.getUsers().size());
        Map<Long, User> players = tournament.getUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Game game = tournament.getGame();

        // Pega lista completa e embaralhada
        List<TournamentParticipantDTO> tournamentParticipants = buildParticipants(tournament);

        // Map de participantes por ID
        Map<Long, TournamentParticipantDTO> participantsById = 
            tournamentParticipants.stream()
            .collect(Collectors.toMap(
                TournamentParticipantDTO::id,
                Function.identity()
            ));

        // Lista dos participantes do torneio
        List<Long> participants = tournamentParticipants.stream()
            .map(TournamentParticipantDTO::id)
            .collect(Collectors.toCollection(ArrayList::new));

        BracketTournament bracket = new BracketTournament();

        int fase = 1;
        List<Long> currentParticipants = participants;

        while (currentParticipants.size() > 1){
            RoundTournamentDTO round = new RoundTournamentDTO();
            round.setName(describeRound(currentParticipants.size()));
            List<Long> nextRound = new ArrayList<>();

            for (int i = 0; i < currentParticipants.size(); i += 2) {
                Long player1 = currentParticipants.get(i);
                Long player2 = currentParticipants.get(i + 1);

                TournamentParticipantDTO participant1 = participantsById.get(player1);
                TournamentParticipantDTO participant2 = participantsById.get(player2);

                if (participant1.fake() || participant2.fake()){
                    MatchResultTournamentDTO result = resolveAutomaticAdvance(participant1, participant2);

                    round.getMatches().add(result);
                    nextRound.add(result.getWinnerId());

                    log.info(
                        "Avanço automático. torneioId={}, fase={}, player1Id={}, player2Id={}, vencedorId={}",
                        tournament.getId(), fase, player1, player2, result.getWinnerId()
                    );

                    // Vai pra próxima partida
                    continue;
                }

                // Só partidas com ambos jogadores reais de fato ocorrem
                User userPlayer1 = players.get(player1);
                User userPlayer2 = players.get(player2);

                Match match;
                Long winnerId;
                boolean walkover = false;
                String walkoverReason = null;

                try {
                    // tenta a partida até MATCH_RETRY_ATTEMPTS vezes — absorve falha transitória
                    MatchResult result = runWithRetry(userPlayer1, userPlayer2, game, MATCH_RETRY_ATTEMPTS);

                    match = new Match(userPlayer1, userPlayer2, LocalDateTime.now(),
                            result.getWinner(), result.getReplayJson(), game);
                    if (tournament.getClassroom() != null) {
                        match.setClassroom(tournament.getClassroom());
                    }
                    matchRepository.save(match);
                    playerService.updateWinners(userPlayer1, userPlayer2, match);
                    winnerId = result.getWinner().getId();

                } catch (MatchExecutionException e) {
                    // a partida falhou em TODAS as tentativas → decide por sorteio e SEGUE
                    User winner = sortearVencedor(userPlayer1, userPlayer2);
                    walkover = true;
                    walkoverReason = "Partida não pôde ser disputada após múltiplas tentativas; vencedor decidido por sorteio.";

                    String replayJson = matchOrchestrator.getEngine(game.getName())
                            .walkoverReplayJson(userPlayer1, userPlayer2, winner, walkoverReason);

                    match = new Match(userPlayer1, userPlayer2, LocalDateTime.now(),
                            winner, replayJson, game);
                    if (tournament.getClassroom() != null) {
                        match.setClassroom(tournament.getClassroom());
                    }
                    matchRepository.save(match);
                    // NÃO chama updateWinners — W.O. não conta nas estatísticas dos alunos
                    winnerId = winner.getId();

                    log.warn("Confronto decidido por WALKOVER (sorteio). torneioId={}, player1Id={}, player2Id={}, vencedorSorteadoId={}, motivo={}",
                            tournament.getId(), player1, player2, winnerId, e.getMessage());

                } catch (Exception e) {
                    // erro catastrófico (não é falha de partida) → última defesa: marca ERROR e aborta
                    String errorContext = String.format(
                            "Erro na fase %d | %s (id=%d) vs %s (id=%d) | %s", fase, userPlayer1.getName(), player1,
                            userPlayer2.getName(), player2, e.getMessage()
                    );
                    log.error("Falha catastrófica durante execução do torneio. torneoId={}, errorContext={}", tournament.getId(), errorContext, e);
                    if (!bracket.getRounds().isEmpty()) {
                        tournament.setBracketJson(jsonConverter.converterBracket(bracket));
                    }
                    tournament.setStatus(TournamentStatus.ERROR);
                    tournament.setErrorContext(errorContext);
                    tournamentRepository.save(tournament);
                    return;
                }

                Long matchId = match.getId();

                log.info("Rodada do torneio. fase={}, player1Id={}, player2Id={}, matchId={}, vencedorId={}, walkover={}",
                        fase, player1, player2, matchId, winnerId, walkover);

                MatchResultTournamentDTO matchResult = new MatchResultTournamentDTO();
                matchResult.setPlayer1Id(player1);
                matchResult.setPlayer2Id(player2);
                matchResult.setPlayer1Name(players.get(player1).getName());
                matchResult.setPlayer2Name(players.get(player2).getName());
                matchResult.setMatchId(matchId);
                matchResult.setWinnerId(winnerId);
                matchResult.setWinnerNameId(players.get(winnerId).getName());
                matchResult.setWalkover(walkover);
                matchResult.setReason(walkoverReason);

                round.getMatches().add(matchResult);
                nextRound.add(winnerId);
            }

            bracket.getRounds().add(round);
            currentParticipants = nextRound;
            fase++;
        }

        Long winnerId = currentParticipants.get(0);
        User winner = players.get(winnerId);

        // A regra atual deve garantir que isso não ocorra
        if (winner == null) {
            throw new TournamentException(
                    "Não foi possível determinar um campeão real para o torneio."
            );
        }

        tournament.setBracketJson(jsonConverter.converterBracket(bracket));
        tournament.setWinner(winner);

        log.info("Torneio finalizado. torneioId={}, vencedorId={}",
                tournament.getId(), winnerId);

        playerService.updateWinnerTournament(winnerId);

        tournament.setStatus(TournamentStatus.FINISHED);
        tournamentRepository.save(tournament);
        tournament.getUsers().forEach(participant ->
                notificationService.create(participant.getId(), NotificationType.TOURNAMENT_FINISHED, tournament.getId())
        );
    }

    // Tenta rodar a partida até `tentativas` vezes. Só desiste (relança) se TODAS as
    // tentativas falharem por erro de execução da partida (MatchExecutionException).
    // Qualquer outro erro (config/infra) sobe na hora, sem retry.
    private MatchResult runWithRetry(User player1, User player2, Game game, int tentativas) {
        MatchExecutionException ultimaFalha = null;
        for (int tentativa = 1; tentativa <= tentativas; tentativa++) {
            try {
                return matchOrchestrator.run(player1, player2, game);
            } catch (MatchExecutionException e) {
                ultimaFalha = e;
                log.warn("Falha na tentativa {}/{} da partida: {} (id={}) vs {} (id={}) — {}",
                        tentativa, tentativas, player1.getName(), player1.getId(),
                        player2.getName(), player2.getId(), e.getMessage());
            }
        }
        throw ultimaFalha;
    }

    // Sorteia o vencedor de um confronto que não pôde ser disputado.
    // protected para permitir spy/override nos testes (resultado determinístico).
    protected User sortearVencedor(User player1, User player2) {
        return random.nextBoolean() ? player1 : player2;
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
        if (dto.getStartTime() != null && dto.getStartTime().isBefore(LocalDateTime.now().plusMinutes(5))) {
            throw new TournamentException("Start Time inválido! Coloque uma data de pelo menos 5 min acima do momento atual.");
        }
        tournament.setName(dto.getName());
        tournament.setDescription(normalizeDescription(dto.getDescription()));
        if (dto.getStartTime() != null) {
            tournament.setStartTime(dto.getStartTime());
        }
        tournament = tournamentRepository.save(tournament);
        log.info("Torneio atualizado. torneoId={}, novoNome={}, novoStartTime={}",
                tournament.getId(), tournament.getName(), tournament.getStartTime());
        return toMinDtoWithCanReady(tournament, user);
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
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(
                () -> new ResourceNotFoundException("Turma não encontrada.")
        );

        if (classroom.getStatus() == ClassroomStatus.ARCHIVED){
            return Page.empty(pageable);
        }
        Page<Tournament> result = tournamentRepository.findByClassroomId(classroomId, pageable);
        User user = userService.authenticated();
        return result.map(tournament -> toMinDtoWithCanReady(tournament, user));
    }

    private List<TournamentParticipantDTO> buildParticipants(Tournament tournament){
        validateParticipantQuantity(tournament.getMaxParticipants());

        // Cria lista de participantes reais do torneio
        List<TournamentParticipantDTO> participants = tournament.getUsers()
            .stream()
            .map(TournamentParticipantDTO::fromUser)
            .collect(Collectors.toCollection(ArrayList::new));

        // Validações
        if (participants.isEmpty()){
            throw new TournamentException("O torneio precisa de pelo menos um jogador real.");
        }

        if (participants.size() > tournament.getMaxParticipants()){
            throw new TournamentException("A quantidade de inscritos ultrapassou a capacidade máxima do torneio.");
        }

        // Calcula a quantidade de participantes que falta e adiciona na lista como jogadores fakes
        int missingParticipants = tournament.getMaxParticipants() - participants.size();

        int i;
        for (i = 1; i <= missingParticipants; i++){
            participants.add(TournamentParticipantDTO.fake(i));
        }

        // Realiza o sorteio das chaves
        Collections.shuffle(participants);

        return participants;
    }

    // Partidas com jogadores fakes possui resultado imediato
    private MatchResultTournamentDTO resolveAutomaticAdvance (
        TournamentParticipantDTO player1,
        TournamentParticipantDTO player2
    ){
        if (!player1.fake() && !player2.fake()){
            throw new IllegalArgumentException(
                "Avanço automático exige ao menos um jogador fake."
            );
        }

        // Se houver real, ele vence
        // Se for dois fakes, avança o primeiro
        TournamentParticipantDTO winner = player1.fake() && !player2.fake()
            ? player2
            : player1;
        
        String reason = player1.fake() && player2.fake()
            ? "O primeiro fictício avança em caso de confronto de fictícios"
            : "Jogador real avança automaticamente.";

        MatchResultTournamentDTO result = new MatchResultTournamentDTO(
            player1.id(),
            player1.name(),
            player2.id(),
            player2.name(),
            null, // Sem partida nem replay
            winner.id(),
            winner.name()
        );

        result.setPlayer1Fictitious(player1.fake());
        result.setPlayer2Fictitious(player2.fake());
        result.setAutomaticAdvance(true);
        result.setWalkover(false);
        result.setReason(reason);

        return result;
    }
}
