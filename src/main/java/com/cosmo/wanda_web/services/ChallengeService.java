package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeFIndAllPendingDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeIsAcceptedDTO;
import com.cosmo.wanda_web.entities.*;
import com.cosmo.wanda_web.infra.MatchOrchestrator;
import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;
import com.cosmo.wanda_web.repositories.ChallengeRepository;
import com.cosmo.wanda_web.repositories.GameRepository;
import com.cosmo.wanda_web.repositories.MatchRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.ChallengeException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ChallengeService {

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

    @Transactional
    public void challenge(ChallengeDTO dto) {
        User userChallenger = userService.authenticated(); // Usuário Logado

        Game game = gameRepository.findByNameIgnoreCase(dto.getGameName()).orElseThrow(
                () -> new ResourceNotFoundException("O jogo nao foi encontrado!")
        );

        User userChallenged = userRepository.findById(dto.getChallengedId()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário desafiado não foi encontrado"));

        // Verificar se há um desafio pendente entre esses dois alunos
        if (challengeRepository.checkIfChallengePendingExists(
                userChallenger.getId(), userChallenged.getId(), game.getId()).isPresent()) {
            throw new ChallengeException("Já existe um desafio pendente para este jogo!");
        }

        Challenge challenge = new Challenge();
        challenge.setChallenger(userChallenger);
        challenge.setChallenged(userChallenged);
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setMatch(null);
        challenge.setStatus(ChallengeStatus.PENDING);
        challenge.setGame(game);

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
        if (!dto.getAccepted()){
            challengeUpdate(dto.getChallengeId(), ChallengeStatus.DECLINED, null);
            return null;
        }
        Long result = matchOrchestrator.run(challenge.getChallenger().getId(), challenge.getChallenged().getId(), challenge.getGame());
        Match match = matchRepository.getReferenceById(result);
        challengeUpdate(dto.getChallengeId(), ChallengeStatus.ACCEPTED, match);
        return result;
    }

    @Transactional
    private void challengeUpdate(Long challengeId, ChallengeStatus status, Match match) {
        challengeRepository.updateChallenge(challengeId, status, match);
    }
}
