package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeFIndAllPendingDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeIsAcceptedDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.infra.dtos.MatchResult;
import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;
import com.cosmo.wanda_web.repositories.*;
import com.cosmo.wanda_web.services.exceptions.ChallengeException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChallengeService {

    private static final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MatchOrchestrator matchOrchestrator;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private ClassroomStudentRepository classroomStudentRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Transactional
    public void challenge(ChallengeDTO dto) {
        User userChallenger = userService.authenticated();

        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("O jogo nao foi encontrado!")
        );

        User userChallenged = userRepository.findById(dto.getChallengedId()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário desafiado não foi encontrado")
        );

        if (challengeRepository.checkIfChallengePendingExists(
                userChallenger.getId(), userChallenged.getId(), game.getId()).isPresent()) {
            throw new ChallengeException("Já existe um desafio pendente para este jogo!");
        }

        if (!functionService.verifyFunctionsByGame(userChallenger, dto.getGameName())) {
            throw new ChallengeException("Você não tem as funções necessárias para desafiar nesse jogo!");
        }

        // Vínculo com turma — opcional
        Classroom classroom = null;
        if (dto.getClassroomId() != null) {
            classroom = classroomRepository.findByIdWithDetails(dto.getClassroomId()).orElseThrow(
                    () -> new ResourceNotFoundException("Turma não encontrada")
            );
            if (classroom.getStatus() != ClassroomStatus.ACTIVE) {
                throw new ChallengeException("Não é possível criar desafios em uma turma arquivada");
            }
            if (!classroomStudentRepository.existsByClassroomAndStudent(dto.getClassroomId(), userChallenger.getId())) {
                throw new ChallengeException("Você não é membro desta turma");
            }
            if (!classroomStudentRepository.existsByClassroomAndStudent(dto.getClassroomId(), userChallenged.getId())) {
                throw new ChallengeException("O usuário desafiado não é membro desta turma");
            }
        }

        log.info("Desafio enviado. desafiadorId={}, desafiadoId={}, jogo={}, turmaId={}",
                userChallenger.getId(), dto.getChallengedId(), dto.getGameName(), dto.getClassroomId());

        Challenge challenge = new Challenge();
        challenge.setChallenger(userChallenger);
        challenge.setChallenged(userChallenged);
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setMatch(null);
        challenge.setStatus(ChallengeStatus.PENDING);
        challenge.setGame(game);
        challenge.setClassroom(classroom);

        challengeRepository.save(challenge);
    }

    @Transactional(readOnly = true)
    public Page<ChallengeFIndAllPendingDTO> findAllPending(Pageable pageable) {
        User user = userService.authenticated();
        Page<FindAllPendingChallengerProjection> allPending = challengeRepository.findAllPending(user.getId(), pageable);
        return allPending.map(ChallengeFIndAllPendingDTO::new);
    }

    @Transactional
    public Long isAccepted(ChallengeIsAcceptedDTO dto) {
        Challenge challenge = challengeRepository.findById(dto.getChallengeId()).orElseThrow(
                () -> new ResourceNotFoundException("esse Challenge não existe"));

        Long currentUserId = userService.authenticated().getId();
        if (!challenge.getChallenged().getId().equals(currentUserId)) {
            throw new ChallengeException("Você não pode aceitar este desafio.");
        }
        if (!dto.getAccepted()) {
            log.info("Desafio recusado. challengeId={}", dto.getChallengeId());
            challengeUpdate(dto.getChallengeId(), ChallengeStatus.DECLINED, null);
            return null;
        }

        log.info("Desafio aceito, iniciando partida. challengeId={}, desafiadorId={}, desafiadoId={}",
                dto.getChallengeId(),
                challenge.getChallenger().getId(),
                challenge.getChallenged().getId());

        MatchResult result = matchOrchestrator.run(challenge.getChallenger(), challenge.getChallenged(), challenge.getGame());

        Match match = new Match(challenge.getChallenger(),challenge.getChallenged(),LocalDateTime.now(),result.getWinner(),result.getReplayJson(),
                challenge.getGame());
        if (challenge.getClassroom() != null) {
            match.setClassroom(challenge.getClassroom());
        }
        matchRepository.save(match);
        playerService.updateWinners(challenge.getChallenger(), challenge.getChallenged(), match);

        log.info("Partida do desafio finalizada. challengeId={}, matchId={}", dto.getChallengeId(), match.getId());
        challengeUpdate(dto.getChallengeId(), ChallengeStatus.ACCEPTED, match);
        return match.getId();
    }

    @Transactional
    private void challengeUpdate(Long challengeId, ChallengeStatus status, Match match) {
        challengeRepository.updateChallenge(challengeId, status, match);
    }

    @Transactional(readOnly = true)
    public Page<ChallengeFIndAllPendingDTO> findByClassroom(Long classroomId, Pageable pageable) {
        Page<FindAllPendingChallengerProjection> result = challengeRepository.findByClassroomId(classroomId, pageable);
        return result.map(ChallengeFIndAllPendingDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ChallengeFIndAllPendingDTO> findByClassroomAndUser(Long classroomId, Pageable pageable) {
        User user = userService.authenticated();
        Page<FindAllPendingChallengerProjection> result = challengeRepository
                .findByClassroomAndUser(classroomId, user.getId(), pageable);
        return result.map(ChallengeFIndAllPendingDTO::new);
    }
}
