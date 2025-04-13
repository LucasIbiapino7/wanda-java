package com.cosmo.wanda_web.services;

import com.cosmo.wanda_web.dto.challengers.ChallengeDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeFIndAllPendingDTO;
import com.cosmo.wanda_web.dto.challengers.ChallengeIsAcceptedDTO;
import com.cosmo.wanda_web.dto.match.PlayedMatchDTO;
import com.cosmo.wanda_web.entities.Challenge;
import com.cosmo.wanda_web.entities.ChallengeStatus;
import com.cosmo.wanda_web.entities.User;
import com.cosmo.wanda_web.projections.FindAllPendingChallengerProjection;
import com.cosmo.wanda_web.repositories.ChallengeRepository;
import com.cosmo.wanda_web.repositories.UserRepository;
import com.cosmo.wanda_web.services.exceptions.ChallengeException;
import com.cosmo.wanda_web.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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

    @Transactional
    public void challenge(ChallengeDTO dto) {
        User userChallenger = userService.authenticated(); // Usuário Logado

        User userChallenged = userRepository.findById(dto.getChallengedId()).orElseThrow(
                () -> new ResourceNotFoundException("Usuário desafiado não foi encontrado"));

        // Verificar se há um desafio pendente entre esses dois alunos
        if (challengeRepository.checkIfChallengePendingExists(userChallenger.getId(), userChallenged.getId()).isPresent()){
            throw new ChallengeException("Já existe um desafio pendente!"); // Mudar essa exceção
        }

        Challenge challenge = new Challenge();
        challenge.setChallenger(userChallenger);
        challenge.setChallenged(userChallenged);
        challenge.setCreatedAt(Instant.now());
        challenge.setMatch(null);
        challenge.setStatus(ChallengeStatus.PENDING);

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
        System.out.println("dto:" + dto.getAccepted());
        Challenge challenge = challengeRepository.findById(dto.getChallengeId()).orElseThrow(
                () -> new ResourceNotFoundException("esse Challenge não existe"));
        if (!dto.getAccepted()){
            System.out.println("caiu aqui");
            challengeUpdate(dto.getChallengeId(), ChallengeStatus.DECLINED);
            return null;
        }
        Long result = matchService.RunMatch(
                new PlayedMatchDTO(challenge.getChallenger().getId(), challenge.getChallenged().getId()));
        challengeUpdate(dto.getChallengeId(), ChallengeStatus.ACCEPTED);
        return result;
    }

    @Transactional
    private void challengeUpdate(Long challengeId, ChallengeStatus status) {
        challengeRepository.rejectedChallenge(challengeId, status);
    }
}
